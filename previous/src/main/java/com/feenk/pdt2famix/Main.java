package com.feenk.pdt2famix;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {

    private static final Logger logger = LogManager.getLogger(Main.class);
	
	public static void main(String[] args) {
		Importer importer = new Importer();
		String pathName;
		if (args.length > 0)
			pathName = args[0];
		else 
			pathName = ".";
		Path path = Paths.get(pathName).toAbsolutePath().normalize();
		String mseFileName = path.getName(path.getNameCount() - 1) + ".mse";
		JavaFiles javaFiles = new JavaFiles();
		javaFiles.deepJavaFiles(path.toString());
		Classpath classpath = new Classpath();
		//classpath.deepJarFiles(path.toString());
		logger.trace("importing root folder - " + path.toString());
		importer.run(javaFiles, classpath);
		//logger.trace("exporting - " + mseFileName);
		//importer.exportMSE(mseFileName);
		//logger.trace("done");
	}

}
