package com.untrackr.alerter.model.common;

public class Alert {

	public enum Priority {

		info(0),
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
	private AlertData data;
	private Integer retry;
	private Integer expire;
	private long timestamp;
	private boolean end;

	public Alert(Priority priority, String title, String message, AlertData data) {
		this.priority = priority;
		this.title = title;
		this.message = message;
		this.data = data;
	}

	public Alert(Priority priority, String title) {
		this(priority, title, null, null);
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

	public long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(long sentOn) {
		this.timestamp = sentOn;
	}

	public AlertData getData() {
		return data;
	}

	public void setData(AlertData data) {
		this.data = data;
	}

	public boolean isEnd() {
		return end;
	}

	public void setEnd(boolean end) {
		this.end = end;
	}

}
