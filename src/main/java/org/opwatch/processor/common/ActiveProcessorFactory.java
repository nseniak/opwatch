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

import org.opwatch.processor.config.ActiveProcessorConfig;
import org.opwatch.processor.primitives.producer.CommandExecutorDesc;
import org.opwatch.processor.primitives.producer.CommandRunner;
import org.opwatch.service.ProcessorService;

import java.io.File;

public abstract class ActiveProcessorFactory<D extends ActiveProcessorConfig, P extends ActiveProcessor> extends ProcessorFactory<D, P> {

	public ActiveProcessorFactory(ProcessorService processorService) {
		super(processorService);
	}

	protected CommandRunner makeCommandOutputProducer(CommandExecutorDesc descriptor) {
		String command = checkPropertyValue("command", descriptor.getCommand());
		File directory = new File(".");
		return new CommandRunner(processorService, command, directory);
	}

}
