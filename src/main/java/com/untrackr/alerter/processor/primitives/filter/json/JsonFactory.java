package com.untrackr.alerter.processor.primitives.filter.json;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.common.ProcessorSignature;
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
	public Class<Json> processorClass() {
		return Json.class;
	}

	@Override
	public ProcessorSignature staticSignature() {
		return ProcessorSignature.makeFilter();
	}

	@Override
	public Json make(Object scriptObject) {
		JsonConfig descriptor = convertProcessorDescriptor(scriptObject);
		return new Json(getProcessorService(), descriptor, name());
	}

}
