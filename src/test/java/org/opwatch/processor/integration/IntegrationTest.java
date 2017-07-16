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

package org.opwatch.processor.integration;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;
import org.opwatch.ProcessorTestRoot;

import java.util.concurrent.Callable;

import static org.opwatch.testutil.ResourceComparator.compareOutput;

public class IntegrationTest extends ProcessorTestRoot {

	@Rule
	public ErrorCollector collector = new ErrorCollector();

	@Test
	public void test() throws Exception {
		compareOutput(collector, IntegrationTest.class, "*.js", "*-pretty.pretty",
				(resourceName, processorExpression) -> {
					Callable<Object> resultCallable = runExpressionCallable(processorExpression);
					Object result = runExpression(processorExpression);
					return scriptService.pretty(result);
				});
	}
}
