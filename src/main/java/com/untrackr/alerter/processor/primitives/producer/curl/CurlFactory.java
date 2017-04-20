package com.untrackr.alerter.processor.primitives.producer.curl;

import com.untrackr.alerter.processor.common.FactoryExecutionScope;
import com.untrackr.alerter.processor.common.RuntimeError;
import com.untrackr.alerter.processor.common.ProcessorSignature;
import com.untrackr.alerter.processor.primitives.producer.ScheduledExecutorFactory;
import com.untrackr.alerter.service.ProcessorService;

import java.net.URI;
import java.net.URISyntaxException;

public class CurlFactory extends ScheduledExecutorFactory<CurlConfig, Curl> {

	public CurlFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "curl";
	}

	@Override
	public Class<CurlConfig> configurationClass() {
		return CurlConfig.class;
	}

	@Override
	public Class<Curl> processorClass() {
		return Curl.class;
	}

	@Override
	public ProcessorSignature staticSignature() {
		return ProcessorSignature.makeProducer();
	}

	@Override
	public Curl make(Object scriptObject) {
		CurlConfig config = convertProcessorConfig(scriptObject);
		String urlString = checkPropertyValue("url", config.getUrl());
		URI uri;
		try {
			uri = new URI(urlString);
		} catch (URISyntaxException e) {
			throw new RuntimeError("invalid \"url\": " + e.getLocalizedMessage() + ": \"" + urlString + "\"",
					new FactoryExecutionScope(this),
					e);
		}
		long connectTimeout = checkPropertyValue("connectTimeout", config.getConnectTimeout()).value(this);
		long readTimeout = checkPropertyValue("readTimeout", config.getReadTimeout()).value(this);
		boolean insecure = config.getInsecure();
		return new Curl(getProcessorService(), config, name(), makeScheduledExecutor(config), uri,
				(int) connectTimeout, (int) readTimeout, insecure);
	}

}
