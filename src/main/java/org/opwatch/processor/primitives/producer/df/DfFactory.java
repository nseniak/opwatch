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

package org.opwatch.processor.primitives.producer.df;

import org.opwatch.documentation.ProcessorCategory;
import org.opwatch.processor.common.ProcessorSignature;
import org.opwatch.processor.primitives.producer.ScheduledExecutorFactory;
import org.opwatch.service.ProcessorService;

public class DfFactory extends ScheduledExecutorFactory<DfConfig, Df> {

	public DfFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "df";
	}

	@Override
	public Class<DfConfig> configurationClass() {
		return DfConfig.class;
	}

	@Override
	public Class<Df> processorClass() {
		return Df.class;
	}

	@Override
	public ProcessorSignature staticSignature() {
		return ProcessorSignature.makeProducer();
	}

	@Override
	public ProcessorCategory processorCategory() {
		return ProcessorCategory.producer;
	}

	@Override
	public Df make(Object scriptObject) {
		DfConfig config = convertProcessorConfig(scriptObject);
		String file = checkPropertyValue("file", config.getFile());
		return new Df(getProcessorService(), config, name(), checkSchedulingInfo(config), new java.io.File(file));
	}

}
