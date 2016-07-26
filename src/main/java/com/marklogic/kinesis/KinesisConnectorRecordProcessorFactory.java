package com.marklogic.kinesis;

import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorFactory;
import com.amazonaws.services.kinesis.connectors.KinesisConnectorConfiguration;
import com.amazonaws.services.kinesis.connectors.interfaces.IBuffer;
import com.amazonaws.services.kinesis.connectors.interfaces.IEmitter;
import com.amazonaws.services.kinesis.connectors.interfaces.IFilter;
import com.amazonaws.services.kinesis.connectors.interfaces.IKinesisConnectorPipeline;
import com.amazonaws.services.kinesis.connectors.interfaces.ITransformerBase;
import org.apache.log4j.Logger;

public class KinesisConnectorRecordProcessorFactory<T, U>
  implements IRecordProcessorFactory
{
  private static final Logger LOG = Logger.getLogger(KinesisConnectorRecordProcessorFactory.class.getName());
  private IKinesisConnectorPipeline<T, U> pipeline;
  private KinesisConnectorConfiguration configuration;
  
  public KinesisConnectorRecordProcessorFactory(IKinesisConnectorPipeline<T, U> pipeline, KinesisConnectorConfiguration configuration)
  {
    this.configuration = configuration;
    this.pipeline = pipeline;
  }
  
  public IRecordProcessor createProcessor()
  {
    try
    {
      IBuffer<T> buffer = this.pipeline.getBuffer(this.configuration);
      IEmitter<U> emitter = this.pipeline.getEmitter(this.configuration);
      ITransformerBase<T, U> transformer = this.pipeline.getTransformer(this.configuration);
      IFilter<T> filter = this.pipeline.getFilter(this.configuration);
      return new KinesisConnectorRecordProcessor(buffer, filter, emitter, transformer, this.configuration);
    }
    catch (Throwable t)
    {
      throw new RuntimeException(t);
    }
  }
}
