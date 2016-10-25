package com.untrackr.alerter.model.common;

import com.untrackr.alerter.processor.common.Processor;

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
	private Processor emitter;
	private AlertData data;
	private Integer retry;
	private Integer expire;
	private long timestamp;
	private boolean end;
	private PushoverKey pushoverKey;

	public Alert(PushoverKey pushoverKey, Priority priority, String title, String message, Processor emitter, AlertData data) {
		this.pushoverKey = pushoverKey;
		this.priority = priority;
		this.title = title;
		this.message = message;
		this.emitter = emitter;
		this.data = data;
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

	public Processor getEmitter() {
		return emitter;
	}

	public void setEmitter(Processor emitter) {
		this.emitter = emitter;
	}

	public PushoverKey getPushoverKey() {
		return pushoverKey;
	}

	public void setPushoverKey(PushoverKey pushoverKey) {
		this.pushoverKey = pushoverKey;
	}

}
