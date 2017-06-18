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

import org.opwatch.processor.common.Processor;
import org.opwatch.processor.config.ImplicitProperty;
import org.opwatch.processor.config.ProcessorConfig;

import java.util.List;

public class ParallelConfig extends ProcessorConfig {

	private List<Processor<?>> processors;

	@ImplicitProperty
	public List<Processor<?>> getProcessors() {
		return processors;
	}

	public void setProcessors(List<Processor<?>> processors) {
		this.processors = processors;
	}

}
