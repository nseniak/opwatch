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

package org.opwatch.processor.primitives.consumer.send;

import org.opwatch.processor.common.ProcessorPayloadExecutionScope;
import org.opwatch.processor.payload.Payload;
import org.opwatch.processor.primitives.consumer.Consumer;
import org.opwatch.service.ProcessorService;
import org.springframework.web.util.UriComponentsBuilder;

import static org.opwatch.service.HttpService.RECEIVE_PATH_PREFIX;

public class Send extends Consumer<SendConfig> {

	private String hostname;
	private int port;
	private String path;
	private String uri;

	public Send(ProcessorService processorService, SendConfig configuration, String name, String hostname, int port, String path) {
		super(processorService, configuration, name);
		this.hostname = hostname;
		this.port = port;
		this.path = path;
		uri = UriComponentsBuilder.newInstance().scheme("http").host(hostname).port(port).path(RECEIVE_PATH_PREFIX + path).toUriString();
	}

	@Override
	public void consume(Payload payload) {
		String payloadString = processorService.getScriptService().jsonStringify(payload);
		processorService.postForEntityWithErrors(uri, payloadString, Void.class, hostname, port, path,
				() -> new ProcessorPayloadExecutionScope(this, payload));
	}

}
