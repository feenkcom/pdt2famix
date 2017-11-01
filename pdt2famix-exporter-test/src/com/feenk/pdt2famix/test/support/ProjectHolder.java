package com.feenk.pdt2famix.test.support;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IScriptProject;

public class ProjectHolder {
	
	private static IProject project;
	private static IScriptProject projectPHP;
	
	public static IScriptProject getProject(String projectName) throws Exception {
		if (projectPHP == null) {
			createProject(projectName);
		}
		return projectPHP;
	}
	
	public static void createProject(String projectName) throws Exception {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		project = root.getProject(projectName);
		project.open(null /* IProgressMonitor */);
		project.close(null /* IProgressMonitor */);
		project.open(null /* IProgressMonitor */);
		project.refreshLocal(IResource.DEPTH_INFINITE, null);
		projectPHP = DLTKCore.create(project); 
	}
}
