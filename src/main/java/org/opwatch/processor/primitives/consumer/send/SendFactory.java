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

import org.opwatch.processor.common.ActiveProcessorFactory;
import org.opwatch.processor.common.ProcessorSignature;
import org.opwatch.service.ProcessorService;

import java.util.regex.Pattern;

public class SendFactory extends ActiveProcessorFactory<SendConfig, Send> {

	public SendFactory(ProcessorService processorService) {
		super(processorService);
	}

	@Override
	public String name() {
		return "send";
	}

	@Override
	public Class<SendConfig> configurationClass() {
		return SendConfig.class;
	}

	@Override
	public Class<Send> processorClass() {
		return Send.class;
	}

	@Override
	public ProcessorSignature staticSignature() {
		return ProcessorSignature.makeConsumer();
	}

	private static Pattern pathPattern = Pattern.compile("(?<hostname>[^:/]+)?(?::(?<port>[0-9]+))?(?<stack>/.*)");

	@Override
	public Send make(Object scriptObject) {
		SendConfig config = convertProcessorConfig(scriptObject);
		String path = checkPropertyValue("path", config.getPath());
		if (path.startsWith("/")) {
			path = path.substring(1);
		}
		String hostname = checkPropertyValue("hostname", config.getHostname());
		int port =  (config.getPort() != null) ? config.getPort() : processorService.config().defaultPostPort();
		return new Send(getProcessorService(), config, name(), hostname, port, path);
	}

}
