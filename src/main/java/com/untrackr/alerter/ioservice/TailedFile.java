package com.untrackr.alerter.ioservice;

import com.untrackr.alerter.model.common.AlerterProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class TailedFile {

	private static final Logger logger = LoggerFactory.getLogger(TailedFile.class);

	private File file;
	private TailLineHandler handler;
	private AlerterProfile alerterProfile;

	public TailedFile(AlerterProfile alerterProfile, File file, TailLineHandler handler) {
		this.alerterProfile = alerterProfile;
		this.file = file;
		this.handler = handler;
	}

	public void tail() throws InterruptedException, IOException {
		FileReader reader = null;
		try {
			while (true) {
				if ((reader = openFile(file)) == null) {
					logger.info("Waiting for file: " + file);
					while ((reader = openFile(file)) == null) {
						Thread.sleep(alerterProfile.getTailedFileWatchingCheckDelay());
					}
					logger.info("File created: " + file);
				}
				int lineNumber = 0;
				BufferedReader buffered = new BufferedReader(reader);
				// Go to the tail
				while (buffered.readLine() != null) {
					lineNumber = lineNumber + 1;
					// Continue
				}
				long fileLength = file.length();
				String canonicalPath = file.getCanonicalPath();
				while (true) {
					if (!file.exists()) {
						logger.info("File deleted: " + file);
						break;
					}
					if (file.length() < fileLength) {
						// File has been truncated
						logger.info("File truncated: " + file);
						break;
					}
					if (!file.getCanonicalPath().equals(canonicalPath)) {
						// File has been truncated
						logger.info("File location has changed: " + file);
						break;
					}
					fileLength = file.length();
					String line = buffered.readLine();
					if (line == null) {
						// end of file, start polling
						Thread.sleep(alerterProfile.getTailPollInterval());
					} else {
						lineNumber = lineNumber + 1;
						handler.handle(line, lineNumber);
					}
				}
			}
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					// Do nothing
				}
			}
		}
	}

	private FileReader openFile(File file) {
		try {
			return new FileReader(file);
		} catch (FileNotFoundException e) {
			return null;
		}
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

}