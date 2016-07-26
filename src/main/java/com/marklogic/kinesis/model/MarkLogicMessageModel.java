package com.marklogic.kinesis.model;

import java.io.Serializable;

/**
 * Represents a document ready to be inserted into MarkLogic 
 */
public class MarkLogicMessageModel
  implements Serializable
{
  private byte[] data;
  private String url;
  private String docType;
  
  public String getDocType()
  {
    return this.docType;
  }
  
  public void setDocType(String docType)
  {
    this.docType = docType;
  }
  
  public MarkLogicMessageModel(byte[] data)
  {
    this.data = data;
  }
  
  public MarkLogicMessageModel(String docType, String url, byte[] data)
  {
    this.data = data;
    setUrl(url);
  }
  
  public byte[] getData()
  {
    return this.data;
  }
  
  public String getDataAsString()
  {
    return new String(this.data);
  }
  
  public void setData(byte[] data)
  {
    this.data = data;
  }
  
  public String getUrl()
  {
    return this.url;
  }
  
  public void setUrl(String url)
  {
    this.url = url;
  }
}
