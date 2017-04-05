package com.untrackr.alerter.processor.primitives.consumer.post;

import com.untrackr.alerter.processor.common.*;
import com.untrackr.alerter.service.AlerterConfig;
import com.untrackr.alerter.service.ProcessorService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PostFactory extends ActiveProcessorFactory<PostConfig, Post> {

	public PostFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "post";
	}

	@Override
	public Class<PostConfig> configurationClass() {
		return PostConfig.class;
	}

	@Override
	public Class<Post> processorClass() {
		return Post.class;
	}

	@Override
	public ProcessorSignature staticSignature() {
		return ProcessorSignature.makeConsumer();
	}

	private static Pattern pathPattern = Pattern.compile("(?<hostname>[^:/]+)?(?::(?<port>[0-9]+))?(?<stack>/.*)");

	@Override
	public Post make(Object scriptObject) {
		PostConfig descriptor = convertProcessorConfig(scriptObject);
		String pathString = checkVariableSubstitution("path", checkPropertyValue("path", descriptor.getPath()));
		Matcher matcher = pathPattern.matcher(pathString);
		if (!matcher.matches()) {
			throw new RuntimeError("incorrect \"path\" syntax: \"" + pathString + "\"",
					new FactoryExecutionContext(this));
		}
		AlerterConfig profile = processorService.config();
		String hostname = (matcher.group("hostname") != null) ? matcher.group("hostname") : profile.defaultPostHostname();
		int port = (matcher.group("port") != null) ? Integer.parseInt(matcher.group("hostname")) : profile.defaultPostPort();
		String urlPath = matcher.group("stack");
		return new Post(getProcessorService(), descriptor, name(), pathString, hostname, port, urlPath);
	}

}
