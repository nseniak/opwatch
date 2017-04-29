package org.opwatch.processor.primitives.filter.apply;

import org.opwatch.processor.common.ActiveProcessorFactory;
import org.opwatch.processor.common.ProcessorSignature;
import org.opwatch.processor.config.JavascriptFilter;
import org.opwatch.service.ProcessorService;

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
		JavascriptFilter lambda = checkPropertyValue("lambda", config.getLambda());
		return new Apply(getProcessorService(), config, name(), lambda);
	}

}
