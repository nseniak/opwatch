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

package org.opwatch.processor.primitives.filter;

import com.google.common.collect.ImmutableMap;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.opwatch.ProcessorTestRoot;

import java.io.ByteArrayInputStream;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.TimeUnit;

import static org.opwatch.testutil.ResourceComparator.compareOutput;

public class JstackTest extends ProcessorTestRoot {

	private static long EXEC_TIME = TimeUnit.MILLISECONDS.toMillis(5000);

	@Rule
	public ErrorCollector collector = new ErrorCollector();

	@Test
	public void testStack() throws Exception {
		Map<String, String> jstackOptions = ImmutableMap.<String, String>builder()
				.put("jstack-2-2.txt", "{ methodRegexp: /^com\\.untrackr/ }")
				.put("jstack-2-3.txt", "{ methodRegexp: /^com\\.foobar/ }")
				.put("jstack-5-2.txt", "{ methodRegexp: /^org\\.myproject\\./ }")
				.build();
		compareOutput(collector, JstackTest.class, "jstack-*.txt", "jstack-*.json",
				(resourceName, resourceString) -> {
					String options = jstackOptions.getOrDefault(resourceName, "");
					List<String> lines = runWithOutputLines(EXEC_TIME, new ByteArrayInputStream(resourceString.getBytes()),
							runExpression("pipe(stdin(), jstack(" + options + "), stdout())"));
					List<String> content = outputContent(lines);
					StringJoiner joiner = new StringJoiner(",", "[", "]");
					content.forEach(joiner::add);
					return joiner.toString();
				});
	}

}
