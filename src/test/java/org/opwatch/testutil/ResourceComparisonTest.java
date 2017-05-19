package org.opwatch.testutil;

import org.springframework.core.io.Resource;

class ResourceComparisonTest extends OutputComparisonTest {

	private Resource inputResource;
	private ResourceTestOutputProducer producer;

	public ResourceComparisonTest(Class<?> loaderClass, String expectedResourceName, Resource inputResource, ResourceTestOutputProducer producer) {
		super(loaderClass, expectedResourceName);
		this.inputResource = inputResource;
		this.producer = producer;
	}

	@Override
	public String output() {
		return producer.produce(displayName(), resourceString(inputResource));
	}

	@Override
	public String displayName() {
		return inputResource.getFilename();
	}

}
