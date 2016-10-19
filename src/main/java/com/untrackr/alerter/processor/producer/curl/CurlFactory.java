package com.untrackr.alerter.processor.producer.curl;

import com.untrackr.alerter.processor.common.*;
import com.untrackr.alerter.processor.producer.ScheduledExecutorFactory;
import com.untrackr.alerter.service.ProcessorService;

import java.net.URI;
import java.net.URISyntaxException;

public class CurlFactory extends ScheduledExecutorFactory {

	public CurlFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "curl";
	}

	@Override
	public Processor make(Object scriptObject) {
		CurlDesc descriptor = convertProcessorArgument(CurlDesc.class, scriptObject);
		String urlString = checkVariableSubstitution("url", checkPropertyValue("url", descriptor.getUrl()));
		URI uri;
		try {
			uri = new URI(urlString);
		} catch (URISyntaxException e) {
			throw new AlerterException("invalid \"url\": " + e.getLocalizedMessage() + ": \"" + urlString + "\"",
					ExceptionContext.makeProcessorFactory(name()));
		}
		long defaultConnectTimeout = processorService.getProfileService().profile().getDefaultHttpConnectTimeout();
		long connectTimeout = optionalDurationValue("connectTimeout", descriptor.getConnectTimeout(), defaultConnectTimeout);
		long defaultReadTimeout = processorService.getProfileService().profile().getDefaultHttpReadTimeout();
		long readTimeout = optionalDurationValue("readTimeout", descriptor.getReadTimeout(), defaultReadTimeout);
		boolean insecure = optionaPropertyValue("insecure", descriptor.isInsecure(), false);
		Curl curl = new Curl(getProcessorService(), displayName(descriptor), makeScheduledExecutor(descriptor), uri,
				(int) connectTimeout, (int) readTimeout, insecure);
		return curl;
	}

}
