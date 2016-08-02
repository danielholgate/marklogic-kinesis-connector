package com.marklogic.kinesis.test;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.amazonaws.services.kinesis.model.PutRecordsRequest;
import com.amazonaws.services.kinesis.model.PutRecordsRequestEntry;
import com.amazonaws.services.kinesis.model.PutRecordsResult;
import com.amazonaws.services.kinesis.model.PutRecordsResultEntry;
import com.amazonaws.regions.Region;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

public class SendDataToKinesis
{
  final static String APPLICATION_NAME = "marklogic-stream-test";
  private enum DOC_TYPE { TEXT, JSON, XML }
  static private DOC_TYPE docType = null; // document type of messages which will be put into the Kinesis stream
  final private static EnumSet<DOC_TYPE> docTypes = EnumSet.allOf( DOC_TYPE.class );
  final static int DEFAULT_NUMBER = 50;
  
  //
  // Test class to put messages in Kinesis stream.
  // 
  public static void main(String[] args)
  {
	  
    AWSCredentialsProvider credentialsProvider = null;
    String streamName = "";
    String regionName = "";
    int recordsToInsert = DEFAULT_NUMBER;
    
    try
    {
      credentialsProvider = getCredentials();
    }
    catch (AmazonClientException e)
    {
      System.out.println("Unable to obtain credentials. Exiting: " + e.getMessage());
      return;
    }
    
    if (args.length < 3) {
    	System.out.println("Arguments:  StreamName RegionName DocumentType [NumberOfDocuments]");
    	System.out.println("Valid options for DocumentType are: " + docTypes);
    	return;
    }
    
    streamName = args[0];
    regionName = args[1];

    	try {
    		docType = DOC_TYPE.valueOf(args[2]);
    	} catch (IllegalArgumentException e) {
    		System.out.println(docType + " is not valid. Document type must be one of: " + docTypes );
    		return;
    	}
    	
    	if (args.length == 4) recordsToInsert = Integer.valueOf(args[3]);
 
    produce(docType, recordsToInsert, streamName, regionName, credentialsProvider);
    
    System.out.println("Finished");
  }
  
  static AmazonKinesisClient getKinesisClient(AWSCredentialsProvider credentialsProvider, String regionN)
  {
    AmazonKinesisClient amazonKinesisClient = null;
    amazonKinesisClient = new AmazonKinesisClient(credentialsProvider);
    amazonKinesisClient.setRegion(Region.getRegion(com.amazonaws.regions.Regions.fromName(regionN)));
    return amazonKinesisClient;
  }
  
  static void produce(DOC_TYPE doctype, int recordsCount, String stream, String region, AWSCredentialsProvider credentialsProvider)
  {
    String xmlPayload = "<myxml><title>some message via kinesis</title><body>Lorem ipsum dolor sit amet, pellentesque wisi aliquam</body>/myxml>";
    String jsonPayload = "{\"phonetype\":\"N95\",\"cat\":\"WP\"}";
    String textPayload = "Lorem ipsum dolor sit amet, pellentesque wisi aliquam, id hac, vitae tellus ut lacinia, sed duis gravida tempor ab et neque";
    String filePath = "/Users/dholgate/Documents/development/photos/cert";
    
    /*
    byte[] binaryPayload = null;
    try
    {
      Path path = Paths.get(filePath, new String[0]);
      binaryPayload = Files.readAllBytes(path);
    }
    catch (Exception e)
    {
      System.out.println(e.getMessage());
    }
    byte[] finalPayLoad = textPayload.getBytes();
    */
    
    byte[] finalPayLoad = null;
    
    switch (doctype) {
    case XML:
    	finalPayLoad = xmlPayload.getBytes();
    	break;
    case JSON:
    	finalPayLoad = jsonPayload.getBytes();
    	break;
    case TEXT:
    	finalPayLoad = textPayload.getBytes();
    	break;
    default:
    	
    	break;

    }
    
    AmazonKinesisClient amazonKinesisClient = getKinesisClient(credentialsProvider,region);
    
    amazonKinesisClient.listStreams();
    
    PutRecordsRequest putRecordsRequest = new PutRecordsRequest();
    putRecordsRequest.setStreamName(stream);
    List<PutRecordsRequestEntry> putRecordsRequestEntryList = new ArrayList();
    PutRecordsRequestEntry putRecordsRequestEntry;
    for (int i = 0; i < recordsCount; i++)
    {
      putRecordsRequestEntry = new PutRecordsRequestEntry();
      putRecordsRequestEntry.setData(ByteBuffer.wrap(finalPayLoad));
      putRecordsRequestEntry.setPartitionKey(String.format("partitionKey-%d", new Object[] { Integer.valueOf(i) }));
      putRecordsRequestEntryList.add(putRecordsRequestEntry);
    }
    putRecordsRequest.setRecords(putRecordsRequestEntryList);
    PutRecordsResult putRecordsResult = amazonKinesisClient.putRecords(putRecordsRequest);
    if (putRecordsResult.getFailedRecordCount().intValue() == 0)
    {
      System.out.println("Sucessfully added " + recordsCount + " " + doctype.toString() + " messages to stream " + stream + " in region " + region);
    }
    else
    {
      System.out.println("Exception occurred adding messages to stream " + stream + ":");
      for (PutRecordsResultEntry entry : putRecordsResult.getRecords()) {
        if (entry.getErrorCode() != null) {
          System.out.println(entry.getErrorCode() + ":" + entry.getErrorMessage());
        }
      }
    }
  }
  
  static AWSCredentialsProvider getCredentials()
  {
    System.out.println("Obtaining credentials..");
    AWSCredentialsProvider credentialsProvider = new DefaultAWSCredentialsProviderChain();
    credentialsProvider.getCredentials();
    return credentialsProvider;
  }
  
}
