package com.untrackr.alerter.model.descriptor;

import com.untrackr.alerter.common.SinglyLinkedList;

import java.util.StringJoiner;

public class IncludePath {

	private SinglyLinkedList<String> reversePathList;

	public IncludePath() {
		this.reversePathList = null;
	}

	public IncludePath(SinglyLinkedList<String> reversePathList) {
		this.reversePathList = reversePathList;
	}

	public IncludePath append(String pathname) {
		SinglyLinkedList<String> extendedPath = new SinglyLinkedList<>(pathname, reversePathList);
		return new IncludePath(extendedPath);
	}

	public boolean isEmpty() {
		return reversePathList == null;
	}

	public String last() {
		return reversePathList.getHead();
	}

	public String path() {
		if (reversePathList == null) {
			return "";
		}
		StringJoiner joiner = new StringJoiner(" > ");
		reversePathList.reverse().forEach(joiner::add);
		return joiner.toString();
	}

}
