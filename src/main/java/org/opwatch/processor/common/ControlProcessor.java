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
import org.opwatch.service.ProcessorService;

import java.util.List;

public abstract class ControlProcessor<C extends ProcessorConfig> extends Processor<C> {

	public ControlProcessor(ProcessorService processorService, C configuration, String name) {
		super(processorService, configuration, name);
	}

	public void stop(List<Processor<?>> processors) {
		boolean ok = true;
		for (Processor<?> processor : processors) {
			ok = ok & processorService.withExceptionHandling("error stopping processor",
					() -> new ProcessorVoidExecutionScope(processor),
					processor::stop);
		}
		if (!ok) {
			throw new RuntimeError("error stopping processor", new ProcessorVoidExecutionScope(this));
		}
	}

}
