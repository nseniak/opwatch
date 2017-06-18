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

import org.opwatch.processor.payload.Payload;
import org.opwatch.service.ProcessorService;

public class MessageContext {

	enum EmitterType {
		global, processor, factory
	}

	private EmitterType emitterType;
	private String hostname;
	private String serviceId;
	private String processorName;
	private Payload payload;
	private ScriptStack stack;

	private MessageContext() {
	}

	private MessageContext(EmitterType emitterType,
												 String hostname,
												 String serviceId,
												 String processorName,
												 Payload payload,
												 ScriptStack stack) {
		this.emitterType = emitterType;
		this.hostname = hostname;
		this.serviceId = serviceId;
		this.processorName = processorName;
		this.payload = payload;
		this.stack = stack;
	}

	public static MessageContext makeGlobal(ProcessorService processorService, ScriptStack stack) {
		String hostname = processorService.hostName();
		String serviceId = processorService.getId();
		return new MessageContext(EmitterType.global, hostname, serviceId, null, null, stack);
	}

	public static MessageContext makeProcessor(ProcessorService processorService, String processorName, Payload payload, ScriptStack stack) {
		String hostname = processorService.hostName();
		String serviceId = processorService.getId();
		return new MessageContext(EmitterType.global, hostname, serviceId, processorName, payload, stack);
	}

	public static MessageContext makeFactory(ProcessorService processorService, String processorName, ScriptStack stack) {
		String hostname = processorService.hostName();
		String serviceId = processorService.getId();
		return new MessageContext(EmitterType.global, hostname, serviceId, processorName, null, stack);
	}

	public String descriptor() {
		switch (emitterType) {
			case global:
				return hostname;
			case processor:
			case factory:
				return processorName + " on " + hostname;
		}
		throw new IllegalStateException("unknown kind: " + emitterType.name());
	}

	public EmitterType getEmitterType() {
		return emitterType;
	}

	public void setEmitterType(EmitterType emitterType) {
		this.emitterType = emitterType;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

	public String getServiceId() {
		return serviceId;
	}

	public void setServiceId(String serviceId) {
		this.serviceId = serviceId;
	}

	public String getProcessorName() {
		return processorName;
	}

	public void setProcessorName(String processorName) {
		this.processorName = processorName;
	}

	public Payload getPayload() {
		return payload;
	}

	public void setPayload(Payload payload) {
		this.payload = payload;
	}

	public ScriptStack getStack() {
		return stack;
	}

	public void setStack(ScriptStack stack) {
		this.stack = stack;
	}

}
