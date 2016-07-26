package com.marklogic.kinesis;

import com.amazonaws.services.kinesis.clientlibrary.exceptions.InvalidStateException;
import com.amazonaws.services.kinesis.clientlibrary.exceptions.KinesisClientLibDependencyException;
import com.amazonaws.services.kinesis.clientlibrary.exceptions.ShutdownException;
import com.amazonaws.services.kinesis.clientlibrary.exceptions.ThrottlingException;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorCheckpointer;
import com.amazonaws.services.kinesis.clientlibrary.types.ShutdownReason;
import com.amazonaws.services.kinesis.connectors.KinesisConnectorConfiguration;
import com.amazonaws.services.kinesis.connectors.UnmodifiableBuffer;
import com.amazonaws.services.kinesis.connectors.interfaces.IBuffer;
import com.amazonaws.services.kinesis.connectors.interfaces.ICollectionTransformer;
import com.amazonaws.services.kinesis.connectors.interfaces.IEmitter;
import com.amazonaws.services.kinesis.connectors.interfaces.IFilter;
import com.amazonaws.services.kinesis.connectors.interfaces.ITransformer;
import com.amazonaws.services.kinesis.connectors.interfaces.ITransformerBase;
import com.amazonaws.services.kinesis.model.Record;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class KinesisConnectorRecordProcessor<T, U>
  implements IRecordProcessor
{
  private static final Log LOG = LogFactory.getLog(KinesisConnectorRecordProcessor.class);
  private final IEmitter<U> emitter;
  private final ITransformerBase<T, U> transformer;
  private final IFilter<T> filter;
  private final IBuffer<T> buffer;
  private final int retryLimit;
  private final long backoffInterval;
  private boolean isShutdown = false;
  private String shardId;
  private static final long BACKOFF_TIME_IN_MILLIS = 3000L;
  private static final int NUM_RETRIES = 10;
  private static final long CHECKPOINT_INTERVAL_MILLIS = 60000L;
  private long nextCheckpointTimeInMillis;
  
  public KinesisConnectorRecordProcessor(IBuffer<T> buffer, IFilter<T> filter, IEmitter<U> emitter, ITransformerBase<T, U> transformer, KinesisConnectorConfiguration configuration)
  {
    if ((buffer == null) || (filter == null) || (emitter == null) || (transformer == null)) {
      throw new IllegalArgumentException("buffer, filter, emitter, transformer");
    }
    this.buffer = buffer;
    this.filter = filter;
    this.emitter = emitter;
    this.transformer = transformer;
    if (configuration.RETRY_LIMIT <= 0) {
      this.retryLimit = 1;
    } else {
      this.retryLimit = configuration.RETRY_LIMIT;
    }
    this.backoffInterval = configuration.BACKOFF_INTERVAL;
  }
  
  public void initialize(String shardId)
  {
    LOG.info("Initializing record processor for shard: " + shardId);
    this.shardId = shardId;
  }
  
  private void filterAndBufferRecord(T transformedRecord, Record record)
  {
    if (this.filter.keepRecord(transformedRecord)) {
      this.buffer.consumeRecord(transformedRecord, record.getData().array().length, record.getSequenceNumber());
    }
  }
  
  public void processRecords(List<Record> records, IRecordProcessorCheckpointer checkpointer)
  {
    if (this.isShutdown)
    {
      LOG.warn("processRecords called on shutdown record processor for shardId: " + this.shardId);
      return;
    }
    if (this.shardId == null) {
      throw new IllegalStateException("Record processor not initialized");
    }
    for (Record record : records) {
      try
      {
        if ((this.transformer instanceof ITransformer))
        {
          ITransformer<T, U> singleTransformer = (ITransformer)this.transformer;
          filterAndBufferRecord(singleTransformer.toClass(record), record);
        }
        else if ((this.transformer instanceof ICollectionTransformer))
        {
          ICollectionTransformer<T, U> listTransformer = (ICollectionTransformer)this.transformer;
          Collection<T> transformedRecords = listTransformer.toClass(record);
          for (T transformedRecord : transformedRecords) {
            filterAndBufferRecord(transformedRecord, record);
          }
        }
        else
        {
          throw new RuntimeException("Transformer must implement ITransformer or ICollectionTransformer");
        }
      }
      catch (IOException e)
      {
        LOG.error(e);
      }
    }
    if (this.buffer.shouldFlush())
    {
      LOG.info("Flushing buffer");
      Object emitItems = transformToOutput(this.buffer.getRecords());
      emit(checkpointer, (List)emitItems);
    }
    if (System.currentTimeMillis() > this.nextCheckpointTimeInMillis)
    {
      checkpoint(checkpointer);
      this.nextCheckpointTimeInMillis = (System.currentTimeMillis() + 60000L);
    }
  }
  
  private List<U> transformToOutput(List<T> items)
  {
    List<U> emitItems = new ArrayList();
    for (T item : items) {
      try
      {
        emitItems.add(this.transformer.fromClass(item));
      }
      catch (IOException e)
      {
        LOG.error("Failed to transform record " + item + " to output type", e);
      }
    }
    return emitItems;
  }
  
  private void emit(IRecordProcessorCheckpointer checkpointer, List<U> emitItems)
  {
    List<U> unprocessed = new ArrayList(emitItems);
    try
    {
      for (int numTries = 0; numTries < this.retryLimit; numTries++)
      {
        unprocessed = this.emitter.emit(new UnmodifiableBuffer(this.buffer, unprocessed));
        if (unprocessed.isEmpty()) {
          break;
        }
        try
        {
          Thread.sleep(this.backoffInterval);
        }
        catch (InterruptedException localInterruptedException) {}
      }
      if (!unprocessed.isEmpty()) {
        this.emitter.fail(unprocessed);
      }
      String lastSequenceNumberProcessed = this.buffer.getLastSequenceNumber();
      LOG.info("lastSequenceNumberProcessed = " + lastSequenceNumberProcessed);
      this.buffer.clear();
      if (lastSequenceNumberProcessed != null) {
        checkpointer.checkpoint(lastSequenceNumberProcessed);
      }
    }
    catch (IOException|KinesisClientLibDependencyException|InvalidStateException|ThrottlingException|ShutdownException e)
    {
      LOG.error(e);
      this.emitter.fail(unprocessed);
    }
  }
  
  public void shutdown(IRecordProcessorCheckpointer checkpointer, ShutdownReason reason)
  {
    LOG.info("Shutting down record processor for shard: " + this.shardId);
    if (reason == ShutdownReason.TERMINATE) {
      checkpoint(checkpointer);
    }
  }
  
  private void checkpoint(IRecordProcessorCheckpointer checkpointer)
  {
    LOG.info("Checkpointing shard " + this.shardId);
    for (int i = 0; i < 10; i++)
    {
      try
      {
        checkpointer.checkpoint();
      }
      catch (ShutdownException se)
      {
        LOG.info("Caught shutdown exception, skipping checkpoint.", se);
      }
      catch (ThrottlingException e)
      {
        if (i >= 9)
        {
          LOG.error("Checkpoint failed after " + (i + 1) + "attempts.", e);
          break;
        }
        LOG.info("Transient issue when checkpointing - attempt " + (i + 1) + " of " + 10, e);
      }
      catch (InvalidStateException e)
      {
        LOG.error("Cannot save checkpoint to the DynamoDB table used by the Amazon Kinesis Client Library.", e);
      }
      try
      {
        Thread.sleep(3000L);
      }
      catch (InterruptedException e)
      {
        LOG.debug("Interrupted sleep", e);
      }
    }
  }
}
