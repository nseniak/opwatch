package com.untrackr.alerter.processor.primitives.filter.sh;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.common.ProcessorSignature;
import com.untrackr.alerter.processor.primitives.producer.CommandRunner;
import com.untrackr.alerter.service.ProcessorService;

public class ShFactory extends ActiveProcessorFactory<ShConfig, Sh> {

	public ShFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "sh";
	}

	@Override
	public Class<ShConfig> configurationClass() {
		return ShConfig.class;
	}

	@Override
	public Class<Sh> processorClass() {
		return Sh.class;
	}

	@Override
	public ProcessorSignature staticSignature() {
		return ProcessorSignature.makeAny();
	}

	@Override
	public Sh make(Object scriptObject) {
		ShConfig config = convertProcessorDescriptor(scriptObject);
		CommandRunner producer = makeCommandOutputProducer(config);
		return new Sh(getProcessorService(), config, name(), producer);
	}

}
