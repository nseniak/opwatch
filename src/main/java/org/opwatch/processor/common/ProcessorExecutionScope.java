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

import org.opwatch.service.ProcessorService;

public abstract class ProcessorExecutionScope extends ExecutionScope {

	private Processor<?> processor;

	protected ProcessorExecutionScope(Processor<?> processor) {
		this.processor = processor;
	}

	@Override
	public MessageContext makeContext(ProcessorService processorService, ScriptStack stack) {
		return MessageContext.makeProcessor(processorService, processor.getName(), null, stack);
	}

	public Processor<?> getProcessor() {
		return processor;
	}

}
