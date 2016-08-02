package com.marklogic.kinesis;

import com.amazonaws.services.kinesis.clientlibrary.lib.worker.KinesisClientLibConfiguration;
import com.amazonaws.services.kinesis.clientlibrary.lib.worker.Worker;
import com.amazonaws.services.kinesis.connectors.KinesisConnectorConfiguration;
import com.amazonaws.services.kinesis.metrics.impl.NullMetricsFactory;
import com.amazonaws.services.kinesis.metrics.interfaces.IMetricsFactory;
import org.apache.log4j.Logger;

public abstract class KinesisConnectorExecutorBase<T, U> implements Runnable {
	private static final Logger LOG = Logger.getLogger(KinesisConnectorExecutorBase.class.getName());
	protected Worker worker;

	protected void initialize(KinesisConnectorConfiguration kinesisConnectorConfiguration) {
		initialize(kinesisConnectorConfiguration, new NullMetricsFactory());
	}

	protected void initialize(KinesisConnectorConfiguration kinesisConnectorConfiguration,
			IMetricsFactory metricFactory) {
		KinesisClientLibConfiguration kinesisClientLibConfiguration = new KinesisClientLibConfiguration(
				kinesisConnectorConfiguration.APP_NAME, kinesisConnectorConfiguration.KINESIS_INPUT_STREAM,
				kinesisConnectorConfiguration.AWS_CREDENTIALS_PROVIDER, kinesisConnectorConfiguration.WORKER_ID)
						.withKinesisEndpoint(kinesisConnectorConfiguration.KINESIS_ENDPOINT)
						.withFailoverTimeMillis(kinesisConnectorConfiguration.FAILOVER_TIME)
						.withMaxRecords(kinesisConnectorConfiguration.MAX_RECORDS)
						.withInitialPositionInStream(kinesisConnectorConfiguration.INITIAL_POSITION_IN_STREAM)
						.withIdleTimeBetweenReadsInMillis(kinesisConnectorConfiguration.IDLE_TIME_BETWEEN_READS)
						.withCallProcessRecordsEvenForEmptyRecordList(true)
						.withCleanupLeasesUponShardCompletion(
								kinesisConnectorConfiguration.CLEANUP_TERMINATED_SHARDS_BEFORE_EXPIRY)
						.withParentShardPollIntervalMillis(kinesisConnectorConfiguration.PARENT_SHARD_POLL_INTERVAL)
						.withShardSyncIntervalMillis(kinesisConnectorConfiguration.SHARD_SYNC_INTERVAL)
						.withTaskBackoffTimeMillis(kinesisConnectorConfiguration.BACKOFF_INTERVAL)
						.withMetricsBufferTimeMillis(kinesisConnectorConfiguration.CLOUDWATCH_BUFFER_TIME)
						.withMetricsMaxQueueSize(kinesisConnectorConfiguration.CLOUDWATCH_MAX_QUEUE_SIZE)
						.withUserAgent(kinesisConnectorConfiguration.APP_NAME + ","
								+ kinesisConnectorConfiguration.CONNECTOR_DESTINATION + ","
								+ "amazon-kinesis-connector-java-1.2.0")
						.withRegionName(kinesisConnectorConfiguration.REGION_NAME);
		if (!kinesisConnectorConfiguration.CALL_PROCESS_RECORDS_EVEN_FOR_EMPTY_LIST) {
			LOG.warn(
					"The false value of callProcessRecordsEvenForEmptyList will be ignored. It must be set to true for the bufferTimeMillisecondsLimit to work correctly.");
		}
		if (kinesisConnectorConfiguration.IDLE_TIME_BETWEEN_READS > kinesisConnectorConfiguration.BUFFER_MILLISECONDS_LIMIT) {
			LOG.warn(
					"idleTimeBetweenReads is greater than bufferTimeMillisecondsLimit. For best results, ensure that bufferTimeMillisecondsLimit is more than or equal to idleTimeBetweenReads ");
		}
		if (metricFactory != null) {
			this.worker = new Worker(getKinesisConnectorRecordProcessorFactory(), kinesisClientLibConfiguration,
					metricFactory);
		} else {
			this.worker = new Worker(getKinesisConnectorRecordProcessorFactory(), kinesisClientLibConfiguration);
		}
		LOG.info(getClass().getSimpleName() + " worker created");
	}

	public void run() {
		if (this.worker != null) {
			LOG.info("Starting worker");
			try {
				this.worker.run();
			} catch (Throwable t) {
				LOG.fatal(t);
				throw t;
			} finally {
				LOG.error("Worker " + getClass().getSimpleName() + " is not running.");
			}
		} else {
			LOG.fatal("Initialize must be called before run.");
			throw new RuntimeException("Initialize must be called before run.");
		}
	}

	public abstract KinesisConnectorRecordProcessorFactory<T, U> getKinesisConnectorRecordProcessorFactory();
}
