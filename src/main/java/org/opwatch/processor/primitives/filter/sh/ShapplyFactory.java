package org.opwatch.processor.primitives.filter.sh;

import org.opwatch.processor.common.ActiveProcessorFactory;
import org.opwatch.processor.common.ProcessorSignature;
import org.opwatch.processor.primitives.producer.CommandRunner;
import org.opwatch.service.ProcessorService;

public class ShapplyFactory extends ActiveProcessorFactory<ShapplyConfig, Shapply> {

	public ShapplyFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "shapply";
	}

	@Override
	public Class<ShapplyConfig> configurationClass() {
		return ShapplyConfig.class;
	}

	@Override
	public Class<Shapply> processorClass() {
		return Shapply.class;
	}

	@Override
	public ProcessorSignature staticSignature() {
		return ProcessorSignature.makeAny();
	}

	@Override
	public Shapply make(Object scriptObject) {
		ShapplyConfig config = convertProcessorConfig(scriptObject);
		CommandRunner producer = makeCommandOutputProducer(config);
		return new Shapply(getProcessorService(), config, name(), producer);
	}

}
