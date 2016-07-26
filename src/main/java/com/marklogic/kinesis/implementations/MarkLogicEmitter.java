package com.marklogic.kinesis.implementations;

import com.amazonaws.services.kinesis.connectors.KinesisConnectorConfiguration;
import com.amazonaws.services.kinesis.connectors.UnmodifiableBuffer;
import com.amazonaws.services.kinesis.connectors.interfaces.IEmitter;
import com.marklogic.kinesis.MarkLogicSender;
import com.marklogic.kinesis.config.KinesisConnectorForMarkLogicConfiguration;
import com.marklogic.kinesis.model.MarkLogicMessageModel;
import java.io.IOException;
import java.util.List;
import org.apache.log4j.Logger;

public class MarkLogicEmitter
  implements IEmitter<MarkLogicMessageModel>
{
  private static final Logger LOG = Logger.getLogger(MarkLogicEmitter.class.getName());
  private MarkLogicSender sender;
  private KinesisConnectorForMarkLogicConfiguration config;
  private static final boolean SEND_RECORDS_IN_BATCHES = true;
  private long batchSize = 1000L;
  
  public MarkLogicEmitter(KinesisConnectorConfiguration configuration)
  {
    this.config = ((KinesisConnectorForMarkLogicConfiguration)configuration);
    this.sender = new MarkLogicSender(this.config);
    this.batchSize = this.config.BUFFER_RECORD_COUNT_LIMIT;
  }
  
  public List<MarkLogicMessageModel> emit(UnmodifiableBuffer<MarkLogicMessageModel> buffer)
    throws IOException
  {
    List<MarkLogicMessageModel> records = buffer.getRecords();
    return sendRecords(records);
  }
  
  private List<MarkLogicMessageModel> sendRecords(List<MarkLogicMessageModel> records)
  {
    return this.sender.sendToMarkLogic(records);
  }
  
  public void fail(List<MarkLogicMessageModel> records)
  {
    for (MarkLogicMessageModel record : records) {
      LOG.error("Could not emit record: " + record);
    }
  }
  
  public void shutdown() {}
}
