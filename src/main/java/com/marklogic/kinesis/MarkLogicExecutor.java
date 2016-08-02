package com.marklogic.kinesis;

import com.marklogic.kinesis.model.KinesisMessageModel;
import com.marklogic.kinesis.model.MarkLogicMessageModel;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class MarkLogicExecutor extends KinesisConnectorExecutor<KinesisMessageModel, MarkLogicMessageModel> {
	private static final Log LOG = LogFactory.getLog(MarkLogicExecutor.class);
	private static final String configFile = "MarkLogicConnector.properties";

	public MarkLogicExecutor(String configFile) {
		super(configFile);
	}

	public KinesisConnectorRecordProcessorFactory<KinesisMessageModel, MarkLogicMessageModel> getKinesisConnectorRecordProcessorFactory() {
		return new KinesisConnectorRecordProcessorFactory(new MarkLogicMessageModelPipeline(), this.config);
	}

	public static void main(String[] args) {
		LOG.info("Starting up Kinesis to MarkLogic connector");
		KinesisConnectorExecutor<KinesisMessageModel, MarkLogicMessageModel> marklogicExecutor = new MarkLogicExecutor(
				"MarkLogicConnector.properties");
		MarkLogicJavaClientManager _client = null;

		if (marklogicExecutor.config.MARKLOGIC_TEST_CONNECTION) {
			LOG.info("Testing connection to MarkLogic");
			_client = new MarkLogicJavaClientManager(marklogicExecutor.config.MARKLOGIC_HOST,
					marklogicExecutor.config.MARKLOGIC_PORT, marklogicExecutor.config.MARKLOGIC_USER,
					marklogicExecutor.config.MARKLOGIC_PASSWORD);
			_client.open();
			boolean result = _client.testConnection(marklogicExecutor.config.MARKLOGIC_TEST_CONNECTION_DOC_URI);
			_client.releaseClient();
			if (!result) {
				LOG.fatal("Failed to connect to MarkLogic. Please checking connection settings. Shutting down.");
			} else {
				LOG.info("Starting");
				marklogicExecutor.run();
			}
		} else {
			LOG.info("Starting");
			marklogicExecutor.run();
		}
		LOG.info("Finished.");
	}

}
