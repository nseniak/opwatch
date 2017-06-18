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

package org.opwatch.processor.primitives.filter.grep;

import org.opwatch.processor.common.ActiveProcessorFactory;
import org.opwatch.processor.common.ProcessorSignature;
import org.opwatch.service.ProcessorService;
import jdk.nashorn.internal.objects.NativeRegExp;

public class GrepFactory extends ActiveProcessorFactory<GrepConfig, Grep> {

	public GrepFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "grep";
	}

	@Override
	public Class<GrepConfig> configurationClass() {
		return GrepConfig.class;
	}

	@Override
	public Class<Grep> processorClass() {
		return Grep.class;
	}

	@Override
	public ProcessorSignature staticSignature() {
		return ProcessorSignature.makeFilter();
	}

	@Override
	public Grep make(Object scriptObject) {
		GrepConfig config = convertProcessorConfig(scriptObject);
		NativeRegExp regexp = checkPropertyValue("regexp", config.getRegexp());
		boolean invert = checkPropertyValue("invert", config.getInvert());
		return new Grep(getProcessorService(), config, name(), regexp, invert);
	}

}
