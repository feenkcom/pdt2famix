package com.feenk.pdt2famix.exporter.client;

import java.util.Arrays;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

import com.feenk.pdt2famix.AnnotationResolver;
import com.feenk.pdt2famix.Importer;

/**
 * This class controls all aspects of the application's execution
 */
public class Application implements IApplication {

	@Override
	public Object start(IApplicationContext context) throws Exception {
		
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		System.out.println(root.getLocation().toString());
		
		IProject project = root.getProject("elm-symphony");
		System.out.println(project.getLocation().toString());
		
		Importer importer;
		
		try {
//			project.open(null /* IProgressMonitor */);
			
			project.close(new NullProgressMonitor() /* IProgressMonitor */);
			project.open(new NullProgressMonitor() /* IProgressMonitor */);
			project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
			project.build(IncrementalProjectBuilder.FULL_BUILD, null);
			ResourcesPlugin.getWorkspace().save(true, null);
			project.open(new NullProgressMonitor() /* IProgressMonitor */);
			
			
			IScriptProject projectPHP = DLTKCore.create(project);
			AnnotationResolver annotationResolver = new AnnotationResolver(
					"Annotation",  // DoctrineCoreConstants.ANNOTATION_TAG_NAME
					"Doctrine\\Common\\Annotations\\Annotation"); // DoctrineCoreConstants.DEFAULT_ANNOTATION_NAMESPACE
			importer = new Importer(projectPHP, annotationResolver);
			
			System.out.println("Start model construction:");
			importer.run(projectPHP, Arrays.asList(new String[] {"app", "src", "tests"}), true);
			System.out.println("done running");
		} catch (Exception e) {
			throw new RuntimeException("Model Generation Error", e);
		}
		System.out.println("Completed model construction");
		
		System.out.println("Start MSE export");
		IPath projectPath = project.getFullPath().makeAbsolute();
		String mseFileName = projectPath.segment(projectPath.segmentCount() - 1) + ".mse";
		importer.exportMSE(
			ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() +
			//project.getFullPath().toString() +
			"/"+
			mseFileName);
		System.out.println("Completed MSE export");
		
		return IApplication.EXIT_OK;
	}

	@Override
	public void stop() {
		// nothing to do
	}
}
