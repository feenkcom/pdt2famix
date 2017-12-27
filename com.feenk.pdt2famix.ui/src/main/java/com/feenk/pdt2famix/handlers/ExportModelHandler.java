package com.feenk.pdt2famix.handlers;

import java.util.Arrays;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.feenk.pdt2famix.AnnotationResolver;
import com.feenk.pdt2famix.Importer;

import pdt2famix_plugin.Activator;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class ExportModelHandler extends AbstractHandler {
	private static final Activator logger = Activator.getDefault();
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {	
//		String indexerId1 = System.getProperty(DLTKCore.INDEXER_ID);
//		String indexerId2 = Platform.getPreferencesService().getString(DLTKCore.PLUGIN_ID,
//				DLTKCore.INDEXER_ID, null, null);
//		IConfigurationElement[] elements = Platform.getExtensionRegistry()
//				.getConfigurationElementsFor(DLTKCore.PLUGIN_ID + ".indexer");
		
		
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		ISelectionService service = window.getSelectionService();
		IStructuredSelection structured = (IStructuredSelection) service.getSelection();
	 
		IProject project;
		Importer importer;
		
		if (structured.getFirstElement() instanceof IProject) {
			project = (IProject) structured.getFirstElement();
		} else {
			// Default to elm-symphony; should remove this 
			IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
			project = root.getProject("elm-symphony");
		}
		
		logger.resetExternalLogFiles();
		logger.trace("Start model construction: "+project.getLocation());
		try {
			project.open(null /* IProgressMonitor */);
			IScriptProject projectPHP = DLTKCore.create(project);
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
		IPath projectPath = project.getFullPath().makeAbsolute();
		String mseFileName = projectPath.segment(projectPath.segmentCount() - 1) + ".mse";
		importer.exportMSE(
			ResourcesPlugin.getWorkspace().getRoot().getLocation().toString() +
			//project.getFullPath().toString() +
			"/"+
			mseFileName);
		logger.trace("Completed MSE export");
		
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
