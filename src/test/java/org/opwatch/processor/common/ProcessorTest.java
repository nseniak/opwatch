package org.opwatch.processor.common;

import org.junit.Test;
import org.opwatch.ProcessorTestRoot;
import org.opwatch.processor.primitives.producer.top.Top;
import org.springframework.test.annotation.DirtiesContext;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@DirtiesContext
public class ProcessorTest extends ProcessorTestRoot {

	private static final long STARTUP_WAIT = TimeUnit.SECONDS.toMillis(3);
	private static final long EXECUTION_TIMEOUT = TimeUnit.SECONDS.toMillis(30);

	@Test
	public void testRun() throws Exception {
		PipedOutputStream outputStream = new PipedOutputStream();
		PipedInputStream os = new PipedInputStream(outputStream);
		Future<Void> future = run(
				withOutput(outputStream,
						withTimeout(EXECUTION_TIMEOUT,
								expression("pipe(top(), stdout())"))));
		BufferedReader outputReader = new BufferedReader(new InputStreamReader(os));
		List<String> lines = new ArrayList<>();
		Future<Void> outputFuture = readLines(outputReader, lines);
		Thread.sleep(STARTUP_WAIT);
		processorService.stopRunningProcessor();
		outputFuture.cancel(false);
		future.get();
		// Check that the processor is stopped
		assertThat(processorService.running(), is(false));
		// Check that the output contains the output
		List<String> content = outputContent(lines);
		jsonParse(content.get(0), Top.TopInfo.class);
	}

}
