package org.opwatch.processor.primitives.consumer.send;

import org.opwatch.processor.common.ActiveProcessorFactory;
import org.opwatch.processor.common.ProcessorSignature;
import org.opwatch.service.Config;
import org.opwatch.service.ProcessorService;

import java.util.regex.Pattern;

public class SendFactory extends ActiveProcessorFactory<SendConfig, Send> {

	public SendFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "send";
	}

	@Override
	public Class<SendConfig> configurationClass() {
		return SendConfig.class;
	}

	@Override
	public Class<Send> processorClass() {
		return Send.class;
	}

	@Override
	public ProcessorSignature staticSignature() {
		return ProcessorSignature.makeConsumer();
	}

	private static Pattern pathPattern = Pattern.compile("(?<hostname>[^:/]+)?(?::(?<port>[0-9]+))?(?<stack>/.*)");

	@Override
	public Send make(Object scriptObject) {
		SendConfig config = convertProcessorConfig(scriptObject);
		String path = checkPropertyValue("path", config.getPath());
		Config profile = processorService.config();
		String hostname = checkPropertyValue("hostname", config.getHostname());
		int port =  (config.getPort() != null) ? config.getPort() : profile.defaultPostPort();
		return new Send(getProcessorService(), config, name(), path, hostname, port, path);
	}

}
