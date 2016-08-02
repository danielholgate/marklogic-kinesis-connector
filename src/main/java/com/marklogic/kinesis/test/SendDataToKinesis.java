package com.marklogic.kinesis.test;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.amazonaws.services.kinesis.model.PutRecordsRequest;
import com.amazonaws.services.kinesis.model.PutRecordsRequestEntry;
import com.amazonaws.services.kinesis.model.PutRecordsResult;
import com.amazonaws.services.kinesis.model.PutRecordsResultEntry;
<<<<<<< HEAD
import com.amazonaws.regions.Region;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.EnumSet;
=======
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
>>>>>>> c7ff54cc6aec1edeb85949dcb106226e3f5f81d4
import java.util.List;

public class SendDataToKinesis
{
<<<<<<< HEAD
  final static String APPLICATION_NAME = "marklogic-stream-test";
  private enum DOC_TYPE { TEXT, JSON, XML }
  static private DOC_TYPE docType = null; // document type of messages which will be put into the Kinesis stream
  final private static EnumSet<DOC_TYPE> docTypes = EnumSet.allOf( DOC_TYPE.class );
  final static int DEFAULT_NUMBER = 50;
=======
  static String STREAM_NAME = "KinesisToMarkLogic";
  static String END_POINT = "https://kinesis.ap-southeast-2.amazonaws.com";
  static String APPLICATION_NAME = "marklogic-stream-test";
  private enum DOC_TYPE { TEXT, JSON, XML }
  static private DOC_TYPE docType = null; // document type of mesages which will be put into the kinesis stream
>>>>>>> c7ff54cc6aec1edeb85949dcb106226e3f5f81d4
  
  //
  // Test class to put messages in Kinesis stream.
  // 
  public static void main(String[] args)
  {
<<<<<<< HEAD
	  
    AWSCredentialsProvider credentialsProvider = null;
    String streamName = "";
    String regionName = "";
    int recordsToInsert = DEFAULT_NUMBER;
    
=======
    AWSCredentialsProvider credentialsProvider = null;
    int recordsToInsert = 50;
>>>>>>> c7ff54cc6aec1edeb85949dcb106226e3f5f81d4
    try
    {
      credentialsProvider = getCredentials();
    }
    catch (AmazonClientException e)
    {
      System.out.println("Unable to obtain credentials. Exiting: " + e.getMessage());
      return;
    }
    
<<<<<<< HEAD
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
=======
    if (args.length == 0) {
    	System.out.println("No arguments supplied. Defaulting to JSON");
    	docType = DOC_TYPE.JSON;
    }
    
    if (args.length == 1) {
    	try {
    		docType = DOC_TYPE.valueOf(args[0]);
    	} catch (IllegalArgumentException e) {
    		System.out.println("Document type must be one of: " + DOC_TYPE.values() );
    		return;
    	}
    }

     else if ( args.length == 2 ) {
    	 try {
     		docType = DOC_TYPE.valueOf(args[0]);
     	} catch (IllegalArgumentException e) {
     		System.out.println("Document type must be one of: " + DOC_TYPE.values() );
     		return;
     	}
    		STREAM_NAME = args[1];
    	}
     else if ( args.length == 3 ) {
    	 
    }
 
    produce(docType, recordsToInsert, credentialsProvider);
>>>>>>> c7ff54cc6aec1edeb85949dcb106226e3f5f81d4
    
    System.out.println("Finished");
  }
  
<<<<<<< HEAD
  static AmazonKinesisClient getKinesisClient(AWSCredentialsProvider credentialsProvider, String regionN)
  {
    AmazonKinesisClient amazonKinesisClient = null;
    amazonKinesisClient = new AmazonKinesisClient(credentialsProvider);
    amazonKinesisClient.setRegion(Region.getRegion(com.amazonaws.regions.Regions.fromName(regionN)));
    return amazonKinesisClient;
  }
  
  static void produce(DOC_TYPE doctype, int recordsCount, String stream, String region, AWSCredentialsProvider credentialsProvider)
=======
  static AmazonKinesisClient getKinesisClient(AWSCredentialsProvider credentialsProvider)
  {
    AmazonKinesisClient amazonKinesisClient = null;
    amazonKinesisClient = new AmazonKinesisClient(credentialsProvider);
    amazonKinesisClient.setEndpoint(END_POINT);
    return amazonKinesisClient;
  }
  
  static void produce(DOC_TYPE doctype, int recordsCount, AWSCredentialsProvider credentialsProvider)
>>>>>>> c7ff54cc6aec1edeb85949dcb106226e3f5f81d4
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
    
<<<<<<< HEAD
    AmazonKinesisClient amazonKinesisClient = getKinesisClient(credentialsProvider,region);
=======
    AmazonKinesisClient amazonKinesisClient = getKinesisClient(credentialsProvider);
>>>>>>> c7ff54cc6aec1edeb85949dcb106226e3f5f81d4
    
    amazonKinesisClient.listStreams();
    
    PutRecordsRequest putRecordsRequest = new PutRecordsRequest();
<<<<<<< HEAD
    putRecordsRequest.setStreamName(stream);
=======
    putRecordsRequest.setStreamName(STREAM_NAME);
>>>>>>> c7ff54cc6aec1edeb85949dcb106226e3f5f81d4
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
<<<<<<< HEAD
      System.out.println("Sucessfully added " + recordsCount + " " + doctype.toString() + " messages to stream " + stream + " in region " + region);
    }
    else
    {
      System.out.println("Exception occurred adding messages to stream " + stream + ":");
=======
      System.out.println("Sucessfully added " + recordsCount + " messages to stream " + STREAM_NAME + " at endpoint " + END_POINT);
    }
    else
    {
      System.out.println("Exception occurred adding messages to stream " + STREAM_NAME + ":");
>>>>>>> c7ff54cc6aec1edeb85949dcb106226e3f5f81d4
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
<<<<<<< HEAD
  
=======
>>>>>>> c7ff54cc6aec1edeb85949dcb106226e3f5f81d4
}