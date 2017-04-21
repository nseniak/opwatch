package com.untrackr.alerter.processor.primitives.filter.test;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.common.ProcessorSignature;
import com.untrackr.alerter.processor.config.JavascriptPredicate;
import com.untrackr.alerter.service.ProcessorService;

public class TestFactory extends ActiveProcessorFactory<TestConfig, Test> {

	public TestFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "test";
	}

	@Override
	public Class<TestConfig> configurationClass() {
		return TestConfig.class;
	}

	@Override
	public Class<Test> processorClass() {
		return Test.class;
	}

	@Override
	public ProcessorSignature staticSignature() {
		return ProcessorSignature.makeFilter();
	}

	@Override
	public Test make(Object scriptObject) {
		TestConfig config = convertProcessorConfig(scriptObject);
		JavascriptPredicate lambda = checkPropertyValue("lambda", config.getLambda());
		return new Test(getProcessorService(), config, name(), lambda);
	}

}