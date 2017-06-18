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

package org.opwatch.processor.primitives.filter.jstack;

import jdk.nashorn.internal.objects.NativeRegExp;
import org.opwatch.processor.common.ActiveProcessorFactory;
import org.opwatch.processor.common.ProcessorSignature;
import org.opwatch.service.ProcessorService;

public class JstackFactory extends ActiveProcessorFactory<JstackConfig, Jstack> {

	public JstackFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "jstack";
	}

	@Override
	public Class<JstackConfig> configurationClass() {
		return JstackConfig.class;
	}

	@Override
	public Class<Jstack> processorClass() {
		return Jstack.class;
	}

	@Override
	public ProcessorSignature staticSignature() {
		return ProcessorSignature.makeFilter();
	}

	@Override
	public Jstack make(Object scriptObject) {
		JstackConfig config = convertProcessorConfig(scriptObject);
		NativeRegExp methodRegex = config.getMethodRegexp();
		return new Jstack(getProcessorService(), config, name(), methodRegex);
	}

}
