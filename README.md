# marklogic-kinesis-connector
Connector from AWS Kinesis streams to MarkLogic database

**marklogic-kinesis-connector** acts as a pipeline between an Amazon Kinesis stream and a MarkLogic database. Data can be sent from Kinesis directly to MarkLogic as XML, JSON, or free/unstructured text documents where it will be automatically indexed and available for use in applications for analysis and processing. The connector uses the MarkLogic Java Client and can run in AWS or locally to pull data from Kinesis.

## Requirements

 + **Java JDK 1.7**: This connector has been built with Java version 1.7.
 + **Ant**: A build.xml script is provided to build the connector with Ant.
 + **AWS Kinesis Account**: An Amazon AWS Kinesis account to use as a source of data.
 + **MarkLogic database**: The host, port, username,and password a MarkLogic database either in AWS or on premise.

## Overview

Incoming records from one (or many) shards of an AWS Kinesis Stream will be written to MarkLogic, optionally into a collection and using a specified URI base path.  By specifying the type of data coming through the Kinesis stream (XML, JSON, TEXT) MarkLogic will ingest and automatically index them and make them available for immediate use in your applications.

## Configuration

The MarkLogicConnector.properties file should be modified for the Kinesis Stream(**kinesisInputStream**, **regionName**), destination MarkLogic database/cluster details(**marklogic.host**, **marklogic.port**, **marklogic.user**), App Name (**appName**)

## Building the Connector

To download required dependencies and build the project execute **ant build**

When this completes the **build** directory should have been created containing **marklogic-kinesis.jar** and the following two sub-directories:

**config** directory containing **MarkLogic.properties** and **log4j.properties**

**lib** directory containing dependency jars

## Running the Connector

Set up your AWS credentials in ~/.aws/credentials

Modify **MarkLogicConnector.properties** with the appropriate Kinesis stream details: **kinesisInputStream** and **regionName** as well as setting the type of data which will be coming through the Kinesis stream in **kinesisInputStream.documenttype**

For MarkLogic set the **marklogic.host**, **marklogic.port**, **marklogic.user** and **marklogic.user.password**

Ensure the user has been created in MarkLogic and has rest-writer privileges to write to the MarkLogic REST api

Run the connector with **java -jar marklogic-kinesis.jar**

## Related

[Amazon Kinesis](http://aws.amazon.com/kinesis/)

[Ant](http://ant.apache.org/)

[AWS Kinesis Account](http://aws.amazon.com/account/)

[Kinesis Client Library](https://github.com/awslabs/amazon-kinesis-client/)
