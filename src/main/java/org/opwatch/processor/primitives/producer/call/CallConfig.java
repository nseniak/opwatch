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

package org.opwatch.processor.primitives.producer.call;

import org.opwatch.processor.config.*;

public class CallConfig extends ScheduledProcessorConfig {

	private JavascriptConsumer input;
	private JavascriptProducer output;

	@ImplicitProperty
	public JavascriptProducer getOutput() {
		return output;
	}

	public void setOutput(JavascriptProducer output) {
		this.output = output;
	}

	@OptionalProperty
	public JavascriptConsumer getInput() {
		return input;
	}

	public void setInput(JavascriptConsumer input) {
		this.input = input;
	}

}
