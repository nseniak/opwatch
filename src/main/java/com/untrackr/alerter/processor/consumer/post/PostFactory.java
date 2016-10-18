package com.untrackr.alerter.processor.consumer.post;

import com.untrackr.alerter.model.common.AlerterProfile;
import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ScriptStack;
import com.untrackr.alerter.processor.common.RuntimeScriptException;
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
	public Processor make(Object scriptObject) throws RuntimeScriptException {
		PostDesc descriptor = convertProcessorArgument(PostDesc.class, scriptObject);
		String pathString = checkVariableSubstitution("path", checkFieldValue("path", descriptor.getPath()));
		Matcher matcher = pathPattern.matcher(pathString);
		if (!matcher.matches()) {
			throw new RuntimeScriptException("incorrect \"path\" syntax: \"" + pathString + "\"");
		}
		AlerterProfile profile = processorService.getProfileService().profile();
		String hostname = (matcher.group("hostname") != null) ? matcher.group("hostname") : profile.getDefaultPostHostname();
		int port = (matcher.group("port") != null) ? Integer.parseInt(matcher.group("hostname")) : profile.getDefaultPostPort();
		String urlPath = matcher.group("stack");
		Post post = new Post(getProcessorService(), ScriptStack.currentStack(), pathString, hostname, port, urlPath);
		initialize(post, descriptor);
		return post;
	}

}
