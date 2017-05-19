package org.opwatch.processor.common;

import org.junit.Test;
import org.opwatch.ProcessorTestRoot;
import org.opwatch.processor.primitives.producer.top.Top;
import org.springframework.test.annotation.DirtiesContext;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@DirtiesContext
public class ProcessorTest extends ProcessorTestRoot {

	@Test
	public void testRun() throws Exception {
		PipedOutputStream outputStream = new PipedOutputStream();
		PipedInputStream os = new PipedInputStream(outputStream);
		Future<Void> future = run(
				withOutput(outputStream,
						withTimeout(TimeUnit.SECONDS.toMillis(3),
								expression("pipe(top(), stdout())"))));
		BufferedReader outputReader = new BufferedReader(new InputStreamReader(os));
		List<String> lines = new ArrayList<>();
		Future<Void> outputFuture = readLines(outputReader, lines);
		Thread.sleep(TimeUnit.SECONDS.toMillis(1));
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
