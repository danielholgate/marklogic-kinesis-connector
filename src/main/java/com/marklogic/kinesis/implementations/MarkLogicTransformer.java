package com.marklogic.kinesis.implementations;

import com.amazonaws.services.kinesis.connectors.interfaces.ITransformer;
import com.marklogic.kinesis.model.KinesisMessageModel;
import com.marklogic.kinesis.model.MarkLogicMessageModel;

public abstract interface MarkLogicTransformer
  extends ITransformer<KinesisMessageModel, MarkLogicMessageModel>
{}
