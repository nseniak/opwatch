package com.untrackr.alerter.channel.common.throttled;

import com.untrackr.alerter.processor.common.Message;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MessageAggregate {

	private Map<String, AggregateMessagePart> messagePartMap = new LinkedHashMap<>();
	private Message.Level maxLevel = Message.Level.lowest;
	private int total;

	public void addMessage(String displayTitle, Message message) {
		AggregateMessagePart part = messagePartMap.computeIfAbsent(displayTitle, s -> new AggregateMessagePart(displayTitle));
		part.addMessage(message);
		total = total + 1;
		if (part.getMaxLevel().getRank() > maxLevel.getRank()) {
			maxLevel = part.getMaxLevel();
		}
	}

	public List<AggregateMessagePart> messageParts() {
		List<AggregateMessagePart> parts = new ArrayList<>(messagePartMap.values());
		parts.sort((o1, o2) -> Integer.compare(o2.getMaxLevel().getRank(), o1.getMaxLevel().getRank()));
		return parts;
	}

	public static class AggregateMessagePart {

		private String displayTitle;
		private Message.Level maxLevel = Message.Level.lowest;
		private int count;

		public AggregateMessagePart(String displayTitle) {
			this.displayTitle = displayTitle;
		}

		public void addMessage(Message message) {
			if (message.getLevel().getRank() > maxLevel.getRank()) {
				maxLevel = message.getLevel();
			}
			count = count + 1;
		}

		public String titleWithCount() {
			String occurrences = " occurrence" + ((count == 1) ? "" : "s");
			return displayTitle + " (" + count + occurrences + ")";
		}

		public String getDisplayTitle() {
			return displayTitle;
		}

		public Message.Level getMaxLevel() {
			return maxLevel;
		}

		public int getCount() {
			return count;
		}

	}

	public Message.Level getMaxLevel() {
		return maxLevel;
	}

	public int getTotal() {
		return total;
	}

}
