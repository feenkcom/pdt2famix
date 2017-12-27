package com.feenk.pdt2famix.exporter.client;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

import com.feenk.pdt2famix.exporter.inphp.SymfonyProjectExporter;

/**
 * This class controls all aspects of the application's execution
 */
public class ExportModelApplication implements IApplication {

	@Override
	public Object start(IApplicationContext context) throws Exception {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject("elm-symphony");
				
		// project.open(null /* IProgressMonitor */);
		project.close(new NullProgressMonitor() /* IProgressMonitor */);
		project.open(new NullProgressMonitor() /* IProgressMonitor */);
		project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		project.build(IncrementalProjectBuilder.FULL_BUILD, null);
		ResourcesPlugin.getWorkspace().save(true, null);
		project.open(new NullProgressMonitor() /* IProgressMonitor */);
		
		SymfonyProjectExporter projectExporter = new SymfonyProjectExporter();
		projectExporter.exportProject(project);
		
		return IApplication.EXIT_OK;
	}

	@Override
	public void stop() {
		// nothing to do
	}
}
