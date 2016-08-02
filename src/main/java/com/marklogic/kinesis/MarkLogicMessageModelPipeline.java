package com.marklogic.kinesis;

import com.amazonaws.services.kinesis.connectors.KinesisConnectorConfiguration;
import com.amazonaws.services.kinesis.connectors.impl.AllPassFilter;
import com.amazonaws.services.kinesis.connectors.impl.BasicMemoryBuffer;
import com.amazonaws.services.kinesis.connectors.interfaces.IBuffer;
import com.amazonaws.services.kinesis.connectors.interfaces.IEmitter;
import com.amazonaws.services.kinesis.connectors.interfaces.IFilter;
import com.amazonaws.services.kinesis.connectors.interfaces.IKinesisConnectorPipeline;
import com.amazonaws.services.kinesis.connectors.interfaces.ITransformer;
import com.marklogic.kinesis.config.KinesisConnectorForMarkLogicConfiguration;
import com.marklogic.kinesis.implementations.MarkLogicEmitter;
import com.marklogic.kinesis.model.KinesisMessageModel;
import com.marklogic.kinesis.model.MarkLogicMessageModel;
import com.marklogic.kinesis.transform.MarkLogicKinesisMessageModelTransformer;
import org.apache.log4j.Logger;

public class MarkLogicMessageModelPipeline
		implements IKinesisConnectorPipeline<KinesisMessageModel, MarkLogicMessageModel> {
	private static final Logger LOG = Logger.getLogger(MarkLogicMessageModelPipeline.class.getName());

	public IEmitter<MarkLogicMessageModel> getEmitter(KinesisConnectorConfiguration configuration) {
		return new MarkLogicEmitter(configuration);
	}

	public IBuffer<KinesisMessageModel> getBuffer(KinesisConnectorConfiguration configuration) {
		return new BasicMemoryBuffer(configuration);
	}

	public ITransformer<KinesisMessageModel, MarkLogicMessageModel> getTransformer(
			KinesisConnectorConfiguration configuration) {
		String className = "com.marklogic.kinesis.transform.MarkLogicKinesisMessageModelTransformer";
		ClassLoader classLoader = MarkLogicMessageModelPipeline.class.getClassLoader();
		Class ModelClass = null;
		ITransformer<KinesisMessageModel, MarkLogicMessageModel> ITransformerObject = null;
		try {
			ModelClass = classLoader.loadClass(className);
			ITransformerObject = (ITransformer) ModelClass.newInstance();
			((MarkLogicKinesisMessageModelTransformer) ITransformerObject).setStreamDocumentType(
					((KinesisConnectorForMarkLogicConfiguration) configuration).KINESIS_STREAM_DOC_TYPE);
			((MarkLogicKinesisMessageModelTransformer) ITransformerObject)
					.setBaseURI(((KinesisConnectorForMarkLogicConfiguration) configuration).MARKLOGIC_INBOUND_BASE_URI);

			LOG.info("Using transformer: " + ITransformerObject.getClass().getName());
			return ITransformerObject;
		} catch (ClassNotFoundException e) {
			LOG.error("Class not found: " + className + " error: " + e.getMessage());
		} catch (InstantiationException e) {
			LOG.error("Class not found: " + className + " error: " + e.getMessage());
		} catch (IllegalAccessException e) {
			LOG.error("Class not found: " + className + " error: " + e.getMessage());
		}
		return ITransformerObject;
	}

	public IFilter<KinesisMessageModel> getFilter(KinesisConnectorConfiguration configuration) {
		return new AllPassFilter();
	}
}
