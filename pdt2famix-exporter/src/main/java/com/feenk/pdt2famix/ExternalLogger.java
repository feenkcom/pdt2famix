package com.feenk.pdt2famix;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

import org.eclipse.core.resources.ResourcesPlugin;

public class ExternalLogger {

	public void resetExternalLogFiles () {
		logTraceMessage("", false);
		logErrorMessage("", false);
	}
	
	public void logTraceMessage(String message, boolean append) {
		String logFilePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString()+"/"+"pdt2famix-files.txt";
		String fullMessage = LocalDateTime.now().toString() + " - " + message + "\n";
		logExternalMessage(fullMessage, logFilePath, append);
	}
	
	public void logErrorMessage(String message, boolean append) {
		String logFilePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString()+"/"+"pdt2famix-problems.txt";
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
