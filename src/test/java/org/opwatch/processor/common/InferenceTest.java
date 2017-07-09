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

package org.opwatch.processor.common;

import org.junit.Test;
import org.opwatch.ProcessorTestRoot;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

public class InferenceTest extends ProcessorTestRoot {

	@Test
	public void testProducer() {
		checkProducer("top()");
	}

	@Test
	public void testConsumer() {
		checkConsumer("alert(\"test alert title\")");
	}

	@Test
	public void testFilter() {
		checkFilter("grep(/foo/)");
	}

	@Test
	public void testCall() {
		checkProducer("call(function () {return 1})");
		checkFilter("call({input: function (x) {return x}, output: function () {return 1}})");
	}

	private void checkProducer(String processor) {
		for (DataRequirement req : DataRequirement.values()) {
			DataRequirement expected = (req == DataRequirement.Data) ? null : DataRequirement.Data;
			check(processor, req, expected);
		}
	}

	private void checkConsumer(String processor) {
		for (DataRequirement req : DataRequirement.values()) {
			DataRequirement expected = (req == DataRequirement.NoData) ? null : DataRequirement.NoData;
			check(processor, req, expected);
		}
	}

	private void checkFilter(String processor) {
		for (DataRequirement req : DataRequirement.values()) {
			DataRequirement expected = (req == DataRequirement.NoData) ? null : DataRequirement.Data;
			check(processor, req, expected);
		}
	}

	private void checkFilterWithIgnorableOutput(String processor) {
		for (DataRequirement req : DataRequirement.values()) {
			DataRequirement expected = (req == DataRequirement.NoData) ? null : DataRequirement.Any;
			check(processor, req, expected);
		}
	}

	@Test
	public void testPipe() {
		for (DataRequirement req : DataRequirement.values()) {
			check("pipe()", req, req);
			check("pipe(pipe(), pipe())", req, req);
		}
		checkProducer("pipe(top(), pipe(), pipe())");
		checkProducer("pipe(pipe(), top(), pipe())");
		checkProducer("pipe(pipe(), pipe(), top())");
		checkConsumer("pipe(stdout(), pipe(), pipe())");
		checkConsumer("pipe(pipe(), stdout(), pipe())");
		checkConsumer("pipe(pipe(), pipe(), stdout())");
		checkFilter("pipe(grep(/foo/), pipe(), pipe())");
		checkFilter("pipe(pipe(), grep(/foo/), pipe())");
		checkFilter("pipe(pipe(), pipe(), grep(/foo/))");
	}

	@Test
	public void testParallel() {
		for (DataRequirement req : DataRequirement.values()) {
			check("parallel()", req, DataRequirement.NoData);
			check("parallel(parallel(), parallel())", req, DataRequirement.NoData);
		}
		checkProducer("parallel(top())");
		checkProducer("parallel(top(), stdin())");
		checkConsumer("parallel(stdout())");
		checkConsumer("parallel(stdout(), alert('test'))");
		checkConsumer("parallel(stdout(), alert('test'))");
		checkFilter("parallel(top(), alert('test'))");
		checkFilter("parallel(alert('test'), top())");
		// Combination of parallel and pipe
		checkParallelProducerPipe("parallel(top(), pipe())");
		checkParallelProducerPipe("parallel(pipe(), top())");
		checkParallelConsumerPipe("parallel(stdout(), pipe())");
		checkParallelConsumerPipe("parallel(pipe(), stdout())");
		checkParallelFilterPipe("parallel(grep(/foo/), pipe())");
		checkParallelFilterPipe("parallel(pipe(), grep(/foo/))");
	}

	private void checkParallelProducerPipe(String processor) {
		check(processor, DataRequirement.Unknown, DataRequirement.Data);
		check(processor, DataRequirement.NoData, DataRequirement.Data);
		check(processor, DataRequirement.Data, DataRequirement.Data);
		check(processor, DataRequirement.Any, DataRequirement.Data);
	}

	private void checkParallelConsumerPipe(String processor) {
		check(processor, DataRequirement.Unknown, DataRequirement.Unknown);
		check(processor, DataRequirement.NoData, null);
		check(processor, DataRequirement.Data, DataRequirement.Data);
		check(processor, DataRequirement.Any, DataRequirement.Any);
	}

	private void checkParallelFilterPipe(String processor) {
		check(processor, DataRequirement.Unknown, DataRequirement.Data);
		check(processor, DataRequirement.NoData, null);
		check(processor, DataRequirement.Data, DataRequirement.Data);
		check(processor, DataRequirement.Any, DataRequirement.Data);
	}

	@Test
	public void testAlias() {
		checkProducer("alias({name:'foo', processor: top(), configuration: {}})");
		checkConsumer("alias({name:'foo', processor: stdout(), configuration: {}})");
		checkFilter("alias({name:'foo', processor: grep(/foo/), configuration: {}})");
	}

	public void check(String processor, DataRequirement input, DataRequirement expectedOutput) {
		Processor proc = evalProcessor(processor);
		InferenceResult result = proc.inferOutput(input);
		if (expectedOutput != null) {
			assertThat(result.isError(), is(false));
			assertThat(result.getRequirement(), is(expectedOutput));
		} else {
			assertThat(result.isError(), is(true));
		}
	}

}
