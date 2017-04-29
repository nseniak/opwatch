package org.opwatch.processor.primitives.filter.sh;

import org.opwatch.processor.common.ActiveProcessorFactory;
import org.opwatch.processor.common.ProcessorSignature;
import org.opwatch.processor.primitives.producer.CommandRunner;
import org.opwatch.service.ProcessorService;

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
		ShConfig config = convertProcessorConfig(scriptObject);
		CommandRunner producer = makeCommandOutputProducer(config);
		return new Sh(getProcessorService(), config, name(), producer);
	}

}
