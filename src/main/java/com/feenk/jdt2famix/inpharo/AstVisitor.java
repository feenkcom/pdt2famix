package com.feenk.jdt2famix.inpharo;

//import jniport.SmalltalkRequest;

import org.eclipse.jdt.core.dom.ASTVisitor;
//import org.eclipse.jdt.core.dom.AnnotationTypeDeclaration;
//import org.eclipse.jdt.core.dom.AnnotationTypeMemberDeclaration;
//import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
//import org.eclipse.jdt.core.dom.Assignment;
//import org.eclipse.jdt.core.dom.ClassInstanceCreation;
//import org.eclipse.jdt.core.dom.CompilationUnit;
//import org.eclipse.jdt.core.dom.ConstructorInvocation;
//import org.eclipse.jdt.core.dom.EnumDeclaration;
//import org.eclipse.jdt.core.dom.FieldAccess;
//import org.eclipse.jdt.core.dom.FieldDeclaration;
//import org.eclipse.jdt.core.dom.MethodDeclaration;
//import org.eclipse.jdt.core.dom.MethodInvocation;
//import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
//import org.eclipse.jdt.core.dom.SuperMethodInvocation;
//import org.eclipse.jdt.core.dom.TypeDeclaration;

public class AstVisitor extends ASTVisitor {

//	public AstVisitor() {
//	}
//	
//	////////PACKAGES
//	
//	public static String visitCompilationUnitCallback = AstVisitor.class.getName() + "visit(CompilationUnit)";
//	@Override
//	public boolean visit(CompilationUnit node) {
//		try {
//			new SmalltalkRequest(visitCompilationUnitCallback, this, node).value();
//		} catch (Throwable e) {
//			e.printStackTrace();
//		}
//		return true;
//	}
//	
//	/**
//	 * Needed for keeping track of the current container
//	 */
//	public static String endVisitCompilationUnitCallback = AstVisitor.class.getName() + "endVisit(CompilationUnit)";
//	@Override
//	public void endVisit(CompilationUnit node) {
//		try {
//			new SmalltalkRequest(endVisitCompilationUnitCallback, this, node).value();
//		} catch (Throwable e) {
//			e.printStackTrace();
//		}
//	}
//	
//	
//	////////TYPES
//	
//	public static String visitTypeDeclarationCallback = AstVisitor.class.getName() + "visit(TypeDeclaration)";
//	@Override
//	public boolean visit(TypeDeclaration node) {
//		try {
//			new SmalltalkRequest(visitTypeDeclarationCallback, this, node).value();
//		} catch (Throwable e) {
//			e.printStackTrace();
//		}
//		return true;
//	}
//	
//	/**
//	 * Needed for keeping track of the current container
//	 */
//	public static String endVisitTypeDeclarationCallback = AstVisitor.class.getName() + "endVisit(TypeDeclaration)";
//	@Override
//	public void endVisit(TypeDeclaration node) {
//		try {
//			new SmalltalkRequest(endVisitTypeDeclarationCallback, this, node).value();
//		} catch (Throwable e) {
//			e.printStackTrace();
//		}
//	}
//
//	public static String visitAnonymousClassDeclarationCallback = AstVisitor.class.getName() + "visit(AnonymousClassDeclaration)";
//	@Override
//	public boolean visit(AnonymousClassDeclaration node) {
//		try {
//			new SmalltalkRequest(visitAnonymousClassDeclarationCallback, this, node).value();
//		} catch (Throwable e) {
//			e.printStackTrace();
//		}
//		return true;
//	}
//	
//	public static String endVisitAnonymousClassDeclarationCallback = AstVisitor.class.getName() + "endVisit(AnonymousClassDeclaration)";
//	@Override
//	public void endVisit(AnonymousClassDeclaration node) {
//		try {
//			new SmalltalkRequest(endVisitAnonymousClassDeclarationCallback, this, node).value();
//		} catch (Throwable e) {
//			e.printStackTrace();
//		}
//	}
//	
//	public static String visitEnumDeclarationCallback = AstVisitor.class.getName() + "visit(EnumDeclaration)";
//	@Override
//	public boolean visit(EnumDeclaration node) {
//		try {
//			new SmalltalkRequest(visitEnumDeclarationCallback, this, node).value();
//		} catch (Throwable e) {
//			e.printStackTrace();
//		}
//		return true;
//	}
//	
//	public static String endVisitEnumDeclarationCallback = AstVisitor.class.getName() + "endVisit(EnumDeclaration)";
//	@Override
//	public void endVisit(EnumDeclaration node) {
//		try {
//			new SmalltalkRequest(endVisitEnumDeclarationCallback, this, node).value();
//		} catch (Throwable e) {
//			e.printStackTrace();
//		}
//	}
//	
//	
//	////////ANNOTATIONS
//	
//	@Override
//	public boolean visit(AnnotationTypeDeclaration node) {
//		return super.visit(node);
//	}
//	
//	@Override
//	public void endVisit(AnnotationTypeDeclaration node) {
//		super.endVisit(node);
//	}
//	
//	@Override
//	public boolean visit(AnnotationTypeMemberDeclaration node) {
//		return super.visit(node);
//	}
//	
//	@Override
//	public void endVisit(AnnotationTypeMemberDeclaration node) {
//		super.endVisit(node);
//	}
//	
//	
//	////////METHODS
//	
//	public static String visitMethodDeclarationCallback = AstVisitor.class.getName() + "visit(MethodDeclaration)";
//	@Override
//	public boolean visit(MethodDeclaration node) {
//		try {
//			new SmalltalkRequest(visitMethodDeclarationCallback, this, node).value();
//		} catch (Throwable e) {
//			e.printStackTrace();
//		}
//		return true;
//	}
//
//	/**
//	 * Needed for keeping track of the current container
//	 */
//	public static String endVisitMethodDeclarationCallback = AstVisitor.class.getName() + "endVisit(MethodDeclaration)";
//	@Override
//	public void endVisit(MethodDeclaration node) {
//		try {
//			new SmalltalkRequest(endVisitMethodDeclarationCallback, this, node).value();
//		} catch (Throwable e) {
//			e.printStackTrace();
//		}
//	}
//
//	
//	////////ATTRIBUTES
//	
//	public static String visitFieldDeclarationCallback = AstVisitor.class.getName() + "visit(FieldDeclaration)";
//	@Override
//	public boolean visit(FieldDeclaration node) {
//		try {
//			new SmalltalkRequest(visitFieldDeclarationCallback, this, node).value();
//		} catch (Throwable e) {
//			e.printStackTrace();
//		}
//		return true;
//	}
//	
//	
//	////////INVOCATIONS
//	
//	public static String visitMethodInvocationCallback = AstVisitor.class.getName() + "visit(MethodInvocation)";
//	@Override
//	public boolean visit(MethodInvocation node) {
//		try {
//			new SmalltalkRequest(visitMethodInvocationCallback, this, node).value();
//		} catch (Throwable e) {
//			e.printStackTrace();
//		}
//		return super.visit(node);
//	}
//
//	@Override
//	public void endVisit(MethodInvocation node) {
//		//Not needed
//	}
//	
//	public static String visitSuperMethodInvocationCallback = AstVisitor.class.getName() + "visit(SuperMethodInvocation)";
//	@Override
//	public boolean visit(SuperMethodInvocation node) {
//		try {
//			new SmalltalkRequest(visitSuperMethodInvocationCallback, this, node).value();
//		} catch (Throwable e) {
//			e.printStackTrace();
//		}
//		return true;
//	}
//	
//	@Override
//	public void endVisit(SuperMethodInvocation node) {
//		//Not needed
//	}
//	
//	public static String visitConstructorInvocationCallback = AstVisitor.class.getName() + "visit(ConstructorInvocation)";
//	@Override
//	public boolean visit(ConstructorInvocation node) {
//		try {
//			new SmalltalkRequest(visitConstructorInvocationCallback, this, node).value();
//		} catch (Throwable e) {
//			e.printStackTrace();
//		}
//		return true;
//	}
//	
//	@Override
//	public void endVisit(ConstructorInvocation node) {
//		//Not needed
//	}
//
//	public static String visitSuperConstructorInvocationCallback = AstVisitor.class.getName() + "visit(SuperConstructorInvocation)";
//	@Override
//	public boolean visit(SuperConstructorInvocation node) {
//		try {
//			new SmalltalkRequest(visitSuperConstructorInvocationCallback, this, node).value();
//		} catch (Throwable e) {
//			e.printStackTrace();
//		}
//		return true;
//	}
//	
//	public static String visitClassInstanceCreationCallback = AstVisitor.class.getName() + "visit(ClassInstanceCreation)";
//	@Override
//	public boolean visit(ClassInstanceCreation node) {
//		try {
//			new SmalltalkRequest(visitClassInstanceCreationCallback, this, node).value();
//		} catch (Throwable e) {
//			e.printStackTrace();
//		}
//		return true;
//	}
//	
//	////////ACCESSES
//	public static String visitFieldAccessCallback = AstVisitor.class.getName() + "visit(FieldAccess)";
//	@Override
//	public boolean visit(FieldAccess node) {
//		try {
//			new SmalltalkRequest(visitFieldAccessCallback, this, node).value();
//		} catch (Throwable e) {
//			e.printStackTrace();
//		}
//		return true;
//	}
//
//	@Override
//	public void endVisit(FieldAccess node) {
//		//Not needed
//	}
//
//	public static String visitAssignmentCallback = AstVisitor.class.getName() + "visit(Assignment)";
//	@Override
//	public boolean visit(Assignment node) {
//		try {
//			new SmalltalkRequest(visitAssignmentCallback, this, node).value();
//		} catch (Throwable e) {
//			e.printStackTrace();
//		}
//		return true;
//	}
//	
//	@Override
//	public void endVisit(Assignment node) {
//		//Not needed
//	}

}
