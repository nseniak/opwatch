package com.untrackr.alerter.processor.primitives.filter.json;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class JsonFactory extends ActiveProcessorFactory<JsonDescriptor, Json> {

	public JsonFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "json";
	}

	@Override
	public Class<JsonDescriptor> descriptorClass() {
		return JsonDescriptor.class;
	}

	@Override
	public Json make(Object scriptObject) {
		JsonDescriptor descriptor = convertProcessorDescriptor(scriptObject);
		Json json = new Json(getProcessorService(), descriptor, name());
		return json;
	}

}
