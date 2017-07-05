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

import org.opwatch.processor.config.ActiveProcessorConfig;
import org.opwatch.processor.payload.Payload;
import org.opwatch.service.ProcessorService;
import org.opwatch.service.ScriptService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.StringJoiner;

/**
 * A processor that does some processing of its own, as opposed to control processors that combine other processors.
 */
public abstract class ActiveProcessor<D extends ActiveProcessorConfig> extends Processor<D> {

	private static final Logger logger = LoggerFactory.getLogger(ActiveProcessor.class);

	public ActiveProcessor(ProcessorService processorService, D configuration, String name) {
		super(processorService, configuration, name);
	}

	@Override
	public void inferSignature() {
		ActiveProcessorFactory<?, ?> factory = (ActiveProcessorFactory<?, ?>) processorService.getScriptService().factory(this.getClass());
		if (factory == null) {
			throw new IllegalStateException("cannot find factory for class " + this.getClass().getName());
		}
		signature = factory.staticSignature();
		if (signature == null) {
			throw new IllegalStateException("no static signature for class " + this.getClass().getName());
		}
	}

	@Override
	public void addProducer(Processor<?> producer) {
		super.addProducer(producer);
		producer.addConsumer(this);
	}

	@Override
	public void check() {
		StringJoiner joiner = new StringJoiner(", ");
		switch (signature.getInputRequirement()) {
			case Data:
				if (producers.isEmpty()) {
					joiner.add("input is required");
				}
				break;
			case NoData:
				if (!producers.isEmpty()) {
					joiner.add("cannot receive an input");
				}
				break;
			case Any:
				break;
		}
		switch (signature.getOutputRequirement()) {
			case Data:
				if (consumers.isEmpty()) {
					joiner.add("output should be used");
				}
				break;
			case NoData:
				if (!consumers.isEmpty()) {
					joiner.add("does not generate an output");
				}
				break;
			case Any:
				break;
		}
		if (joiner.length() != 0) {
			throw new RuntimeError("incorrect use in a pipeline: " + joiner.toString(), new ProcessorVoidExecutionScope(this));
		}
	}

	@Override
	public void start() {
		// By default, do nothing
	}

	@Override
	public void stop() {
		// By default, do nothing
	}

	protected void producerInputError() {
		producerInputError();
	}

	public void outputTransformed(Object value, Payload input) {
		Payload payload = Payload.makeTransformed(processorService, this, input, value);
		output(consumers, payload);
	}

	public void outputProduced(Object value) {
		Payload payload = Payload.makeRoot(processorService, this, value);
		output(consumers, payload);
	}

	public void output(Payload payload) {
		output(consumers, payload);
	}

	private void output(List<Processor<?>> consumers, Payload payload) {
		if (processorService.config().trace()) {
			logger.info("Output: " + getName() + " ==> " + processorService.json(payload));
		}
		for (Processor<?> consumer : consumers) {
			processorService.withExceptionHandling("error processing input",
					() -> new ProcessorVoidExecutionScope(consumer),
					() -> consumer.consume(payload));
		}
	}

	public <T> T payloadValue(Payload payload, Class<?> clazz) {
		Object value = payload.getValue();
		if (!clazz.isAssignableFrom(value.getClass())) {
			ScriptService sc = processorService.getScriptService();
			String message = "wrong input value: expected " + sc.typeName(clazz) + ", got " + sc.typeName(value.getClass());
			throw new RuntimeError(message, new ProcessorPayloadExecutionScope(this, payload));
		}
		return (T) value;
	}

}
