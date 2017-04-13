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
	private Object body;
	private MessageContext context;
	private long timestamp;

	private Message() {
	}

	public static Message makeNew(Type type, Level level, String title, Object body, MessageContext context) {
		Message message = new Message();
		message.type = type;
		message.level = level;
		message.title = title;
		message.body = body;
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
