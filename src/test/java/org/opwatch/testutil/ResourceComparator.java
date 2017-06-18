/*
 * Copyright (c) 2016-2017 by OMC Inc and other Opwatch contributors
 *
 * Licensed under the Apache License, Version 2.0  (the "License").  You may obtain
 * a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed
 * under the License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied.  See the License for
 * the specific language governing permissions and limitations under the License.
 */

package org.opwatch.testutil;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;
import org.junit.rules.ErrorCollector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Base class for all tests.
 */
public class ResourceComparator {

	public static Logger logger = LoggerFactory.getLogger(ResourceComparator.class);

	/**
	 * Set this flag to true if you want to regenerate the output files instead of comparing them to the input.
	 */
	private static boolean overwrite = "true".equals(System.getProperty("tests.files.overwrite"));

	public static void setOverwrite(boolean value) {
		overwrite = value;
	}

	public static void compareToResource(Class<?> resourceClass, String output, String expectedResourceName) {
		new StringComparisonTest(resourceClass, output, expectedResourceName).run(overwrite);
	}

	/**
	 * Iterate the test runner on all resources.
	 */
	public static void processResources(ErrorCollector collector,
																			Class<?> resourceClass,
																			String inputResourcePattern,
																			ResourceTestRunner runner) {
		Resource[] inputResources = patternResources(resourceClass, inputResourcePattern);
		for (Resource inputResource : inputResources) {
			try (InputStream stream = inputResource.getInputStream()) {
				String input = new String(ByteStreams.toByteArray(stream), Charsets.UTF_8);
				try {
					runner.run(inputResource.getDescription(), input);
				} catch (Throwable t) {
					collector.addError(t);
				}
			} catch (IOException e) {
				throw new RuntimeException("cannot load resource: " + inputResource, e);
			}
		}
	}

	public static void compareOutput(ErrorCollector collector,
																	 Class<?> resourceClass,
																	 String inputResourcePattern,
																	 String expectedResourcePattern,
																	 ResourceTestOutputProducer producer) {
		Resource[] inputResources = patternResources(resourceClass, inputResourcePattern);
		String inputSuffix = patternSuffix(inputResourcePattern);
		String expectedSuffix = patternSuffix(expectedResourcePattern);
		String outputPrefix = patternPrefix(expectedResourcePattern);
		for (Resource inputResource : inputResources) {
			String inputFilename = inputResource.getFilename();
			String basename = inputFilename.substring(0, inputFilename.length() - inputSuffix.length());
			String expectedFilename = outputPrefix + basename + expectedSuffix;
			OutputComparisonTest test = new ResourceComparisonTest(resourceClass, expectedFilename, inputResource, producer);
			try {
				test.run(overwrite);
			} catch (Throwable t) {
				collector.addError(t);
			}
		}
	}

	private static Resource[] patternResources(Class<?> resourceClass, String pattern) {
		try {
			ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(resourceClass.getClassLoader());
			String path = pattern.contains(":") ? pattern : "classpath:"
					+ resourceClass.getPackage().getName().replace(".", "/")
					+ "/"
					+ pattern;
			Resource[] resources = resolver.getResources(path);
			Arrays.sort(resources, Comparator.comparing(Resource::getFilename));
			return resources;
		} catch (IOException e) {
			throw new RuntimeException("cannot resolve resource pattern: " + pattern, e);
		}
	}

	private static String patternSuffix(String pattern) {
		int wildCardIndex = pattern.lastIndexOf('*');
		if (wildCardIndex < 0) {
			throw new RuntimeException("missing wildcard: " + pattern);
		}
		String suffix = pattern.substring(wildCardIndex + 1);
		if (suffix.contains("/")) {
			throw new RuntimeException("suffix contains a '/': " + pattern);
		}
		return suffix;
	}

	private static String patternPrefix(String pattern) {
		int i = pattern.lastIndexOf('/');
		if (i == -1) {
			return "";
		} else {
			return pattern.substring(0, i + 1);
		}
	}

	private static String resourceString(Resource input) {
		try (InputStream stream = input.getInputStream()) {
			return new String(ByteStreams.toByteArray(stream), Charsets.UTF_8);
		} catch (IOException e) {
			throw new RuntimeException("cannot load resource: " + input, e);
		}
	}

}
