package org.opwatch.processor.primitives.filter;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.opwatch.ProcessorTestRoot;

import java.io.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TrailTest extends ProcessorTestRoot {

	private static long EXEC_TIME = TimeUnit.MILLISECONDS.toMillis(1000);

	@Rule
	public ErrorCollector collector = new ErrorCollector();

	@Test
	public void testTrail() throws Exception {
		PipedOutputStream outputStream = new PipedOutputStream();
		PipedInputStream inputStream = new PipedInputStream(outputStream);
		long execTime = TimeUnit.MILLISECONDS.toMillis(2000);
		List<String> lines = runWithOutputLines(EXEC_TIME, inputStream,
				expression("pipe(stdin(), trail('1s'), stdout())"));
	}

}
