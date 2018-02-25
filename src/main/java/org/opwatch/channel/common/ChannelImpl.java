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

package org.opwatch.channel.common;

import org.opwatch.processor.common.Message;
import org.opwatch.service.ProcessorService;

import static org.opwatch.processor.common.RuntimeError.DEFAULT_ERROR_MESSAGE_LEVEL;
import static org.opwatch.service.ProcessorService.INFO_DEFAULT_MESSAGE_LEVEL;
import static org.springframework.util.StringUtils.capitalize;

public abstract class ChannelImpl implements Channel {

	protected ProcessorService processorService;

	public ChannelImpl(ProcessorService processorService) {
		this.processorService = processorService;
	}

	protected String displayTitle(Message message) {
		String typeDesc = capitalize(message.getType().getDescriptor());
		String levelSuffix = levelSuffix(message.getLevel());
		switch (message.getType()) {
			case error: {
				String suffix = (message.getLevel() == DEFAULT_ERROR_MESSAGE_LEVEL) ? "" : levelSuffix(message.getLevel());
				return typeDesc + suffix + ": " + message.getTitle();
			}
			case info: {
				String suffix = (message.getLevel() == INFO_DEFAULT_MESSAGE_LEVEL) ? "" : levelSuffix;
				return typeDesc + suffix + ": " + message.getTitle();
			}
			case alert:
			case alertOn:
			case alertOff: {
				return typeDesc + levelSuffix + ": " + message.getTitle();
			}
		}
		throw new IllegalStateException("unknown type: " + message.getType());
	}

	protected String levelSuffix(Message.Level level) {
		switch (level) {
			case lowest:
				return "(lowest)";
			case low:
				return "(low)";
			case medium:
				return "(medium)";
			case high:
				return "(HIGH)";
			case emergency:
				return "(EMERGENCY)";
		}
		throw new IllegalStateException("unknown level: " + level.name());
	}

	protected String detailsString(Message message) {
		Object details = message.getDetails();
		if (details == null) {
			return null;
		} else if (details instanceof String) {
			return (String) details;
		} else {
			return processorService.getScriptService().pretty(details);
		}
	}

}
