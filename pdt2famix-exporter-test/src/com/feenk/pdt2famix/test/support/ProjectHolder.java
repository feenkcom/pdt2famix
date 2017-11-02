package com.feenk.pdt2famix.test.support;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
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
		project.close(new NullProgressMonitor() /* IProgressMonitor */);
		project.open(new NullProgressMonitor() /* IProgressMonitor */);
		project.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		ResourcesPlugin.getWorkspace().save(true, null);
		project.open(new NullProgressMonitor() /* IProgressMonitor */);
		
		projectPHP = DLTKCore.create(project); 
	}
	
	
	
}
