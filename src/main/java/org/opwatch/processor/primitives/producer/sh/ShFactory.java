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

package org.opwatch.processor.primitives.producer.sh;

import org.opwatch.documentation.ProcessorCategory;
import org.opwatch.processor.common.CommandInfo;
import org.opwatch.processor.common.ProcessorSignature;
import org.opwatch.processor.common.SchedulingInfo;
import org.opwatch.processor.primitives.producer.ScheduledExecutorFactory;
import org.opwatch.service.ProcessorService;

public class ShFactory extends ScheduledExecutorFactory<ShConfig, Sh> {

	public ShFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "sh";
	}

	@Override
	public Class<ShConfig> configurationClass() {
		return ShConfig.class;
	}

	@Override
	public Class<Sh> processorClass() {
		return Sh.class;
	}

	@Override
	public ProcessorSignature staticSignature() {
		return ProcessorSignature.makeAny();
	}

	@Override
	public ProcessorCategory processorCategory() {
		return ProcessorCategory.any;
	}

	@Override
	public Sh make(Object scriptObject) {
		ShConfig config = convertProcessorConfig(scriptObject);
		SchedulingInfo schedulingInfo = checkSchedulingInfo(config);
		CommandInfo commandInfo = makeCommandInfoCheck(config);
		return new Sh(getProcessorService(), config, name(), schedulingInfo, commandInfo);
	}

}
