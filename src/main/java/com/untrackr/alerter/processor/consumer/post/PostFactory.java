package com.untrackr.alerter.processor.consumer.post;

import com.untrackr.alerter.model.common.AlerterProfile;
import com.untrackr.alerter.model.common.JsonDescriptor;
import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.common.IncludePath;
import com.untrackr.alerter.processor.common.Processor;
import com.untrackr.alerter.processor.common.ValidationError;
import com.untrackr.alerter.service.ProcessorService;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PostFactory extends ActiveProcessorFactory {

	public PostFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "post";
	}

	private static Pattern pathPattern = Pattern.compile("(?<hostname>[^:/]+)?(?::(?<port>[0-9]+))?(?<path>/.*)");

	@Override
	public Processor make(JsonDescriptor jsonDescriptor, IncludePath path) throws ValidationError {
		PostDesc descriptor = convertDescriptor(path, PostDesc.class, jsonDescriptor);
		String pathString = checkVariableSubstitution(path, jsonDescriptor, "path", checkFieldValue(path, jsonDescriptor, "path", descriptor.getPath()));
		Matcher matcher = pathPattern.matcher(pathString);
		if (!matcher.matches()) {
			throw new ValidationError("incorrect \"path\" syntax: \"" + pathString + "\"", path, jsonDescriptor);
		}
		AlerterProfile profile = processorService.getProfileService().profile();
		String hostname = (matcher.group("hostname") != null) ? matcher.group("hostname") : profile.getDefaultPostHostname();
		int port = (matcher.group("port") != null) ? Integer.parseInt(matcher.group("hostname")) : profile.getDefaultPostPort();
		String urlPath = matcher.group("path");
		Post post = new Post(getProcessorService(), path, pathString, hostname, port, urlPath);
		initialize(post, descriptor);
		return post;
	}

}
