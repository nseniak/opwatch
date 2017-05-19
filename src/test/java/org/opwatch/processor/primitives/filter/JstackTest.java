package org.opwatch.processor.primitives.filter;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.opwatch.ProcessorTestRoot;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

import static org.opwatch.testutil.ResourceComparator.compareOutput;

public class JstackTest extends ProcessorTestRoot {

	private static long EXEC_TIME = TimeUnit.MILLISECONDS.toMillis(1000);

	@Rule
	public ErrorCollector collector = new ErrorCollector();

	@Test
	public void testStack() throws Exception {
		compareOutput(collector, JstackTest.class, "jstack-*.txt", "jstack-*.json",
				(resourceName, resourceString) -> {
					List<String> lines = runWithOutputLines(EXEC_TIME, new ByteArrayInputStream(resourceString.getBytes()),
							expression("pipe(stdin(), jstack(), stdout())"));
					List<String> content = outputContent(lines);
					StringJoiner joiner = new StringJoiner(",", "[", "]");
					content.forEach(joiner::add);
					return joiner.toString();
				});
	}

}
