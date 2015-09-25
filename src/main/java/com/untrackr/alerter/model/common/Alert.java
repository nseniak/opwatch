package com.untrackr.alerter.model.common;

public class Alert {

	public static int MAX_TITLE_LENGTH = 250;
	public static int MAX_MESSAGE_LENGTH = 1024;

	public enum Priority {

		low(0),
		normal(1),
		high(2),
		emergency(3);

		int severity;

		Priority(int severity) {
			this.severity = severity;
		}

		public int getSeverity() {
			return severity;
		}

	}

	private Priority priority;
	private String title;
	private String message;
	private Integer retry;
	private Integer expire;

	public Alert(Priority priority, String title, String message) {
		this.priority = priority;
		this.title = truncate(title, MAX_TITLE_LENGTH);
		this.message = truncate(message, MAX_MESSAGE_LENGTH);
	}

	private String truncate(String str, int maxLength) {
		if (str.length() <= maxLength) {
			return str;
		} else {
			return str.substring(0, Math.min(str.length(), maxLength));
		}
	}

	@Override
	public String toString() {
		return "Alert{" +
				"priority=" + priority +
				", title='" + title + '\'' +
				", message='" + message + '\'' +
				", retry=" + retry +
				", expire=" + expire +
				'}';
	}

	public Priority getPriority() {
		return priority;
	}

	public void setPriority(Priority priority) {
		this.priority = priority;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Integer getRetry() {
		return retry;
	}

	public void setRetry(Integer retry) {
		this.retry = retry;
	}

	public Integer getExpire() {
		return expire;
	}

	public void setExpire(Integer expire) {
		this.expire = expire;
	}

}
