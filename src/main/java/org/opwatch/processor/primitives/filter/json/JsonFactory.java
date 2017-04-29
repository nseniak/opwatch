package org.opwatch.processor.primitives.filter.json;

import org.opwatch.processor.common.ActiveProcessorFactory;
import org.opwatch.processor.common.ProcessorSignature;
import org.opwatch.service.ProcessorService;

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
		JsonConfig config = convertProcessorConfig(scriptObject);
		return new Json(getProcessorService(), config, name());
	}

}
