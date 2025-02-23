package com.feenk.pdt2famix.exporter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

public class Classpath {
	private Collection<String> classpath;

	public Classpath() {
		classpath = new ArrayList<String>();
		classpath.add(".");
	}

	public void deepJarFiles(String rootPath) {
		classpath.addAll(deepJarFilesIn(new File(rootPath)));
	}
	
	public String[] paths() {
		return classpath.toArray(new String[0]);
	}
	
	private Collection<String> deepJarFilesIn(File root) {
		Collection<String> all = new ArrayList<String>();
		for (File child : root.listFiles()) {
			if (child.isDirectory()) {
				all.addAll(deepJarFilesIn(child));
			}
			else {
				if (child.getName().endsWith(".jar"))
					all.add(child.getAbsolutePath());
			} 
		}
		return all;
	}

}
