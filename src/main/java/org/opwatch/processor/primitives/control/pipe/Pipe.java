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

package org.opwatch.processor.primitives.control.pipe;

import org.opwatch.processor.common.Processor;
import org.opwatch.processor.common.ProcessorSignature;
import org.opwatch.processor.common.ControlProcessor;
import org.opwatch.processor.payload.Payload;
import org.opwatch.service.ProcessorService;

import java.util.List;

public class Pipe extends ControlProcessor<PipeConfig> {

	private List<Processor<?>> processors;

	public Pipe(ProcessorService processorService, List<Processor<?>> processors, PipeConfig configuration, String name) {
		super(processorService, configuration, name);
		this.processors = processors;
		Processor<?> previous = null;
		for (Processor<?> processor : processors) {
			processor.assignContainer(this);
			if (previous != null) {
				processor.addProducer(previous);
				previous.checkOutputCompatibility(processor.getSignature().getInputRequirement());
				processor.checkInputCompatibility(previous.getSignature().getOutputRequirement());
			}
			previous = processor;
		}
		this.signature = new ProcessorSignature(first().getSignature().getInputRequirement(), last().getSignature().getOutputRequirement());
	}

	@Override
	public void addProducer(Processor<?> producer) {
		super.addProducer(producer);
		first().addProducer(producer);
	}

	@Override
	public void addConsumer(Processor<?> consumer) {
		super.addConsumer(consumer);
		last().addConsumer(consumer);
	}

	@Override
	public void start() {
		for (int i = processors.size() - 1; i >= 0; i--) {
			Processor processor = processors.get(i);
			processor.start();
		}
	}

	@Override
	public void stop() {
		stop(processors);
	}

	private Processor<?> first() {
		return processors.get(0);
	}

	private Processor<?> last() {
		return processors.get(processors.size() - 1);
	}

	@Override
	public void consume(Payload payload) {
		// Nothing to do. Producers and consumers are already connected.
	}

}
