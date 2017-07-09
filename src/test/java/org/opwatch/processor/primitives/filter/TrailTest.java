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
				runExpression("pipe(stdin(), trail('1s'), stdout())"));
	}

}
