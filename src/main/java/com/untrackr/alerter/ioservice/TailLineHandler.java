package com.untrackr.alerter.ioservice;

public interface TailLineHandler {

	void handle(String line, int lineNumber);

}
