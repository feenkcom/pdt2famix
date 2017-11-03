package com.feenk.pdt2famix.inphp;

import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.php.core.ast.nodes.AnonymousClassDeclaration;
import org.eclipse.php.core.ast.nodes.ClassDeclaration;
import org.eclipse.php.core.ast.nodes.Expression;
import org.eclipse.php.core.ast.nodes.FieldAccess;
import org.eclipse.php.core.ast.nodes.IMethodBinding;
import org.eclipse.php.core.ast.nodes.ITypeBinding;
import org.eclipse.php.core.ast.nodes.IVariableBinding;
import org.eclipse.php.core.ast.nodes.InterfaceDeclaration;
import org.eclipse.php.core.ast.nodes.LambdaFunctionDeclaration;
import org.eclipse.php.core.ast.nodes.MethodDeclaration;
import org.eclipse.php.core.ast.nodes.MethodInvocation;
import org.eclipse.php.core.ast.nodes.NamespaceDeclaration;
import org.eclipse.php.core.ast.nodes.SingleFieldDeclaration;
import org.eclipse.php.core.ast.nodes.TraitDeclaration;
import org.eclipse.php.core.ast.nodes.TypeDeclaration;
import org.eclipse.php.core.ast.nodes.Variable;
import org.eclipse.php.core.ast.visitor.AbstractVisitor;

import com.feenk.pdt2famix.Importer;
import com.feenk.pdt2famix.model.famix.Attribute;
import com.feenk.pdt2famix.model.famix.Invocation;
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
	@Override
	public boolean visit(FieldAccess fieldAccess) {
		// TODO Auto-generated method stub
		return super.visit(fieldAccess);
	}
	
	@Override
	public boolean visit(LambdaFunctionDeclaration lambdaFunctionDeclaration) {
		return true;
	}

	@Override
	public boolean visit(AnonymousClassDeclaration anonymousClassDeclaration) {
		return true;
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
	
	// TYPES - CLASS
	
	@Override
	public boolean visit(ClassDeclaration classDeclaration) {
		logger.trace("visiting class declaration - " + classDeclaration.getName());
		visitTypeDeclaration(classDeclaration);
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
		visitTypeDeclaration(traitDeclaration);
		return true;
	}

	@Override
	public void endVisit(TraitDeclaration node) {
		importer.popFromContainerStack();
	}
	
	// TYPES - INTERFACES
	
	@Override
	public boolean visit(InterfaceDeclaration interfaceDeclaration) {
		logger.trace("visiting trait declaration - " + interfaceDeclaration.getName());
		visitTypeDeclaration(interfaceDeclaration);
		return true;
	}

	@Override
	public void endVisit(InterfaceDeclaration interfaceDeclaration) {
		importer.popFromContainerStack();
	}
	
	
	// TYPES - HELPER METHOD
	
	private void visitTypeDeclaration(TypeDeclaration typeDeclaration) {
		ITypeBinding binding = typeDeclaration.resolveTypeBinding();
		if (binding == null) {
			logNullBinding("type declaration", typeDeclaration.getName(), typeDeclaration.getStart());
			return ;
		}
		
		Type famixType = importer.ensureTypeFromTypeBinding(binding);
		famixType.setIsStub(false);
		importer.createSourceAnchor(famixType, typeDeclaration);
//		importer.ensureCommentFromBodyDeclaration(type, node);
		importer.pushOnContainerStack(famixType);
	}
	
	//  METHODS
	
	@Override
	public boolean visit(MethodDeclaration methodDeclarationNode) {
		IMethodBinding methodBinding = methodDeclarationNode.resolveMethodBinding();
		ITypeBinding declatingClass  = methodBinding.getDeclaringClass();
		ITypeBinding[] returnType    = methodBinding.getReturnType();
				
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
	
	
	// ATTRIBUTES
	
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
	
//	// CONSTANTS
//
//	@Override
//	public boolean visit(ConstantDeclaration constantDeclaration) {
//		logger.trace("visiting constant declaration - " + constantDeclaration.names());
//		
//		List<Identifier> names = constantDeclaration.names();
//		List<Expression> initializers = constantDeclaration.initializers();
//		for (int index = 0; index < names.size(); index++ ) {
//			Attribute attribute = importer.ensureConstant(names.get(index), initializers.get(index));
//			//importer.createSourceAnchor(attribute, fieldDeclaration);
//			//importer.ensureCommentFromBodyDeclaration(attribute, field);
//			attribute.setIsStub(false);
//		}
//		
//		return true;
//	}
	
	// METHOD INVOCATION
	
	@Override
	public boolean visit(MethodInvocation methodInvocation) {
//		methodInvocation.getMethod().getFunctionName().getName().toString();
//		methodInvocation.getDispatcher().resolveTypeBinding();
		// ((Variable)methodInvocation.getDispatcher()).resolveVariableBinding();
		IMethodBinding methodBinding = methodInvocation.resolveMethodBinding();
//		methodBinding.getParameterTypes();
//		methodInvocation.getMethod().resolveFunctionBinding();
		Expression dispatcherExpression = methodInvocation.getDispatcher();
		Invocation invocation = importer.createInvocationFromMethodBinding(methodInvocation.resolveMethodBinding());
		if (dispatcherExpression != null) {
			invocation.setReceiver(importer.ensureStructuralEntityFromExpression(dispatcherExpression));
		}
		//importer.createAccessFromExpression(methodInvocation.getMethod().);
//		node.arguments().stream().forEach(arg -> importer.createAccessFromExpression((Expression) arg));
		return true;

	}

}
