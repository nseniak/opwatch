package com.untrackr.alerter.processor.consumer.post;

import com.untrackr.alerter.model.common.AlerterProfile;
import com.untrackr.alerter.processor.common.*;
import com.untrackr.alerter.service.ProcessorService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PostFactory extends ActiveProcessorFactory {

	public PostFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "post";
	}

	private static Pattern pathPattern = Pattern.compile("(?<hostname>[^:/]+)?(?::(?<port>[0-9]+))?(?<stack>/.*)");

	@Override
	public Processor make(Object scriptObject) {
		PostDesc descriptor = convertProcessorArgument(PostDesc.class, scriptObject);
		String pathString = checkVariableSubstitution("path", checkPropertyValue("path", descriptor.getPath()));
		Matcher matcher = pathPattern.matcher(pathString);
		if (!matcher.matches()) {
			throw new AlerterException("incorrect \"path\" syntax: \"" + pathString + "\"", ExceptionContext.makeProcessorFactory(name()));
		}
		AlerterProfile profile = processorService.getProfileService().profile();
		String hostname = (matcher.group("hostname") != null) ? matcher.group("hostname") : profile.getDefaultPostHostname();
		int port = (matcher.group("port") != null) ? Integer.parseInt(matcher.group("hostname")) : profile.getDefaultPostPort();
		String urlPath = matcher.group("stack");
		Post post = new Post(getProcessorService(), displayName(descriptor), pathString, hostname, port, urlPath);
		return post;
	}

}
