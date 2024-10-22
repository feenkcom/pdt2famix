package com.feenk.pdt2famix.exporter.inphp;

import java.util.List;
import java.util.StringJoiner;

import org.eclipse.php.core.ast.nodes.ASTNode;
import org.eclipse.php.core.ast.nodes.AnonymousClassDeclaration;
import org.eclipse.php.core.ast.nodes.ClassDeclaration;
import org.eclipse.php.core.ast.nodes.ClassInstanceCreation;
import org.eclipse.php.core.ast.nodes.ConstantDeclaration;
import org.eclipse.php.core.ast.nodes.Expression;
import org.eclipse.php.core.ast.nodes.FieldAccess;
import org.eclipse.php.core.ast.nodes.FormalParameter;
import org.eclipse.php.core.ast.nodes.IMethodBinding;
import org.eclipse.php.core.ast.nodes.ITypeBinding;
import org.eclipse.php.core.ast.nodes.Identifier;
import org.eclipse.php.core.ast.nodes.InterfaceDeclaration;
import org.eclipse.php.core.ast.nodes.LambdaFunctionDeclaration;
import org.eclipse.php.core.ast.nodes.MethodDeclaration;
import org.eclipse.php.core.ast.nodes.MethodInvocation;
import org.eclipse.php.core.ast.nodes.NamespaceDeclaration;
import org.eclipse.php.core.ast.nodes.NamespaceName;
import org.eclipse.php.core.ast.nodes.SingleFieldDeclaration;
import org.eclipse.php.core.ast.nodes.StaticConstantAccess;
import org.eclipse.php.core.ast.nodes.StaticFieldAccess;
import org.eclipse.php.core.ast.nodes.StaticMethodInvocation;
import org.eclipse.php.core.ast.nodes.TraitDeclaration;
import org.eclipse.php.core.ast.nodes.TraitUseStatement;
import org.eclipse.php.core.ast.nodes.TypeDeclaration;
import org.eclipse.php.core.ast.visitor.AbstractVisitor;

import com.feenk.pdt2famix.exporter.Famix;
import com.feenk.pdt2famix.exporter.model.famix.Access;
import com.feenk.pdt2famix.exporter.model.famix.Attribute;
import com.feenk.pdt2famix.exporter.model.famix.Invocation;
import com.feenk.pdt2famix.exporter.model.famix.Method;
import com.feenk.pdt2famix.exporter.model.famix.Namespace;
import com.feenk.pdt2famix.exporter.model.famix.Parameter;
import com.feenk.pdt2famix.exporter.model.famix.Type;

public class AstVisitor extends AbstractVisitor {	
	private Importer importer;
	
	public AstVisitor(Importer importer) {
		this.importer = importer;
	}
		
	public void logNullBinding(String string, String extraData) {
		importer.logNullBinding(string, extraData);
	}
	
	public void logInvalidBinding(String string, String extraData) {
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
			logNullBinding("type declaration", typeDeclaration.getName().getName());
			return ;
		}
		
		Type famixType = importer.ensureTypeFromTypeBinding(binding, typeDeclaration);
		famixType.setIsStub(false);
		importer.createSourceAnchor(famixType, typeDeclaration);
		//importer.extractCommentAndAnnotationsFromASTNode(famixType, typeDeclaration);
		importer.pushOnContainerStack(famixType);
	}
	
	//  METHODS
	
	@Override
	public boolean visit(MethodDeclaration methodDeclarationNode) {
 		IMethodBinding methodBinding = methodDeclarationNode.resolveMethodBinding();
		Method famixMethod;
		if (methodBinding != null) {
			famixMethod = importer.ensureMethodFromMethodBindingToCurrentContainer(methodBinding);
		}
		else {
//			logNullBinding("method declaration", methodDeclarationNode.getFunction().getFunctionName(), methodDeclarationNode.getStart());
//			famixMethod = importer.ensureMethodFromMethodDeclaration(methodDeclarationNode);
			return true;
		}
		famixMethod.setIsStub(false);
		importer.pushOnContainerStack(famixMethod);
		
		StringJoiner signatureJoiner = new StringJoiner(", ", "(", ")");
		for (FormalParameter parameter: methodDeclarationNode.getFunction().formalParameters()) {
			Parameter famixParameter = importer.parameterFromFormalParameterDeclaration(parameter, famixMethod, methodBinding.getKey());
			
			// Create the signature after extracting the parameter. We need to do it here as from a IMethodBinding we do not get access
			// to the actual parameter types. 
			if (famixParameter.getDeclaredType() != null) {
				if (famixParameter.getDeclaredType().getContainer().getName().equals(Importer.DEFAULT_NAMESPACE_NAME)) {
					signatureJoiner.add(famixParameter.getDeclaredType().getName());
				} else {
					signatureJoiner.add(Famix.qualifiedNameOf(famixParameter.getDeclaredType()));
				}
			} else {
				signatureJoiner.add(Importer.UNKNOWN_NAME);
			}
		}
		famixMethod.setSignature(methodDeclarationNode.getFunction().getFunctionName().getName()+signatureJoiner.toString());
		
		importer.createSourceAnchor(famixMethod, methodDeclarationNode);
		importer.extractCommentAndAnnotationsFromASTNode(famixMethod, methodDeclarationNode);
		return true;
	}
	
	@Override
	public void endVisit(MethodDeclaration methodDeclaration) {
		importer.popFromContainerStack();
	}
	
	
	// ATTRIBUTES
	
	@Override 
	public boolean visit(SingleFieldDeclaration fieldDeclaration) {
		if (shouldRecordExpression(fieldDeclaration.getValue())) {
			importer.pushOnContainerStack(importer.ensureInitializerMethod());
		};
		Attribute attribute = importer.ensureAttributeForFieldDeclaration(fieldDeclaration);
		importer.createSourceAnchor(attribute, fieldDeclaration);
		importer.extractCommentAndAnnotationsFromASTNode(attribute, fieldDeclaration);
		attribute.setIsStub(false);
		return true;
	}
	
	@Override
	public void endVisit(SingleFieldDeclaration fieldDeclaration) {
		if (importer.topOfContainerStack().getName().equals(Importer.INITIALIZER_NAME))
			importer.popFromContainerStack();
	}
	
	// CONSTANTS

	@Override
	public boolean visit(ConstantDeclaration constantDeclaration) {
		List<Identifier> names = constantDeclaration.names();
		List<Expression> initializers = constantDeclaration.initializers();
		
		if (initializers.stream().anyMatch(this::shouldRecordExpression)) {
			importer.pushOnContainerStack(importer.ensureInitializerMethod());
		};
		
		for (int index = 0; index < names.size(); index++ ) {
			Attribute attribute = importer.ensureConstant(names.get(index), initializers.get(index), constantDeclaration.getModifier());
			importer.createSourceAnchor(attribute, names.get(index).getStart(), initializers.get(index).getEnd());
			importer.extractCommentAndAnnotationsFromASTNode(attribute, constantDeclaration);
			attribute.setIsStub(false);
		}
		
		return true;
	}
	
	@Override
	public void endVisit(ConstantDeclaration constantDeclaration) {
		if (importer.topOfContainerStack().getName().equals(Importer.INITIALIZER_NAME))
			importer.popFromContainerStack();
	}
	
	/**
	 * Return true if the given expression should be recorder.
	 */
	private boolean shouldRecordExpression(Expression expression) {
		return expression != null && expression.getType() != ASTNode.SCALAR;
	}
	
	// METHOD INVOCATION
	
	
	@Override
	public boolean visit(MethodInvocation methodInvocation) {
		//logger.trace("MethodInvocation: "+ methodInvocation.getMethod().getFunctionName().getName());
		//TODO: This only hanles invocations from within methods. Extend to take namespaes into account.
		if (importer.topOfContainerStack() instanceof Method) {
			Invocation invocation = importer.createInvocationFromMethodInvocation(methodInvocation);
			importer.createSourceAnchor(invocation, methodInvocation);
		}
		return true;
	}
	
	@Override
	public boolean visit(StaticMethodInvocation methodInvocation) {		
		//logger.trace("StaticMethodInvocation: "+ methodInvocation.getMethod().getFunctionName().getName());
		//TODO: This only hanles invocations from within methods. Extend to take namespaes into account.
		if (importer.topOfContainerStack() instanceof Method) {
			Invocation invocation = importer.createInvocationFromMethodInvocation(methodInvocation);
			importer.createSourceAnchor(invocation, methodInvocation);
		}
		return true;
	}
	
	@Override
	public boolean visit(ClassInstanceCreation classInstanceCreation) {
		IMethodBinding constructorBinding = classInstanceCreation.resolveConstructorBinding();
		ITypeBinding classTypeBinding = classInstanceCreation.resolveTypeBinding();
		Invocation invocation = null;
		
		if (constructorBinding != null) {
			invocation = importer.createInvocationFromMethodBinding(constructorBinding, classInstanceCreation);
		} else if (classTypeBinding != null) {
			Method famixMethod = importer.ensureConstructorForTypeBinding(classTypeBinding, classInstanceCreation.getAST().getBindingResolver());
			invocation = importer.createInvocationToFamixMethod(famixMethod, classInstanceCreation);
		}
		if (invocation != null) {
			importer.createSourceAnchor(invocation, classInstanceCreation);
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
	
	@Override
	public boolean visit(StaticFieldAccess staticFieldAccess) {
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
