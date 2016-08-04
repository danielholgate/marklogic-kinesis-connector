# marklogic-kinesis-connector
Connector from AWS Kinesis streams to MarkLogic database

**marklogic-kinesis-connector** acts as a pipeline between an Amazon Kinesis stream and a MarkLogic database. Data can be sent from Kinesis directly to MarkLogic as XML, JSON, or free/unstructured text documents where it will be automatically indexed and available for use in your applications for analysis and processing. The connector uses the MarkLogic Java Client and can run in AWS or locally to pull data from Kinesis.

## Requirements

 + **Java JDK 1.7**: This connector has been built with Java version 1.7.
 + **Ant**: A build.xml script is provided to build the connector with Ant.
 + **AWS Kinesis Account**: An Amazon AWS Kinesis account to use as a source of data.
 + **MarkLogic database**: A MarkLogic database or cluster which will be the destination for data from Kinesis. 

## Overview

Incoming records from one (or many) shards of an AWS Kinesis Stream will be written to MarkLogic, into a collection if you choose. MarkLogic will ingest and automatically index the documents and make them available for immediate use in your applications.

## Building the Connector

cd to the project directory and:

1. Execute **ant setup**  
   Downloads the dependency management jar and gets the project ready

2. Execute **ant build**
    
   Builds the project (download dependencies, compile, package into jar)

When this completes the **build** directory should have been created containing **marklogic-kinesis.jar** and two sub-directories:

**config** directory containing **MarkLogic.properties** and **log4j.properties**

**lib** directory containing dependency jars

## Configuring the Connector

1. Set up your AWS credentials in ~/.aws/credentials

2. Modify build/[MarkLogicConnector.properties](./MarkLogicConnector.properties):
 
**kinesisInputStream**   Name of the Kinesis stream

 **regionName** The AWS region the stream is in

 **kinesisInputStream.documenttype**  The type of data coming through the stream (JSON, XML, TEXT)

 **marklogic.host**  MarkLogic database host

 **marklogic.port**  MarkLogic application server host port

 **marklogic.user**  MarkLogic database user (Ensure the user exists in MarkLogic and has rest-writer privileges to write REST API)

 **marklogic.user.password**  User password

See **MarkLogicConnector.properties** for additional options.

## Running the Connector

From ANT run the connector with: **ant run**

From command line or script run the connector with: **java -jar marklogic-kinesis.jar**

## Related

[MarkLogic] (http://marklogic.com)

[MarkLogic Java Client] (https://developer.marklogic.com/products/java)

[Amazon Kinesis](http://aws.amazon.com/kinesis/)

[Ant](http://ant.apache.org/)

[AWS Kinesis Account](http://aws.amazon.com/account/)

[Kinesis Client Library](https://github.com/awslabs/amazon-kinesis-client/)
