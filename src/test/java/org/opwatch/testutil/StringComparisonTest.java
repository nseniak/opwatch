package org.opwatch.testutil;

class StringComparisonTest extends OutputComparisonTest {

	private String displayName;
	private String output;

	public StringComparisonTest(Class<?> loaderClass, String output, String expectedResourceName) {
		super(loaderClass, expectedResourceName);
		this.output = output;
		this.displayName = expectedResourceName;
	}

	@Override
	public String output() {
		return output;
	}

	@Override
	public String displayName() {
		return displayName;
	}

}
