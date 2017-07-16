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

import org.opwatch.processor.config.ProcessorConfig;
import org.opwatch.processor.payload.Payload;
import org.opwatch.service.ProcessorService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static jdk.nashorn.internal.runtime.ScriptRuntime.UNDEFINED;
import static org.opwatch.processor.common.ProcessorSignature.outputCompatibilityError;

public abstract class Processor<C extends ProcessorConfig> {

	private String id;
	protected ProcessorService processorService;
	protected C configuration;
	protected String name;
	protected List<Processor<?>> producers = new ArrayList<>();
	protected List<Processor<?>> consumers = new ArrayList<>();
	protected Processor<?> container;
	protected ScriptStack constructionStack;

	public static final String PROCESSOR_RUNNING_MESSAGE = "processor up and running";
	public static final String PROCESSOR_STOPPED_MESSAGE = "processor stopped";
	public static final String PROCESSOR_INTERRUPTED_MESSAGE = "processor interrupted";

	public Processor(ProcessorService processorService, C configuration, String name) {
		this.id = processorService.uuid();
		this.processorService = processorService;
		this.name = name;
		this.configuration = configuration;
		this.constructionStack = ScriptStack.currentStack();
	}

	public void assignContainer(Processor<?> processor) {
		if (container != null) {
			throw new RuntimeError("a processor can only be used once; this one is already used in " + container.getName(),
					new ProcessorVoidExecutionScope(this));
		}
		container = processor;
	}

	public Object run() {
		checkRunnable();
		processorService.signalSystemInfo(PROCESSOR_RUNNING_MESSAGE);
		start();
		processorService.setRunningProcessorThread(Thread.currentThread());
		processorService.setProcessorReturnValue(UNDEFINED);
		try {
			while (true) {
				Thread.sleep(TimeUnit.DAYS.toMillis(1));
			}
		} catch (InterruptedException e) {
			processorService.printStderr(PROCESSOR_INTERRUPTED_MESSAGE);
		} finally {
			processorService.setRunningProcessorThread(null);
		}
		stop();
		processorService.signalSystemInfo(PROCESSOR_STOPPED_MESSAGE);
		Object returnValue = processorService.getProcessorReturnValue();
		processorService.setProcessorReturnValue(UNDEFINED);
		return returnValue;
	}

	/**
	 * For use in scripts
	 */
	public boolean isprocessor() {
		return true;
	}

	public void addProducer(Processor<?> producer) {
		producers.add(producer);
	}

	public void addConsumer(Processor<?> consumer) {
		consumers.add(consumer);
	}

	public abstract void start();

	public abstract void stop();

	public abstract void consume(Payload payload);

	public abstract InferenceResult inferOutput(DataRequirement previousOutput);

	protected DataRequirement checkInferOutput(DataRequirement previousOutput) {
		InferenceResult outputInference = inferOutput(previousOutput);
		if (outputInference.isError()) {
			outputInference.throwError();
		}
		return outputInference.getRequirement();
	}

	public void checkRunnable() {
		DataRequirement output = checkInferOutput(DataRequirement.NoData);
		String errorMessage = outputCompatibilityError(output, DataRequirement.NoData);
		if (errorMessage != null) {
			throw new RuntimeError(errorMessage, new ProcessorVoidExecutionScope(this));
		}
	}

	@Override
	public String toString() {
		return "[object " + name + "]";
	}

	public String getId() {
		return id;
	}

	public ProcessorService getProcessorService() {
		return processorService;
	}

	public ScriptStack getConstructionStack() {
		return constructionStack;
	}

	public String getName() {
		return name;
	}

	public C getConfiguration() {
		return configuration;
	}

	public ProcessorFactory<?, ?> getFactory() {
		return processorService.getScriptService().factory(this.getClass());
	}

}
