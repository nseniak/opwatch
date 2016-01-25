package com.untrackr.alerter.processor.producer.curl;

import com.untrackr.alerter.model.common.JsonDescriptor;
import com.untrackr.alerter.processor.common.IncludePath;
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
	public String type() {
		return "curl";
	}

	@Override
	public Curl make(JsonDescriptor jsonDescriptor, IncludePath path) throws ValidationError {
		CurlDesc descriptor = convertDescriptor(path, CurlDesc.class, jsonDescriptor);
		String urlString = checkVariableSubstitution(path, jsonDescriptor, "url", checkFieldValue(path, jsonDescriptor, "url", descriptor.getUrl()));
		URI uri;
		try {
			uri = new URI(urlString);
		} catch (URISyntaxException e) {
			throw new ValidationError("invalid \"url\": " + e.getLocalizedMessage() + ": \"" + urlString + "\"", path, jsonDescriptor);
		}
		long defaultConnectTimeout = processorService.getProfileService().profile().getDefaultHttpConnectTimeout();
		long connectTimeout = optionalDurationValue(path, jsonDescriptor, "connectTimeout", descriptor.getConnectTimeout(), defaultConnectTimeout);
		long defaultReadTimeout = processorService.getProfileService().profile().getDefaultHttpReadTimeout();
		long readTimeout = optionalDurationValue(path, jsonDescriptor, "readTimeout", descriptor.getReadTimeout(), defaultReadTimeout);
		boolean insecure = optionalFieldValue(path, jsonDescriptor, "insecure", descriptor.isInsecure(), false);
		Curl curl = new Curl(getProcessorService(), path, makeScheduledExecutor(path, jsonDescriptor, descriptor), uri,
				(int) connectTimeout, (int) readTimeout, insecure);
		initialize(curl, descriptor);
		return curl;
	}

}
