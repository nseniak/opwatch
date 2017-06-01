package org.opwatch.processor.primitives.producer.curl;

import org.opwatch.processor.common.FactoryExecutionScope;
import org.opwatch.processor.common.RuntimeError;
import org.opwatch.processor.common.ProcessorSignature;
import org.opwatch.processor.config.ValueOrList;
import org.opwatch.processor.primitives.producer.ScheduledExecutorFactory;
import org.opwatch.service.ProcessorService;
import org.springframework.http.HttpMethod;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

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
		int connectTimeout = convertToInt("connectTimeout", checkPropertyValue("connectTimeout", config.getConnectTimeout()).value(this));
		int readTimeout = convertToInt("timeout", checkPropertyValue("timeout", config.getTimeout()).value(this));
		boolean insecure = config.getInsecure();
		HttpMethod method = null;
		try {
			method = HttpMethod.valueOf(config.getMethod().toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new RuntimeError("invalid \"method\": \"" + config.getMethod() + "\"",
					new FactoryExecutionScope(this),
					e);
		}
		boolean followRedirects = config.getFollowRedirects();
		int maxRedirects = config.getMaxRedirects();
		Map<String, ValueOrList<String>> headers = config.getHeaders();
		Object data = config.getData();
		return new Curl(getProcessorService(), config, name(), makeScheduledExecutor(config), uri, method, headers, data,
				connectTimeout, readTimeout, insecure, followRedirects, maxRedirects);
	}

	private int convertToInt(String fieldName, long l) {
		if (l <= 0 || l > Integer.MAX_VALUE) {
			throw new RuntimeError("invalid \"" + fieldName + "\": " + l + ", must be a positive Integer");
		}
		return (int) l;
	}

}
