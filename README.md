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

## Configuration

The MarkLogicConnector.properties file should be modified for the Kinesis Stream(**kinesisInputStream**, **regionName**), destination MarkLogic database/cluster details (**marklogic.host**, **marklogic.port**, **marklogic.user**), App Name (**appName**)

## Building the Connector

1. Execute **ant setup**  Downloads the Ivy dependency management jar and get the project ready

2. Execute **ant build** Builds the project (download dependencies, compile, package into jar)

When this completes the **build** directory should have been created containing **marklogic-kinesis.jar** and two sub-directories:

**config** directory containing **MarkLogic.properties** and **log4j.properties**

**lib** directory containing dependency jars

## Running the Connector

Set up your AWS credentials in ~/.aws/credentials

Modify **MarkLogicConnector.properties** with the appropriate Kinesis stream details: **kinesisInputStream** and **regionName** as well as setting the type of data which will be coming through the Kinesis stream in **kinesisInputStream.documenttype**

Set **marklogic.host**, **marklogic.port**, **marklogic.user** and **marklogic.user.password** for the MarkLogic database.

Optional: Change **markLogic.inbound.collection** to the name of the collection documents will be added to (default is fromKinesis)

(Ensure the user exists in MarkLogic and has **rest-writer** privileges to write to the MarkLogic REST API)

See **MarkLogicConnector.properties** for additional options (**markLogic.inbound.collection**, **marklogic.inbound.baseURI** etc)

From ANT run the connector with: **ant run**

From command line or script run the connector with: **java -jar marklogic-kinesis.jar**

## Related

[MarkLogic] (http://marklogic.com)

[MarkLogic Java Client] (https://developer.marklogic.com/products/java)

[Amazon Kinesis](http://aws.amazon.com/kinesis/)

[Ant](http://ant.apache.org/)

[AWS Kinesis Account](http://aws.amazon.com/account/)

[Kinesis Client Library](https://github.com/awslabs/amazon-kinesis-client/)
