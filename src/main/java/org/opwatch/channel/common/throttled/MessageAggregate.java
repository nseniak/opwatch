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

package org.opwatch.channel.common.throttled;

import org.opwatch.processor.common.Message;

import java.util.*;

public class MessageAggregate {

	private Map<String, AggregateMessagePart> messagePartMap = new LinkedHashMap<>();
	private Message.Level maxLevel = Message.Level.lowest;
	private Set<Message.Type> messageTypes = new LinkedHashSet<>();
	private int total;

	public void addMessage(String displayTitle, Message message) {
		AggregateMessagePart part = messagePartMap.computeIfAbsent(displayTitle, s -> new AggregateMessagePart(displayTitle));
		part.addMessage(message);
		messageTypes.add(message.getType());
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

	public Set<Message.Type> getMessageTypes() {
		return messageTypes;
	}

	public Message.Level getMaxLevel() {
		return maxLevel;
	}

	public int getTotal() {
		return total;
	}

}
