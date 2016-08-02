package com.marklogic.kinesis;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.marklogic.kinesis.config.KinesisConnectorForMarkLogicConfiguration;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.apache.log4j.Logger;

public abstract class KinesisConnectorExecutor<T, U> extends KinesisConnectorExecutorBase<T, U> {
	private static final Logger LOG = Logger.getLogger(KinesisConnectorExecutor.class.getName());
	protected final KinesisConnectorForMarkLogicConfiguration config;
	private final Properties properties;

	public KinesisConnectorExecutor(String configFile) {
		InputStream configStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(configFile);
		if (configStream == null) {
			String msg = "Could not find resource " + configFile + " in the classpath";
			throw new IllegalStateException(msg);
		}
		this.properties = new Properties();
		try {
			this.properties.load(configStream);
			configStream.close();
		} catch (IOException e) {
			String msg = "Could not load properties file " + configFile + " from classpath";
			throw new IllegalStateException(msg, e);
		}
		this.config = new KinesisConnectorForMarkLogicConfiguration(this.properties, getAWSCredentialsProvider());

		super.initialize(this.config);
	}

	public AWSCredentialsProvider getAWSCredentialsProvider() {
		return new ProfileCredentialsProvider();
	}

	private static boolean parseBoolean(String property, boolean defaultValue, Properties properties) {
		return Boolean.parseBoolean(properties.getProperty(property, Boolean.toString(defaultValue)));
	}
}
