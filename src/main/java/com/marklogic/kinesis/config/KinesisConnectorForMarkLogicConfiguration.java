package com.marklogic.kinesis.config;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.kinesis.connectors.KinesisConnectorConfiguration;
import com.marklogic.kinesis.MarkLogicJavaClientManager;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 * KinesisConnectorForMarkLogicConfiguration
 */
public class KinesisConnectorForMarkLogicConfiguration
  extends KinesisConnectorConfiguration
{
  private static final Logger LOG = Logger.getLogger(KinesisConnectorForMarkLogicConfiguration.class.getName());
  private static final String PROP_MARKLOGIC_HOST = "marklogic.host";
  private static final String PROP_MARKLOGIC_PORT = "marklogic.port";
  private static final String PROP_MARKLOGIC_USER = "marklogic.user";
  private static final String PROP_MARKLOGIC_PASSWORD = "marklogic.user.password";
  private static final String PROP_TRANSFORMER_CLASS = "marklogic.transformer.class";
  private static final String PROP_KINESIS_STREAM_DOC_TYPE = "kinesisInputStream.documenttype";
  private static final String PROP_MARKLOGIC_INBOUND_COLLECTION = "markLogic.inbound.collection";
  private static final String PROP_MARKLOGIC_INBOUND_BASE_URI = "marklogic.inbound.baseURI";
  private static final String PROP_MARKLOGIC_TEST_DETAILS = "marklogic.test.connection";
  private static final String PROP_MARKLOGIC_TEST_DOC_URI = "marklogic.test.connection.uri";
  private static final String DEFAULT_MARKLOGIC_HOST = "localhost";
  private static final String DEFAULT_KINESIS_STREAM_DOC_TYPE = "TEXT";
  private static final String DEFAULT_MARKLOGIC_PORT = "8000";
  private static final String DEFAULT_TRANSFORMER_CLASS = "MarkLogicKinesisMessageModelTransformer";
  private static final String DEFAULT_INBOUND_COLLECTION = "InboundFromKinesis";
  private static final String DEFAULT_INBOUND_BASE_URI = "/kinesis/";
  private static final String DEFAULT_TEST_MARKLOGIC_DETAILS = "true";
  private static final String DEFAULT_TEST_MARKLOGIC_DOC_URI = "kinesis_test.doc";
  private static final String DEFAULT_EMPTY = "";
  public final String MARKLOGIC_HOST;
  public final int MARKLOGIC_PORT;
  public final String MARKLOGIC_USER;
  public final String MARKLOGIC_PASSWORD;
  public final String TRANSFORMER_CLASS;
  public final String KINESIS_STREAM_DOC_TYPE;
  public final String MARKLOGIC_INBOUND_COLLECTION;
  public final String MARKLOGIC_INBOUND_BASE_URI;
  public final boolean MARKLOGIC_TEST_CONNECTION;
  public final String MARKLOGIC_TEST_CONNECTION_DOC_URI;
  
  public KinesisConnectorForMarkLogicConfiguration(Properties properties, AWSCredentialsProvider credentialsProvider)
  {
    super(properties, credentialsProvider);
    this.MARKLOGIC_HOST = properties.getProperty("marklogic.host", "localhost");
    this.MARKLOGIC_USER = properties.getProperty("marklogic.user", "");
    this.MARKLOGIC_PASSWORD = properties.getProperty("marklogic.user.password", "");
    this.MARKLOGIC_PORT = Integer.valueOf(properties.getProperty("marklogic.port", "8000")).intValue();
    this.TRANSFORMER_CLASS = properties.getProperty("marklogic.transformer.class", "MarkLogicKinesisMessageModelTransformer");
    this.MARKLOGIC_INBOUND_COLLECTION = properties.getProperty("markLogic.inbound.collection", "InboundFromKinesis");
    this.MARKLOGIC_INBOUND_BASE_URI = properties.getProperty("marklogic.inbound.baseURI", "/kinesis/");
    String streamDocType = properties.getProperty("kinesisInputStream.documenttype", "TEXT");
    streamDocType = streamDocType.trim();
    
    this.MARKLOGIC_TEST_CONNECTION_DOC_URI = properties.getProperty("marklogic.test.connection.uri", "kinesis_test.doc");
    if (properties.getProperty("marklogic.test.connection", "true").equalsIgnoreCase("true") || properties.getProperty("marklogic.test.connection", "true").equalsIgnoreCase("Y")) {
      this.MARKLOGIC_TEST_CONNECTION = true;
    } else {
      this.MARKLOGIC_TEST_CONNECTION = false;
    }
    try
    {
      MarkLogicJavaClientManager.DocumentType.valueOf(streamDocType);
    }
    catch (Exception e)
    {
      StringBuffer sb = new StringBuffer();
      for (MarkLogicJavaClientManager.DocumentType dt : MarkLogicJavaClientManager.DocumentType.values()) {
        sb.append(dt.toString() + " ");
      }
      LOG.warn(streamDocType + " is not a valid setting for " + "kinesisInputStream.documenttype" + " Must be one of: " + sb.toString());
      LOG.warn("Defaulting kinesisInputStream.documenttype to TEXT");
      streamDocType = "TEXT";
    }
    this.KINESIS_STREAM_DOC_TYPE = streamDocType;
    LOG.info("Reading " + this.KINESIS_STREAM_DOC_TYPE + " documents from Kinesis stream " + this.KINESIS_INPUT_STREAM);
  }
}
