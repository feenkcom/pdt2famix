package com.feenk.pdt2famix.exporter.inphp;

import java.util.Arrays;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IScriptProject;

import pdt2famix_exporter.Activator;

public class SymfonyProjectExporter {
	private static final Activator logger = Activator.getDefault();

	public void exportProject(IProject project) throws ExecutionException{		
		IScriptProject projectPHP;
		Importer importer;
		
		try {
			projectPHP = DLTKCore.create(project);
			
			logger.resetExternalLogFiles(projectPHP);
			logger.trace("Start model construction: "+project.getLocation());
			
			AnnotationResolver annotationResolver = new AnnotationResolver(
					"Annotation",  // DoctrineCoreConstants.ANNOTATION_TAG_NAME
					"Doctrine\\Common\\Annotations\\Annotation"); // DoctrineCoreConstants.DEFAULT_ANNOTATION_NAMESPACE
			importer = new Importer(projectPHP, annotationResolver);
			
			importer.run(projectPHP, 
					Arrays.asList(new String[] {"app", "src", "tests"}), 
					true);
		} catch (Exception e) {
			throw new ExecutionException("Model Generation Error", e);
		}
		logger.trace("Completed model construction");

		logger.trace("Start MSE export");
		IPath absoluteProjectPath = ResourcesPlugin.getWorkspace().getRoot().findMember(projectPHP.getPath()).getLocation();
		IPath projectPath = project.getFullPath().makeAbsolute();
		String mseFileName = projectPath.segment(projectPath.segmentCount() - 1) + ".mse";
		importer.exportMSE(absoluteProjectPath.append(mseFileName).toString());
		logger.trace("Completed MSE export");
	}
	
}
