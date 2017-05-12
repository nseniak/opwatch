package org.opwatch.channel.common;

import org.opwatch.processor.common.Message;

import static org.opwatch.processor.common.RuntimeError.DEFAULT_ERROR_MESSAGE_LEVEL;
import static org.opwatch.service.ProcessorService.INFO_DEFAULT_MESSAGE_LEVEL;
import static org.springframework.util.StringUtils.capitalize;

public abstract class ChannelImpl implements Channel {

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
				return typeDesc  + suffix + ": " + message.getTitle();
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

}
