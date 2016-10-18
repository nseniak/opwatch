package com.untrackr.alerter.processor.producer.curl;

import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ScriptStack;
import com.untrackr.alerter.processor.common.RuntimeScriptException;
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
	public Processor make(Object scriptObject) throws RuntimeScriptException {
		CurlDesc descriptor = convertProcessorArgument(CurlDesc.class, scriptObject);
		String urlString = checkVariableSubstitution("url", checkFieldValue("url", descriptor.getUrl()));
		URI uri;
		try {
			uri = new URI(urlString);
		} catch (URISyntaxException e) {
			throw new RuntimeScriptException("invalid \"url\": " + e.getLocalizedMessage() + ": \"" + urlString + "\"");
		}
		long defaultConnectTimeout = processorService.getProfileService().profile().getDefaultHttpConnectTimeout();
		long connectTimeout = optionalDurationValue("connectTimeout", descriptor.getConnectTimeout(), defaultConnectTimeout);
		long defaultReadTimeout = processorService.getProfileService().profile().getDefaultHttpReadTimeout();
		long readTimeout = optionalDurationValue("readTimeout", descriptor.getReadTimeout(), defaultReadTimeout);
		boolean insecure = optionalFieldValue("insecure", descriptor.isInsecure(), false);
		Curl curl = new Curl(getProcessorService(), ScriptStack.currentStack(), makeScheduledExecutor(descriptor), uri,
				(int) connectTimeout, (int) readTimeout, insecure);
		initialize(curl, descriptor);
		return curl;
	}

}
