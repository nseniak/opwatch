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

package org.opwatch.processor.primitives.filter.trace;

import org.opwatch.documentation.ProcessorCategory;
import org.opwatch.processor.common.ActiveProcessorFactory;
import org.opwatch.processor.common.ProcessorSignature;
import org.opwatch.service.ProcessorService;

public class TraceFactory extends ActiveProcessorFactory<TraceConfig, Trace> {

	public TraceFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "trace";
	}

	@Override
	public Class<TraceConfig> configurationClass() {
		return TraceConfig.class;
	}

	@Override
	public Class<Trace> processorClass() {
		return Trace.class;
	}

	@Override
	public ProcessorSignature staticSignature() {
		return ProcessorSignature.makeSideEffectFilter();
	}

	@Override
	public ProcessorCategory processorCategory() {
		return ProcessorCategory.filter_ignorable;
	}

	@Override
	public Trace make(Object scriptObject) {
		TraceConfig config = convertProcessorConfig(scriptObject);
		return new Trace(getProcessorService(), config, name());
	}

}
