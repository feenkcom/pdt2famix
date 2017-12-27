package com.feenk.pdt2famix.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.feenk.pdt2famix.exporter.inphp.SymfonyProjectExporter;

/**
 * A handler for exporting the MSE model of a project.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class ExportModelHandler extends AbstractHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {			
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		ISelectionService service = window.getSelectionService();
		IStructuredSelection structured = (IStructuredSelection) service.getSelection();
	 
		IProject project;
		
		if (structured.getFirstElement() instanceof IProject) {
			project = (IProject) structured.getFirstElement();
		} else {
			throw new ExecutionException("No project selected");
		}
		
		try {
			project.open(null /* IProgressMonitor */);
		} catch (Exception e) {
			throw new ExecutionException("Model Generation Error", e);
		}
		
		SymfonyProjectExporter projectExporter = new SymfonyProjectExporter();
		projectExporter.exportProject(project);
		
		return null;
	}
	
}
