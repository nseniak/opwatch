package com.untrackr.alerter.processor.transformer.json;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.common.JavascriptTransformer;
import com.untrackr.alerter.service.ProcessorService;

public class JsonFactory extends ActiveProcessorFactory<JsonDesc, Json> {

	public JsonFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String type() {
		return "json";
	}

	@Override
	public Class<JsonDesc> descriptorClass() {
		return JsonDesc.class;
	}

	@Override
	public Json make(Object scriptObject) {
		JsonDesc descriptor = convertProcessorDescriptor(scriptObject);
		Json json = new Json(getProcessorService(), descriptor, type());
		return json;
	}

}
