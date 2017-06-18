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

package org.opwatch.processor.primitives.filter.sh_f;

import org.opwatch.processor.common.ProcessorVoidExecutionScope;
import org.opwatch.processor.payload.Payload;
import org.opwatch.processor.primitives.producer.CommandRunner;
import org.opwatch.processor.primitives.filter.Filter;
import org.opwatch.service.ProcessorService;

import java.util.concurrent.Future;

public class ShFilter extends Filter<ShFilterConfig> {

	protected Future<?> commandConsumerThreadFuture;
	private CommandRunner commandRunner;

	public ShFilter(ProcessorService processorService, ShFilterConfig configuration, String name, CommandRunner commandRunner) {
		super(processorService, configuration, name);
		this.commandRunner = commandRunner;
	}

	@Override
	public void start() {
		super.start();
		commandConsumerThreadFuture = processorService.getConsumerExecutor().submit(() -> {
			processorService.withExceptionHandling("error starting command",
					() -> new ProcessorVoidExecutionScope(this),
					() -> {
						commandRunner.startProcess(this);
						commandRunner.produce(this, -1);
					});
		});
	}

	@Override
	public void stop() {
		commandConsumerThreadFuture.cancel(true);
		commandRunner.stopProcess();
		super.stop();
	}

	@Override
	public void consume(Payload payload) {
		commandRunner.consume(this, payload);
	}

}
