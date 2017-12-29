package com.feenk.pdt2famix.client;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.app.IApplication;
import org.eclipse.equinox.app.IApplicationContext;

import com.feenk.pdt2famix.exporter.inphp.SymfonyProjectExporter;

import pdt2famix_exporter_client.Activator;

/**
 * This class controls all aspects of the application's execution
 */
public class ExportModelApplication implements IApplication {

	@Override
	public Object start(IApplicationContext context) throws Exception {
		String projectName  = (String)getArgumentValue("-pdt2famixProject", context);
		if (projectName == null) {
			throw new Exception("Missing project name");
		}
		trace("Analyze project: "+projectName);
		
		if (hasArgumentFlag("-pdt2famixCleanWorkspace", context)) {
			trace("Clean workspace");
			Platform.getLog(Activator.getContext().getBundle());
			
			ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.CLEAN_BUILD, null);
		}
		if (hasArgumentFlag("-pdt2famixBuildWorkspace", context)) {
			trace("Build workspace");
			ResourcesPlugin.getWorkspace().build(IncrementalProjectBuilder.FULL_BUILD, null);
//			ResourcesPlugin.getWorkspace().save(true, null);
		}
		
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(projectName);
		project.open(new NullProgressMonitor());
		
		if (hasArgumentFlag("-pdt2famixCleanProject", context)) {
			trace("Clean project");
			project.build(IncrementalProjectBuilder.CLEAN_BUILD, null);
		}
		if (hasArgumentFlag("-pdt2famixBuildProject", context)) {
			trace("Build project");
//			project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
			project.build(IncrementalProjectBuilder.FULL_BUILD, null);
		}
		
		SymfonyProjectExporter projectExporter = new SymfonyProjectExporter();
		projectExporter.exportProject(project);
		
		return IApplication.EXIT_OK;
	}

	private boolean hasArgumentFlag(String argumentName, IApplicationContext context) {
		Object[] arguments = (Object[])context.getArguments().get(IApplicationContext.APPLICATION_ARGS);
		for (int index = 0; index < arguments.length; index++) {
			if (arguments[index].equals(argumentName)) return true;
		}
		return false;
	}
	
	private Object getArgumentValue(String argumentName, IApplicationContext context) {
		//Platform.getApplicationArgs();
		List<Object> arguments = Arrays.asList((Object[])context.getArguments().get(IApplicationContext.APPLICATION_ARGS));
		int argumentIndex = arguments.indexOf(argumentName);
		
		if (argumentIndex == -1) {
			return null;
		}
		if (argumentIndex+1>=arguments.size()) {
			return null;
		}
		return arguments.get(argumentIndex+1);
	}
	
	private void trace(String logMessage) {
		System.out.println(logMessage);
		Platform.getLog(Activator.getContext().getBundle()).log(new Status(IStatus.INFO, Activator.PLUGIN_ID, logMessage));
	}
	
	@Override
	public void stop() {
		// nothing to do
	}
}
