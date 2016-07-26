package com.marklogic.kinesis.transform;

import com.amazonaws.services.kinesis.connectors.interfaces.ITransformer;
import com.amazonaws.services.kinesis.model.Record;
import com.marklogic.kinesis.model.KinesisMessageModel;
import com.marklogic.kinesis.model.MarkLogicMessageModel;
import java.io.IOException;
import org.apache.log4j.Logger;

public class MarkLogicKinesisMessageModelTransformer
  implements ITransformer<KinesisMessageModel, MarkLogicMessageModel>
{
  private static final Logger LOG = Logger.getLogger(MarkLogicKinesisMessageModelTransformer.class.getName());
  private String _docType = "";
  private String _baseURI = "";
  
  public void setStreamDocumentType(String documentType)
  {
    this._docType = documentType;
  }
  
  public void setBaseURI(String baseURI)
  {
    this._baseURI = baseURI;
  }
  
  public KinesisMessageModel toClass(Record message)
    throws IOException
  {
    KinesisMessageModel kmm = new KinesisMessageModel();
    kmm.setData(message.getData().array());
    return kmm;
  }
  
  public MarkLogicMessageModel fromClass(KinesisMessageModel record)
    throws IOException
  {
    byte[] decodedRecord = record.getData();
    
    MarkLogicMessageModel mlMessage = new MarkLogicMessageModel(decodedRecord);
    
    String docSuffix = "";
    switch (this._docType)
    {
    case "XML": 
      docSuffix = ".xml";
      break;
    case "JSON": 
      docSuffix = ".json";
      break;
    case "BINARY": 
      docSuffix = "";
      break;
    case "TEXT": 
      docSuffix = ".txt";
      break;
    default: 
      docSuffix = ".default";
    }
    int randomNum = 0 + (int)(Math.random() * 9.99999999E8D);
    String docURI = this._baseURI + System.currentTimeMillis() + "/" + String.valueOf(randomNum) + docSuffix;
    mlMessage.setUrl(docURI);
    mlMessage.setDocType(this._docType);
    
    return mlMessage;
  }
}
