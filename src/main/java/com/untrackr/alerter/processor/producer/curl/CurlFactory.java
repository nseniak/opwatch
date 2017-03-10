package com.untrackr.alerter.processor.producer.curl;

import com.untrackr.alerter.processor.common.AlerterException;
import com.untrackr.alerter.processor.common.ExceptionContext;
import com.untrackr.alerter.processor.consumer.alert.AlertGeneratorDesc;
import com.untrackr.alerter.processor.producer.ScheduledExecutorFactory;
import com.untrackr.alerter.service.ProcessorService;

import java.net.URI;
import java.net.URISyntaxException;

public class CurlFactory extends ScheduledExecutorFactory {

	public CurlFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "curl";
	}

	@Override
	public Class<CurlDesc> descriptorClass() {
		return CurlDesc.class;
	}

	@Override
	public Curl make(Object scriptObject) {
		CurlDesc descriptor = convertProcessorDescriptor(CurlDesc.class, scriptObject);
		String urlString = checkVariableSubstitution("url", checkPropertyValue("url", descriptor.getUrl()));
		URI uri;
		try {
			uri = new URI(urlString);
		} catch (URISyntaxException e) {
			throw new AlerterException("invalid \"url\": " + e.getLocalizedMessage() + ": \"" + urlString + "\"",
					ExceptionContext.makeProcessorFactory(type()));
		}
		long defaultConnectTimeout = processorService.getProfileService().profile().getDefaultHttpConnectTimeout();
		long connectTimeout = optionalDurationValue("connectTimeout", descriptor.getConnectTimeout(), defaultConnectTimeout);
		long defaultReadTimeout = processorService.getProfileService().profile().getDefaultHttpReadTimeout();
		long readTimeout = optionalDurationValue("readTimeout", descriptor.getReadTimeout(), defaultReadTimeout);
		boolean insecure = optionaPropertyValue("insecure", descriptor.getInsecure(), false);
		Curl curl = new Curl(getProcessorService(), descriptor, type(), makeScheduledExecutor(descriptor), uri,
				(int) connectTimeout, (int) readTimeout, insecure);
		return curl;
	}

}
