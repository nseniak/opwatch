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

package org.opwatch.processor.primitives.control.alias;

import org.opwatch.processor.common.Processor;
import org.opwatch.processor.common.ControlProcessor;
import org.opwatch.processor.payload.Payload;
import org.opwatch.service.ProcessorService;

public class Alias extends ControlProcessor<AliasConfig> {

	Processor<?> processor;

	public Alias(ProcessorService processorService, Processor<?> processor, AliasConfig configuration, String name) {
		super(processorService, configuration, name);
		this.processor = processor;
		processor.assignContainer(this);
		this.signature = processor.getSignature();
	}

	@Override
	public void addProducer(Processor<?> producer) {
		super.addProducer(producer);
		processor.addProducer(producer);
	}

	@Override
	public void addConsumer(Processor<?> consumer) {
		super.addConsumer(consumer);
		processor.addConsumer(consumer);
	}

	@Override
	public void start() {
		processor.start();
	}

	@Override
	public void stop() {
		processor.stop();
	}

	@Override
	public void consume(Payload payload) {
		// Nothing to do. The processor is already connected.
	}

}
