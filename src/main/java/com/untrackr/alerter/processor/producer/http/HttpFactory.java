package com.untrackr.alerter.processor.producer.http;

import com.untrackr.alerter.model.common.JsonDescriptor;
import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.common.ValidationError;
import com.untrackr.alerter.service.ProcessorService;

public class HttpFactory extends ActiveProcessorFactory {

	public HttpFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "http";
	}

	@Override
	public Http make(JsonDescriptor jsonDescriptor, IncludePath path) throws ValidationError {
		HttpDesc descriptor = convertDescriptor(path, HttpDesc.class, jsonDescriptor);
		String urlPath = checkFieldValue(path, jsonDescriptor, "url", descriptor.getUrl());
		Http http = new Http(getProcessorService(), path, urlPath);
		initialize(http, descriptor);
		return http;
	}

}
