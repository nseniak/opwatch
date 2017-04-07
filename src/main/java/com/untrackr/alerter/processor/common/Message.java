package com.untrackr.alerter.processor.common;

public class Message {

	public enum Type {
		error, info, alert, alertStart, alertEnd
	}

	public enum Level {

		lowest(0),
		low(1),
		medium(2),
		high(3),
		emergency(4);

		int level;

		Level(int level) {
			this.level = level;
		}

		public int getLevel() {
			return level;
		}

	}

	private Type type;
	private Level level;
	private String title;
	private MessageContext context;
	private long timestamp;

	public Message(Type type, Level level, String title, MessageContext context) {
		this.type = type;
		this.level = level;
		this.title = title;
		this.context = context;
		this.timestamp = System.currentTimeMillis();
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

	public long getTimestamp() {
		return timestamp;
	}

	public MessageContext getContext() {
		return context;
	}

}
