package com.untrackr.alerter.processor.common;

import com.untrackr.alerter.common.SinglyLinkedList;

import java.io.File;
import java.util.StringJoiner;

public class IncludePath {

	private SinglyLinkedList<LoadedFile> reversePathList;

	public IncludePath() {
		this.reversePathList = null;
	}

	public IncludePath(SinglyLinkedList<LoadedFile> reversePathList) {
		this.reversePathList = reversePathList;
	}

	public IncludePath append(LoadedFile element) {
		SinglyLinkedList<LoadedFile> extendedPath = new SinglyLinkedList<>(element, reversePathList);
		return new IncludePath(extendedPath);
	}

	public boolean isEmpty() {
		return reversePathList == null;
	}

	public LoadedFile last() {
		return reversePathList.getHead();
	}

	public String pathDescriptor() {
		if (reversePathList == null) {
			return "";
		}
		StringJoiner joiner = new StringJoiner(" > ");
		reversePathList.reverse().forEach(element -> joiner.add(element.getFilename()));
		return joiner.toString();
	}

	public static class LoadedFile {

		private String filename;
		private File file;

		public LoadedFile(String filename, File file) {
			this.filename = filename;
			this.file = file;
		}

		public String getFilename() {
			return filename;
		}

		public File getFile() {
			return file;
		}

	}

}
