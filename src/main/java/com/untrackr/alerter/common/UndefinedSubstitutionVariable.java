package com.untrackr.alerter.common;

public class UndefinedSubstitutionVariable extends Exception {

	String name;

	public UndefinedSubstitutionVariable(String name) {
		super("undefined substitution variable: " + name);
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
