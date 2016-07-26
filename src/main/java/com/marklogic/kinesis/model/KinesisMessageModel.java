package com.marklogic.kinesis.model;

import java.io.Serializable;

/**
 * KinesisMessageModel a document ready to be inserted into MarkLogic 
 */
public class KinesisMessageModel
  implements Serializable
{
  private byte[] data;
  
  public byte[] getData()
  {
    return this.data;
  }
  
  public String getDataAsString()
  {
    return this.data.toString();
  }
  
  public void setData(byte[] data)
  {
    this.data = data;
  }
}