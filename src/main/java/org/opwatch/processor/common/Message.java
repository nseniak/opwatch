package org.opwatch.processor.common;

public class Message {

	public enum Type {

		error("error", true),
		info("info", true),
		alert("alert", false),
		alertOn("alert ON", false),
		alertOff("alert OFF", false);

		private String descriptor;
		private boolean system;

		Type(String descriptor, boolean system) {
			this.descriptor = descriptor;
			this.system = system;
		}

		public String getDescriptor() {
			return descriptor;
		}

		public boolean isSystem() {
			return system;
		}

	}

	public enum Level {

		lowest(0),
		low(1),
		medium(2),
		high(3),
		emergency(4);

		int rank;

		Level(int rank) {
			this.rank = rank;
		}

		public int getRank() {
			return rank;
		}

	}

	private Type type;
	private Level level;
	private String title;
	private Object details;
	private MessageContext context;
	private long timestamp;

	private Message() {
	}

	public static Message makeNew(Type type, Level level, String title, Object details, MessageContext context) {
		Message message = new Message();
		message.type = type;
		message.level = level;
		message.title = title;
		message.details = details;
		message.context = context;
		message.timestamp = System.currentTimeMillis();
		return message;
	}

	public Type getType() {
		return type;
	}

	public Level getLevel() {
		return level;
	}

	public String getTitle() {
		return title;
	}

	public Object getDetails() {
		return details;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public MessageContext getContext() {
		return context;
	}

}
