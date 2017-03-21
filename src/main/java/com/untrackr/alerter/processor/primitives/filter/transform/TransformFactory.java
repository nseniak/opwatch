package com.untrackr.alerter.processor.primitives.filter.transform;

import com.untrackr.alerter.processor.common.ActiveProcessorFactory;
import com.untrackr.alerter.processor.descriptor.JavascriptFilter;
import com.untrackr.alerter.service.ProcessorService;

public class TransformFactory extends ActiveProcessorFactory<TransformDescriptor, Transform> {

	public TransformFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "transform";
	}

	@Override
	public Class<TransformDescriptor> descriptorClass() {
		return TransformDescriptor.class;
	}

	@Override
	public Transform make(Object scriptObject) {
		TransformDescriptor descriptor = convertProcessorDescriptor(scriptObject);
		JavascriptFilter transformer = checkPropertyValue("transformer", descriptor.getTransformer());
		Transform transform = new Transform(getProcessorService(), descriptor, name(), transformer);
		return transform;
	}

}
