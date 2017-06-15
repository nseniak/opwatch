package org.opwatch.processor.primitives.filter.sh_f;

import org.opwatch.processor.common.ActiveProcessorFactory;
import org.opwatch.processor.common.ProcessorSignature;
import org.opwatch.processor.primitives.producer.CommandRunner;
import org.opwatch.service.ProcessorService;

public class ShFilterFactory extends ActiveProcessorFactory<ShFilterConfig, ShFilter> {

	public ShFilterFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "sh_f";
	}

	@Override
	public Class<ShFilterConfig> configurationClass() {
		return ShFilterConfig.class;
	}

	@Override
	public Class<ShFilter> processorClass() {
		return ShFilter.class;
	}

	@Override
	public ProcessorSignature staticSignature() {
		return ProcessorSignature.makeAny();
	}

	@Override
	public ShFilter make(Object scriptObject) {
		ShFilterConfig config = convertProcessorConfig(scriptObject);
		CommandRunner producer = makeCommandOutputProducer(config);
		return new ShFilter(getProcessorService(), config, name(), producer);
	}

}
