package com.feenk.pdt2famix.exporter;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

import org.eclipse.core.runtime.IPath;

public class ExternalLogger {
	private static final String LOG_PROBLEMS_NAME = "pdt2famix-problems.txt";
	private static final String LOG_FILES_NAME = "pdt2famix-files.txt";
	private IPath logFolder;
	
	
	public void setLoggersFolder(IPath logFolder) {
		this.logFolder = logFolder;
	}
	
	public void resetExternalLogFiles() {
		logTraceMessage("", false);
		logErrorMessage("", false);
	}
	
	public void logTraceMessage(String message, boolean append) {
		if (logFolder == null) {
			return ;
		}
		String logFilePath = logFolder.append(LOG_FILES_NAME).toString();
		String fullMessage = LocalDateTime.now().toString() + " - " + message + "\n";
		logExternalMessage(fullMessage, logFilePath, append);
	}
	
	public void logErrorMessage(String message, boolean append) {
		if (logFolder == null) {
			return ;
		}
		String logFilePath = logFolder.append(LOG_PROBLEMS_NAME).toString();
		String fullMessage = LocalDateTime.now().toString() + " - " + message + "\n";
		logExternalMessage(fullMessage, logFilePath, append);
	}
	
	private void logExternalMessage(String message, String filePath, boolean append) {
		try (FileWriter writer = new FileWriter(filePath, append)) {
			writer.write(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
