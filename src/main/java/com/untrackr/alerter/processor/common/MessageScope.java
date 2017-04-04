package com.untrackr.alerter.processor.common;

public class MessageScope {

	enum Kind {
		global, processor, factory
	}

	private String id;
	private String name;
	private Kind kind;
	private String hostname;

	public MessageScope(String id, String name, Kind kind, String hostname) {
		this.id = id;
		this.name = name;
		this.kind = kind;
		this.hostname = hostname;
	}

	public String descriptor() {
		switch (kind) {
			case global:
				return hostname;
			case processor:
			case factory:
				return name + " on " + hostname;
		}
		throw new IllegalStateException("unknown kind: " + kind.name());
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Kind getKind() {
		return kind;
	}

	public void setKind(Kind kind) {
		this.kind = kind;
	}

	public String getHostname() {
		return hostname;
	}

	public void setHostname(String hostname) {
		this.hostname = hostname;
	}

}
