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

package org.opwatch.processor.primitives.producer.console;

import org.opwatch.processor.payload.Payload;
import org.opwatch.processor.primitives.producer.Producer;
import org.opwatch.service.ConsoleService;
import org.opwatch.service.ProcessorService;

public class Stdin extends Producer<StdinConfig> implements ConsoleService.ConsoleLineConsumer {

	public Stdin(ProcessorService processorService, StdinConfig configuration, String name) {
		super(processorService, configuration, name);
	}

	@Override
	public void start() {
		processorService.getConsoleService().addConsumer(this);
	}

	@Override
	public void stop() {
		processorService.getConsoleService().removeConsumer(this);
	}

	@Override
	public void consume(ConsoleService.ConsoleLine line) {
		Payload payload = Payload.makeRoot(processorService, this, line.getText());
		payload.setMetadata(new StdinPayloadMetadata(line.getLine()));
		output(payload);
	}

}
