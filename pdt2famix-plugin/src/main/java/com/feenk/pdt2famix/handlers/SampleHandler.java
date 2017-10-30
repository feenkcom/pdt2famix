package com.feenk.pdt2famix.handlers;

import java.util.ArrayList;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IScriptProject;

import com.feenk.pdt2famix.Importer;

import pdt2famix_plugin.Activator;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class SampleHandler extends AbstractHandler {
	private static final Activator logger = Activator.getDefault();
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		logger.trace("execute ");
		
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject("pdt2famix-exporter-samples");
		Importer importer = new Importer();
		
		try {
			project.open(null /* IProgressMonitor */);
			IScriptProject projectPHP = DLTKCore.create(project);
			logger.trace("run");
			importer.run(projectPHP, new ArrayList<>());
			logger.trace("done running");
		} catch (Exception e) {
			throw new ExecutionException("Model Generation Error", e);
		}
		logger.trace("finished model construction");
		
		IPath projectPath = project.getFullPath().makeAbsolute();
		String mseFileName = projectPath.segment(projectPath.segmentCount() - 1) + ".mse";
		importer.exportMSE(
			ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() +
			project.getFullPath().toString() +
			"/"+
			mseFileName);
		
		return null;
	}

	
	
//	void processContainer(IContainer container, IScriptProject PHPProject) throws CoreException {
//	   IResource [] members = container.members();
//	   for (IResource member : members) {
//	      if (member instanceof IContainer) {
//	         processContainer((IContainer)member, PHPProject); }
//	      else if (member instanceof IFile) {
//	    	  	IFile currentFile = (IFile)member;
//	    	  	
//	    	  	IProjectFragment fragment = PHPProject.getProjectFragment(member.getFullPath());
//	    	  	ASTParser parser = ASTParser.newParser(PHPVersion.PHP7_1);
//
//			IType lwType = PHPProject.findType("Foo");
//			ISourceModule lwSourceModule = lwType.getSourceModule();
//				
//	       }
//	    }
//	}
}
