package com.marklogic.kinesis;

import com.marklogic.kinesis.config.KinesisConnectorForMarkLogicConfiguration;
import com.marklogic.kinesis.model.MarkLogicMessageModel;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MarkLogicSender
{
  private static final Log LOG = LogFactory.getLog(MarkLogicSender.class);
  private MarkLogicJavaClientManager _client = null;
  private String _collection;
  
  public MarkLogicSender(KinesisConnectorForMarkLogicConfiguration config)
  {
    this._client = new MarkLogicJavaClientManager(config.MARKLOGIC_HOST, config.MARKLOGIC_PORT, config.MARKLOGIC_USER, config.MARKLOGIC_PASSWORD);
    
    this._collection = config.MARKLOGIC_INBOUND_COLLECTION;
    this._client.open();
  }
  
  public List<MarkLogicMessageModel> sendToMarkLogic(List<MarkLogicMessageModel> documents)
  {
    LOG.info("Received " + documents.size() + " documents from stream");
    List<MarkLogicMessageModel> failedDocuments = new ArrayList();
    for (MarkLogicMessageModel document : documents) {
      if (!sendToMarkLogic(document)) {
        failedDocuments.add(document);
      }
    }
    if (failedDocuments.size() > 0) {
      LOG.error("Failed to insert " + failedDocuments.size() + "/" + documents.size() + " documents into MarkLogic");
    } else {
      LOG.info("Inserted all " + documents.size() + " documents into MarkLogic");
    }
    return failedDocuments;
  }
  
  public boolean sendToMarkLogic(MarkLogicMessageModel document)
  {
    return this._client.insertDocument(document.getDocType(), this._collection, document.getUrl(), document.getData());
  }
  
}
