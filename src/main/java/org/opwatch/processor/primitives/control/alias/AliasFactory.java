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

package org.opwatch.processor.primitives.control.alias;

import org.opwatch.documentation.ProcessorCategory;
import org.opwatch.processor.common.Processor;
import org.opwatch.processor.common.ProcessorFactory;
import org.opwatch.service.ProcessorService;

public class AliasFactory extends ProcessorFactory<AliasConfig, Alias> {

	public AliasFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "alias";
	}

	@Override
	public Class<AliasConfig> configurationClass() {
		return AliasConfig.class;
	}

	@Override
	public Class<Alias> processorClass() {
		return Alias.class;
	}

	@Override
	public ProcessorCategory processorCategory() {
		return ProcessorCategory.control;
	}

	@Override
	public Alias make(Object scriptObject) {
		AliasConfig config = convertProcessorConfig(scriptObject);
		Processor processor = checkPropertyValue("processor", config.getProcessor());
		String name = checkPropertyValue("name", config.getName());
		return new Alias(getProcessorService(), processor, config, name);
	}

}
