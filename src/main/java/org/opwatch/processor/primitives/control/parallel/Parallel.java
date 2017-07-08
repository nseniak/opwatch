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

package org.opwatch.processor.primitives.control.parallel;

import org.opwatch.processor.common.*;
import org.opwatch.processor.payload.Payload;
import org.opwatch.service.ProcessorService;

import java.util.List;

public class Parallel extends ControlProcessor<ParallelConfig> {

	private List<Processor<?>> processors;

	public Parallel(ProcessorService processorService, List<Processor<?>> processors, ParallelConfig configuration, String name) {
		super(processorService, configuration, name);
		this.processors = processors;
		for (Processor<?> processor : processors) {
			processor.assignContainer(this);
		}
		this.signature = new ProcessorSignature(ProcessorSignature.DataRequirement.NoData, ProcessorSignature.DataRequirement.NoData);
		for (Processor<?> processor : processors) {
			this.signature = this.signature.parallel(processor.getSignature());
		}
	}

	@Override
	public void addProducer(Processor<?> producer) {
		super.addProducer(producer);
		for (Processor<?> processor : processors) {
			if (processor.getSignature().acceptsInput()) {
				processor.addProducer(producer);
			}
		}
	}

	@Override
	public void addConsumer(Processor<?> consumer) {
		super.addConsumer(consumer);
		for (Processor<?> processor : processors) {
			if (processor.getSignature().producesOutput()) {
				processor.addConsumer(consumer);
			}
		}
	}

	@Override
	public void start() {
		for (Processor processor : processors) {
			processor.start();
		}
	}

	@Override
	public void stop() {
		stop(processors);
	}

	@Override
	public void consume(Payload payload) {
		// Nothing to do. Producers and consumers are already connected.
	}

}
