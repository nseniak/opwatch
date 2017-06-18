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

package org.opwatch.processor.primitives.producer.receive;

import org.opwatch.processor.common.ProcessorVoidExecutionScope;
import org.opwatch.processor.payload.Payload;
import org.opwatch.processor.primitives.producer.Producer;
import org.opwatch.service.HttpService;
import org.opwatch.service.ProcessorService;

public class Receive extends Producer<ReceiveConfig> implements HttpService.PostBodyHandle {

	private String path;

	public Receive(ProcessorService processorService, ReceiveConfig configuration, String name, String path) {
		super(processorService, configuration, name);
		this.path = path;
	}

	@Override
	public void start() {
		processorService.getHttpService().addPostBodyConsumer(path, this);
	}

	@Override
	public void stop() {
		processorService.getHttpService().removePostBodyConsumer(path, this);
	}

	@Override
	public void handlePost(String input) {
		Object object = processorService.getScriptService().jsonParse(input);
		Payload payload = Payload.makeReceived(processorService, this, object);
		processorService.withExceptionHandling("error consuming HTTP post",
				() -> new ProcessorVoidExecutionScope(this),
				() -> output(payload));
	}

}
