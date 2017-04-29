package org.opwatch.ioservice;

public interface FileEventHandler {

	/**
	 * Invoked when the file is seen for the first time or when it is modified
	 *
	 * @param watchedFile
	 */
	void handleContent(WatchedFile watchedFile);

	/**
	 * Invoked when the file is deleted
	 *
	 * @param watchedFile
	 */
	void handleDelete(WatchedFile watchedFile);

}
