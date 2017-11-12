package com.feenk.pdt2famix.inphp;

import java.util.List;

import org.eclipse.dltk.core.IModelElement;
import org.eclipse.php.core.ast.nodes.ASTNode;
import org.eclipse.php.core.ast.nodes.AnonymousClassDeclaration;
import org.eclipse.php.core.ast.nodes.Bindings;
import org.eclipse.php.core.ast.nodes.ClassDeclaration;
import org.eclipse.php.core.ast.nodes.ConstantDeclaration;
import org.eclipse.php.core.ast.nodes.Expression;
import org.eclipse.php.core.ast.nodes.FieldAccess;
import org.eclipse.php.core.ast.nodes.FormalParameter;
import org.eclipse.php.core.ast.nodes.IMethodBinding;
import org.eclipse.php.core.ast.nodes.ITypeBinding;
import org.eclipse.php.core.ast.nodes.IVariableBinding;
import org.eclipse.php.core.ast.nodes.Identifier;
import org.eclipse.php.core.ast.nodes.InterfaceDeclaration;
import org.eclipse.php.core.ast.nodes.LambdaFunctionDeclaration;
import org.eclipse.php.core.ast.nodes.MethodDeclaration;
import org.eclipse.php.core.ast.nodes.MethodInvocation;
import org.eclipse.php.core.ast.nodes.NamespaceDeclaration;
import org.eclipse.php.core.ast.nodes.NamespaceName;
import org.eclipse.php.core.ast.nodes.SingleFieldDeclaration;
import org.eclipse.php.core.ast.nodes.StaticConstantAccess;
import org.eclipse.php.core.ast.nodes.TraitDeclaration;
import org.eclipse.php.core.ast.nodes.TraitUseStatement;
import org.eclipse.php.core.ast.nodes.TypeDeclaration;
import org.eclipse.php.core.ast.visitor.AbstractVisitor;
import org.eclipse.php.core.compiler.PHPFlags;

import com.feenk.pdt2famix.Importer;
import com.feenk.pdt2famix.model.famix.Access;
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
	
	public void logInvalidBinding(String string, Object extraData) {
		importer.logInvalidBinding(string, extraData);
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
		visitTypeDeclaration(traitDeclaration);
		return true;
	}

	@Override
	public void endVisit(TraitDeclaration node) {
		importer.popFromContainerStack();
	}
	
	@Override
	public boolean visit(TraitUseStatement node) {
		for (NamespaceName traitName: node.getTraitList()) {
			ITypeBinding traitBinding = traitName.resolveTypeBinding();
			if (traitBinding != null) {
				importer.addTraitUsageToCurrentContainerForTraitBinding(traitBinding);				
			}
		}
		node.getTraitList().get(0).resolveTypeBinding();
		return true;
	}
	
	// TYPES - INTERFACES
	
	@Override
	public boolean visit(InterfaceDeclaration interfaceDeclaration) {
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
		if (importer.isValidTypeBinding(binding) == false) {
			logInvalidBinding("type declaration", typeDeclaration.getName());
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
		
		for (FormalParameter parameter: methodDeclarationNode.getFunction().formalParameters()) {
			importer	.parameterFromFormalParameterDeclaration(parameter, famixMethod, methodBinding.getKey());
		}
		
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
		Attribute attribute = importer.ensureAttributeForFieldDeclaration(fieldDeclaration);
		importer.createSourceAnchor(attribute, fieldDeclaration);
		//importer.ensureCommentFromBodyDeclaration(attribute, field);
		attribute.setIsStub(false);
		return true;
	}
	
	// CONSTANTS

	@Override
	public boolean visit(ConstantDeclaration constantDeclaration) {
		List<Identifier> names = constantDeclaration.names();
		List<Expression> initializers = constantDeclaration.initializers();
		
		for (int index = 0; index < names.size(); index++ ) {
			Attribute attribute = importer.ensureConstant(names.get(index), initializers.get(index), constantDeclaration.getModifier());
			importer.createSourceAnchor(attribute, names.get(index).getStart(), initializers.get(index).getEnd());
			//importer.ensureCommentFromBodyDeclaration(attribute, field);
			attribute.setIsStub(false);
		}
		
		return true;
	}
	
	// METHOD INVOCATION
	
	@Override
	public boolean visit(MethodInvocation methodInvocation) {
		IMethodBinding methodBinding = methodInvocation.resolveMethodBinding();
		if (methodBinding != null) {
			Expression dispatcherExpression = methodInvocation.getDispatcher();
			Invocation invocation = importer.createInvocationFromMethodBinding(methodBinding);
			if (dispatcherExpression != null) {
				invocation.setReceiver(importer.ensureStructuralEntityFromExpression(dispatcherExpression));
			}
			importer.createSourceAnchor(invocation, methodInvocation);
		}
		return true;
	}

	// FIELD ACCESSES
	
	@Override
	public boolean visit(FieldAccess fieldAccess) {
		Access accces = importer.createAccessFromFieldAccessNode(fieldAccess);
		if (fieldAccess.getParent().getType() == ASTNode.ASSIGNMENT) {
			accces.setIsWrite(true);
		}
		importer.createSourceAnchor(accces, fieldAccess);
		return true;
	}
	
	// CONSTANT ACCESSES
	
	@Override
	public boolean visit(StaticConstantAccess classConstantAccess) {
		Access accces = importer.createAccessFromConstantAccessNode(classConstantAccess);
		importer.createSourceAnchor(accces, classConstantAccess);
		return true;
	}
}
