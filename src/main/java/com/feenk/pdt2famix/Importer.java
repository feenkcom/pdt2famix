package com.feenk.pdt2famix;

import org.eclipse.php.core.PHPVersion;
import org.eclipse.php.core.ast.nodes.ASTParser;

import com.feenk.pdt2famix.model.famix.Attribute;
import com.feenk.pdt2famix.model.famix.Method;
import com.feenk.pdt2famix.NamedEntityAccumulator;
import com.feenk.pdt2famix.model.famix.Type;
import ch.akuhn.fame.Repository;

public class Importer {	
	
	private Repository repository;
	public Repository repository() { return repository; }
	
	private NamedEntityAccumulator<Type> types; 
	public NamedEntityAccumulator<Type> types() {return types;}

	private NamedEntityAccumulator<Method> methods;
	public NamedEntityAccumulator<Method> methods() {return methods;}

	private NamedEntityAccumulator<Attribute> attributes;
	public NamedEntityAccumulator<Attribute> attributes() {return attributes;}
	
	
	/**
	 * Typically holds the prefix of the path of the root folder in which the importer was triggered.
	 * It is useful for creating relative paths for the source anchors  
	 */
	protected String ignoredRootPath;
	public String pathWithoutIgnoredRootPath(String originalPath) { 
		return originalPath.replaceAll("\\\\", "/").replaceFirst("^"+ignoredRootPath+"/", "");
	}

	/**
	 * Primary method to trigger the importer after having defined the 
	 * (1) {@link JavaFiles} with files to be parsed, and 
	 * (2) {@link Classpath} with dependencies
	 */
	public void run(JavaFiles javaFiles, Classpath classpath) {
		ignoredRootPath = javaFiles.ignoredRootPath().replaceAll("\\\\", "/");
		ASTParser parser = ASTParser.newParser(PHPVersion.PHP7_1);
		parser.createAST(null);
	}

	public void run(JavaFiles javaFiles) {
		this.run(javaFiles, new Classpath());
	}

	public void runOne(String oneFilePath) {
		JavaFiles javaFiles = new JavaFiles();
		javaFiles.oneJavaFile(oneFilePath);
		this.run(javaFiles, new Classpath());
	}

	public void exportMSE(String mseFileName) {
		// TODO implement :)
	}

}
