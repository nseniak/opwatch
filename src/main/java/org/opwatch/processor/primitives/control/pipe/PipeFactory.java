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

import org.opwatch.processor.common.FactoryExecutionScope;
import org.opwatch.processor.common.Processor;
import org.opwatch.processor.common.ProcessorFactory;
import org.opwatch.processor.common.RuntimeError;
import org.opwatch.service.ProcessorService;

import java.util.List;

public class PipeFactory extends ProcessorFactory<PipeConfig, Pipe> {

	public PipeFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "pipe";
	}

	@Override
	public Class<PipeConfig> configurationClass() {
		return PipeConfig.class;
	}

	@Override
	public Class<Pipe> processorClass() {
		return Pipe.class;
	}

	@Override
	public Pipe make(Object scriptObject) {
		PipeConfig config = convertProcessorConfig(scriptObject);
		List<Processor<?>> processors = config.getProcessors();
		return new Pipe(getProcessorService(), processors, config, name());
	}

}
