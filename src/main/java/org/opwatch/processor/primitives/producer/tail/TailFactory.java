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

package org.opwatch.processor.primitives.producer.tail;

import org.opwatch.documentation.ProcessorCategory;
import org.opwatch.processor.common.ActiveProcessorFactory;
import org.opwatch.processor.common.ProcessorSignature;
import org.opwatch.service.ProcessorService;

import java.nio.file.FileSystems;

public class TailFactory extends ActiveProcessorFactory<TailConfig, Tail> {

	public TailFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "tail";
	}

	@Override
	public Class<TailConfig> configurationClass() {
		return TailConfig.class;
	}

	@Override
	public Class<Tail> processorClass() {
		return Tail.class;
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
	public Tail make(Object scriptObject) {
		TailConfig config = convertProcessorConfig(scriptObject);
		String file = checkPropertyValue("file", config.getFile());
		boolean ignoreBlankLine = checkPropertyValue("ignoreBlank", config.getIgnoreBlank());
		return new Tail(getProcessorService(), config, name(), FileSystems.getDefault().getPath(file), ignoreBlankLine);
	}

}
