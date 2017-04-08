package com.untrackr.alerter.processor.common;

public class Message {

	public enum Type {

		error("error"),
		info("info"),
		alert("alert"),
		alertStart("start of alert"),
		alertEnd("end of alert");

		String descriptor;

		Type(String descriptor) {
			this.descriptor = descriptor;
		}

		public String getDescriptor() {
			return descriptor;
		}

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
	private Object body;
	private MessageContext context;
	private long timestamp;

	public Message(Type type, Level level, String title, Object body, MessageContext context) {
		this.type = type;
		this.level = level;
		this.title = title;
		this.body = body;
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

	public Object getBody() {
		return body;
	}

	public long getTimestamp() {
		return timestamp;
	}

	public MessageContext getContext() {
		return context;
	}

}
