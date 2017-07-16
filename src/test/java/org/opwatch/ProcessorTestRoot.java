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

package org.opwatch;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.google.common.util.concurrent.SimpleTimeLimiter;
import com.google.common.util.concurrent.TimeLimiter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.opwatch.processor.common.Processor;
import org.opwatch.service.ProcessorService;
import org.opwatch.service.ScriptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static junit.framework.TestCase.fail;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.number.OrderingComparison.greaterThan;
import static org.junit.Assert.assertEquals;
import static org.opwatch.processor.common.Processor.PROCESSOR_RUNNING_MESSAGE;
import static org.opwatch.processor.common.Processor.PROCESSOR_STOPPED_MESSAGE;
import static org.opwatch.service.ScriptService.setHomeDirectory;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class ProcessorTestRoot {

	private static final Logger logger = LoggerFactory.getLogger(ProcessorTestRoot.class);

	@Autowired
	protected ScriptService scriptService;

	@Autowired
	protected ProcessorService processorService;

	// Maximum time it should take for a processor to stop after being interrupted
	protected static final long STOP_TIMEOUT = TimeUnit.SECONDS.toMillis(1);

	// Default port used by Spring for tests
	protected static final int TEST_PORT = 8080;

	protected <T> Callable<T> withIO(InputStream inputStream, OutputStream outputStream, Callable<T> callable) {
		return () -> {
			System.setIn(inputStream);
			System.setOut(new PrintStream(outputStream));
			return callable.call();
		};
	}

	protected <T> Callable<T> withOutput(OutputStream outputStream, Callable<T> callable) {
		return withIO(new ForbiddenInputStream(), outputStream, callable);
	}

	protected <T> Callable<T> withTimeout(long timeout, Callable<T> callable) {
		TimeLimiter timeLimiter = new SimpleTimeLimiter();
		return () -> timeLimiter.callWithTimeout(callable, timeout, TimeUnit.MILLISECONDS, true);
	}

	protected <T> Callable<T> withStop(long duration, Callable<T> callable) {
		ScheduledExecutorService es = Executors.newSingleThreadScheduledExecutor();
		es.schedule(() -> processorService.stopRunningProcessor(), duration, TimeUnit.MILLISECONDS);
		return withTimeout(duration + STOP_TIMEOUT, callable);
	}

	protected Callable<Object> runExpressionCallable(String expression) {
		return () -> {
			initTestEnvironment();
			return scriptService.runExpression(expression);
		};
	}

	protected Object runExpression(String expression) {
		initTestEnvironment();
		return scriptService.runExpression(expression);
	}

	protected Object evalExpression(String expression) {
		initTestEnvironment();
		return scriptService.evalExpression(expression);
	}

	protected Processor evalProcessor(String expression) {
		initTestEnvironment();
		Object value = scriptService.evalExpression(expression);
		assertThat(value, instanceOf(Processor.class));
		return (Processor) value;
	}

	private void initTestEnvironment() {
		// Set the current directory to <home>/bin so config.js can be properly loaded
		setHomeDirectory(System.getProperty("user.dir") + "/distrib");
		CommandLineOptions options = new CommandLineOptions();
		options.setHostname("test_host");
		options.setPort(TEST_PORT);
		assertThat(processorService.initialize(options), is(true));
		processorService.config().defaultPostPort(TEST_PORT);
	}

	protected <T> Future<T> future(Callable<T> callable) {
		ExecutorService es = Executors.newSingleThreadExecutor();
		return es.submit(callable);
	}

	protected <T> List<String> runWithOutputLines(long duration, InputStream inputStream, Callable<T> callable) {
		try {
			PipedOutputStream outputStream = new PipedOutputStream();
			BufferedReader outputReader = new BufferedReader(new InputStreamReader(new PipedInputStream(outputStream)));
			List<String> outputLines = new ArrayList<>();
			Future<T> runFuture = future(withStop(duration, withIO(inputStream, outputStream, callable)));
			Future<Void> outputFuture = readLines(outputReader, outputLines);
			runFuture.get();
			outputFuture.cancel(false);
			return outputLines;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected String readLine(long timeout, BufferedReader reader) {
		try {
			TimeLimiter timeLimiter = new SimpleTimeLimiter();
			return timeLimiter.callWithTimeout(() -> reader.readLine(), timeout, TimeUnit.MILLISECONDS, true);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	protected Future<Void> readLines(BufferedReader reader, List<String> lines) {
		ExecutorService es = Executors.newSingleThreadExecutor();
		return es.submit(() -> {
			String line;
			try {
				while ((line = reader.readLine()) != null) {
					lines.add(line);
				}
			} catch (IOException e) {
				// Nothing to do
			}
			return null;
		});
	}

	public static class ForbiddenInputStream extends InputStream {

		@Override
		public int read() throws IOException {
			fail("input stream should not be read");
			return 0;
		}

	}

	protected InputStream resourceStream(String name) {
		try {
			URL resourceUrl = this.getClass().getResource(name);
			assertThat("resource not found: " + name, resourceUrl, notNullValue());
			return resourceUrl.openStream();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected String resourceString(String name) {
		try {
			return CharStreams.toString(new InputStreamReader(resourceStream(name), Charsets.UTF_8));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected List<String> outputContent(List<String> lines) {
		assertThat(lines.size(), greaterThan(1));
		assertThat(lines.get(0), containsString(PROCESSOR_RUNNING_MESSAGE));
		assertThat(lines.get(lines.size() - 1), containsString(PROCESSOR_STOPPED_MESSAGE));
		return lines.subList(1, lines.size() - 1);
	}

	protected <T> T jsonParse(String text, Class<T> clazz) {
		try {
			return new ObjectMapper().readValue(text, clazz);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected <T> T jsonResourceParse(String name, Class<T> clazz) {
		try {
			return new ObjectMapper().readValue(resourceString(name), clazz);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	protected String jsonPrettyString(Object object) {
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			DefaultPrettyPrinter pp = new DefaultPrettyPrinter();
			pp.indentArraysWith(new DefaultPrettyPrinter.FixedSpaceIndenter());
			return objectMapper.writer(pp).writeValueAsString(object);
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}

	protected <T> void compareJson(String message, String actual, String expected, Class<T> clazz) throws IOException {
		assertEquals(message, jsonPrettyString(jsonParse(actual, clazz)), jsonPrettyString(jsonParse(expected, clazz)));
	}

	@Test
	public void dummyTest() {
		// Nothing to do. Just to avoid errors from JUnit because this class contains no tests.
	}

}
