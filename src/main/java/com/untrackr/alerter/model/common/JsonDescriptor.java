package com.untrackr.alerter.model.common;

import jdk.nashorn.api.scripting.ScriptObjectMirror;

/**
 * Wrapper for a script object mirror
 */
public class JsonDescriptor {

	private ScriptObjectMirror objectMirror;

	public JsonDescriptor(ScriptObjectMirror objectMirror) {
		this.objectMirror = objectMirror;
	}

	public ScriptObjectMirror getObjectMirror() {
		return objectMirror;
	}

}
