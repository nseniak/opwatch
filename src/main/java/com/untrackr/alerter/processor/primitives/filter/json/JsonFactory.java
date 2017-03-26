package com.untrackr.alerter.processor.primitives.filter.json;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.service.ProcessorService;

public class JsonFactory extends ActiveProcessorFactory<JsonConfig, Json> {

	public JsonFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "json";
	}

	@Override
	public Class<JsonConfig> configurationClass() {
		return JsonConfig.class;
	}

	@Override
	public Json make(Object scriptObject) {
		JsonConfig descriptor = convertProcessorDescriptor(scriptObject);
		Json json = new Json(getProcessorService(), descriptor, name());
		return json;
	}

}
