package com.feenk.pdt2famix.inphp;

import java.util.Arrays;

import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.php.core.PHPToolkitUtil;
import org.eclipse.php.core.ast.nodes.ASTError;
import org.eclipse.php.core.ast.nodes.Assignment;
import org.eclipse.php.core.ast.nodes.Bindings;
import org.eclipse.php.core.ast.nodes.ClassDeclaration;
import org.eclipse.php.core.ast.nodes.FieldAccess;
import org.eclipse.php.core.ast.nodes.IMethodBinding;
import org.eclipse.php.core.ast.nodes.ITypeBinding;
import org.eclipse.php.core.ast.nodes.IVariableBinding;
import org.eclipse.php.core.ast.nodes.MethodDeclaration;
import org.eclipse.php.core.ast.nodes.NamespaceDeclaration;
import org.eclipse.php.core.ast.nodes.SingleFieldDeclaration;
import org.eclipse.php.core.ast.nodes.TraitDeclaration;
import org.eclipse.php.core.ast.visitor.AbstractVisitor;
import org.eclipse.php.core.compiler.PHPFlags;

import com.feenk.pdt2famix.Importer;
import com.feenk.pdt2famix.model.famix.Attribute;
import com.feenk.pdt2famix.model.famix.Method;
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
		
//	@Override
//	public boolean visit(Assignment assignment) {
//		((FieldAccess)assignment.getLeftHandSide()).resolveFieldBinding().getDeclaringClass().getKey();
//		// TODO Auto-generated method stub
//		return super.visit(assignment);
//	}
//	
//	
//	@Override
//	public boolean visit(FieldAccess fieldAccess) {
//		// TODO Auto-generated method stub
//		return super.visit(fieldAccess);
//	}
	
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
	
	// TYPES - CLASS
	
	@Override
	public boolean visit(ClassDeclaration classDeclaration) {
		logger.trace("visiting class declaration - " + classDeclaration.getName());
		
		ITypeBinding binding = classDeclaration.resolveTypeBinding();
		if (binding == null) {
			logNullBinding("class declaration", classDeclaration.getName(), classDeclaration.getStart());
			return false;
		}
		
		Type famixType = importer.ensureTypeFromTypeBinding(binding);

		//TODO: add superclass details in case there is no binding to the superclass;
		
		famixType.setIsStub(false);
		importer.createSourceAnchor(famixType, classDeclaration);
//		importer.ensureCommentFromBodyDeclaration(type, node);
		importer.pushOnContainerStack(famixType);
				
		return true;
	}
	
	@Override
	public void endVisit(ClassDeclaration classDeclaration) {
		importer.popFromContainerStack();
	}
	
	// TYPES - TRAIT
	
	@Override
	public boolean visit(TraitDeclaration traitDeclaration) {
		logger.trace("visiting trait declaration - " + traitDeclaration.getName());
		
		ITypeBinding binding = traitDeclaration.resolveTypeBinding();
		if (binding == null) {
			logNullBinding("trait declaration", traitDeclaration.getName(), traitDeclaration.getStart());
			return false;
		}
		
		Type famixType = importer.ensureTypeFromTypeBinding(binding);
		famixType.setIsStub(false);
		importer.createSourceAnchor(famixType, traitDeclaration);
//		importer.ensureCommentFromBodyDeclaration(type, node);
		importer.pushOnContainerStack(famixType);
		
		return true;
	}

	@Override
	public void endVisit(TraitDeclaration node) {
		importer.popFromContainerStack();
	}
	
	// TYPES - METHODS
	
	@Override
	public boolean visit(MethodDeclaration methodDeclarationNode) {
		IMethodBinding methodBinding = methodDeclarationNode.resolveMethodBinding();
		ITypeBinding declatingClass  = methodBinding.getDeclaringClass();
		ITypeBinding[] returnType    = methodBinding.getReturnType();
		
		//returnType[0].isPrimitive();
		
		IModelElement modelElement = methodBinding.getPHPElement();
		logger.trace(((IMethod)modelElement).getFullyQualifiedName());
		logger.trace(methodBinding.getKey()); 
		
		
		Method famixMethod;
		if (methodBinding != null) {
			famixMethod = importer.ensureMethodFromMethodBindingToCurrentContainer(methodBinding);
		}
		else {
			logNullBinding("method declaration", methodDeclarationNode.getFunction().getFunctionName(), methodDeclarationNode.getStart());
//			famixMethod = importer.ensureMethodFromMethodDeclaration(methodDeclarationNode);
			return true;
		}
		famixMethod.setIsStub(false);
		importer.pushOnContainerStack(famixMethod);
		
//		node.parameters().
//			stream().
//			forEach(p -> 
//				importer.ensureParameterFromSingleVariableDeclaration((SingleVariableDeclaration) p, method));
		importer.createSourceAnchor(famixMethod, methodDeclarationNode);
//		importer.ensureCommentFromBodyDeclaration(method, node);
		return true;

	}
	
	@Override
	public void endVisit(MethodDeclaration methodDeclaration) {
		importer.popFromContainerStack();
	}
	
	
	/**
	 * Skip the visitor here for the moment. It seems that the binding resolver does not return a variable binding for the parameters.
	 * @param variableBinding
	 */
	@Override 
	public boolean visit(SingleFieldDeclaration fieldDeclaration) {
		logger.trace("visiting single field declaration - " + fieldDeclaration.getName());
			
		IVariableBinding variableBinding = fieldDeclaration.getName().resolveVariableBinding();
		Attribute attribute;
		
		if (variableBinding == null) {
			attribute = importer.ensureAttributeFromFieldDeclarationIntoParentType(fieldDeclaration);
			// Bindings.findFieldInType(null, fieldDeclaration.getName().toString());
		}
		else {
			throw new RuntimeException("Would ne nice to see this error.");
			//attribute = importer.ensureAttributeForVariableBinding(variableBinding);
		}
		
		importer.createSourceAnchor(attribute, fieldDeclaration);
		//importer.ensureCommentFromBodyDeclaration(attribute, field);
		attribute.setIsStub(false);
		
		return true;
	}
	
	private void visit(IVariableBinding variableBinding) {
//		Attribute attribute = importer.ensureAttributeForVariableBinding(variableBinding);	
//		attribute.setIsStub(false);
	}

}
