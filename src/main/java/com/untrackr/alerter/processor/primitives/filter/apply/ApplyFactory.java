package com.untrackr.alerter.processor.primitives.filter.apply;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.common.ProcessorSignature;
import com.untrackr.alerter.processor.config.JavascriptFilter;
import com.untrackr.alerter.service.ProcessorService;

public class ApplyFactory extends ActiveProcessorFactory<ApplyConfig, Apply> {

	public ApplyFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "apply";
	}

	@Override
	public Class<ApplyConfig> configurationClass() {
		return ApplyConfig.class;
	}

	@Override
	public Class<Apply> processorClass() {
		return Apply.class;
	}

	@Override
	public ProcessorSignature staticSignature() {
		return ProcessorSignature.makeFilter();
	}

	@Override
	public Apply make(Object scriptObject) {
		ApplyConfig config = convertProcessorConfig(scriptObject);
		JavascriptFilter transformer = checkPropertyValue("lambda", config.getLambda());
		return new Apply(getProcessorService(), config, name(), transformer);
	}

}
