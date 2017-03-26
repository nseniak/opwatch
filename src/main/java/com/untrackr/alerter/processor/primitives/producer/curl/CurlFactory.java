package com.untrackr.alerter.processor.primitives.producer.curl;

import com.untrackr.alerter.processor.common.AlerterException;
import com.untrackr.alerter.processor.common.ExceptionContext;
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
	public Curl make(Object scriptObject) {
		CurlConfig descriptor = convertProcessorDescriptor(scriptObject);
		String urlString = checkVariableSubstitution("url", checkPropertyValue("url", descriptor.getUrl()));
		URI uri;
		try {
			uri = new URI(urlString);
		} catch (URISyntaxException e) {
			throw new AlerterException("invalid \"url\": " + e.getLocalizedMessage() + ": \"" + urlString + "\"",
					ExceptionContext.makeProcessorFactory(name()));
		}
		long connectTimeout = durationValue(descriptor.getConnectTimeout());
		long readTimeout = durationValue(descriptor.getReadTimeout());
		boolean insecure = descriptor.getInsecure();
		Curl curl = new Curl(getProcessorService(), descriptor, name(), makeScheduledExecutor(descriptor), uri,
				(int) connectTimeout, (int) readTimeout, insecure);
		return curl;
	}

}
