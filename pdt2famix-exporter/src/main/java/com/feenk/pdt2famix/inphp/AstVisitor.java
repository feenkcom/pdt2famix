package com.feenk.pdt2famix.inphp;

import org.eclipse.php.core.ast.nodes.ClassDeclaration;
import org.eclipse.php.core.ast.nodes.ITypeBinding;
import org.eclipse.php.core.ast.nodes.NamespaceDeclaration;
import org.eclipse.php.core.ast.nodes.SingleFieldDeclaration;
import org.eclipse.php.core.ast.visitor.AbstractVisitor;
import org.eclipse.php.core.compiler.PHPFlags;

import com.feenk.pdt2famix.Importer;
import com.feenk.pdt2famix.model.famix.Namespace;
import com.feenk.pdt2famix.model.famix.Type;

import pdt2famix_exporter.Activator;

public class AstVisitor extends AbstractVisitor {
	private static final Activator logger = Activator.getDefault();
	
	private Importer importer;
	
	public AstVisitor(Importer importer) {
		this.importer = importer;
	}
		
	public void logNullBinding(String string, Object extraData, int lineNumber) {
		importer.logNullBinding(string, extraData, lineNumber);
	}

	private void logBinding(ITypeBinding binding) {
		logger.trace(
			"binding: "+ binding.getClass().getName()+"; " + 
			"kind: " + binding.getKind() + "; "+
			"name: " + binding.getName() + "; "+
			"evaluatedType: " + binding.getEvaluatedType());
	}
	
	private String getModifierDescription(int modifiers) {
		String description = "";
		if (PHPFlags.isPublic(modifiers))
			description += "public ";
		if (PHPFlags.isProtected(modifiers))
			description += "protected ";
		if (PHPFlags.isPrivate(modifiers))
			description += "private";
		if (description.isEmpty()) {
			description += "other";
		}
		return description;
	}
	
	
	// NAMESPACES 
	
	@Override
	public boolean visit(NamespaceDeclaration namespaceDeclaration) {
		logger.trace("visiting namespace declaration - " + namespaceDeclaration.getName());
		Namespace namespace = importer.ensureNamespaceFromNamespaceDeclaration(namespaceDeclaration);
		importer.pushOnContainerStack(namespace);
		return true;
	}
	
	@Override
	public void endVisit(NamespaceDeclaration namespaceDeclaration) {
		importer.popFromContainerStack();
	}
	
	// TYPES
	
	@Override
	public boolean visit(ClassDeclaration classDeclaration) {
		logger.trace("visiting class declaration - " + classDeclaration.getName());
		
		ITypeBinding binding = classDeclaration.resolveTypeBinding();
		if (binding == null) {
			logNullBinding("type declaration", classDeclaration.getName(), classDeclaration.getStart());
			return false;
		}
		Type type = importer.ensureTypeFromTypeBinding(binding);
		
		//TODO: add superclass details;
		
		type.setIsStub(false);
		importer.createSourceAnchor(type, classDeclaration);
//		importer.ensureCommentFromBodyDeclaration(type, node);
		importer.pushOnContainerStack(type);
		return true;
	}
	
	@Override
	public void endVisit(ClassDeclaration classDeclaration) {
		//importer.popFromContainerStack();
	}
	
	
	@Override
	public boolean visit(SingleFieldDeclaration singleFieldDeclaration) {
		logger.trace("visiting single field declaration - " + singleFieldDeclaration.getName());
		logBinding(singleFieldDeclaration.getValue().resolveTypeBinding());
		return true;
	}

}
