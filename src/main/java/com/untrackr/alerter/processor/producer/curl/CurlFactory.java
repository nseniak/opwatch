package com.untrackr.alerter.processor.producer.curl;

import com.untrackr.alerter.model.common.JsonDescriptor;
import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ScriptStack;
import com.untrackr.alerter.processor.common.ValidationError;
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
	public Processor make(Object object) throws ValidationError {
		JsonDescriptor jsonDescriptor = scriptDescriptor(object);
		CurlDesc descriptor = convertScriptDescriptor(CurlDesc.class, jsonDescriptor);
		String urlString = checkVariableSubstitution(jsonDescriptor, "url", checkFieldValue(jsonDescriptor, "url", descriptor.getUrl()));
		URI uri;
		try {
			uri = new URI(urlString);
		} catch (URISyntaxException e) {
			throw new ValidationError("invalid \"url\": " + e.getLocalizedMessage() + ": \"" + urlString + "\"", jsonDescriptor);
		}
		long defaultConnectTimeout = processorService.getProfileService().profile().getDefaultHttpConnectTimeout();
		long connectTimeout = optionalDurationValue(jsonDescriptor, "connectTimeout", descriptor.getConnectTimeout(), defaultConnectTimeout);
		long defaultReadTimeout = processorService.getProfileService().profile().getDefaultHttpReadTimeout();
		long readTimeout = optionalDurationValue(jsonDescriptor, "readTimeout", descriptor.getReadTimeout(), defaultReadTimeout);
		boolean insecure = optionalFieldValue(jsonDescriptor, "insecure", descriptor.isInsecure(), false);
		Curl curl = new Curl(getProcessorService(), ScriptStack.currentStack(), makeScheduledExecutor(jsonDescriptor, descriptor), uri,
				(int) connectTimeout, (int) readTimeout, insecure);
		initialize(curl, descriptor);
		return curl;
	}

}
