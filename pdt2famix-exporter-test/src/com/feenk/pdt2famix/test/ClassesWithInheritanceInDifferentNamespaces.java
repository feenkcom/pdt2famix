package com.feenk.pdt2famix.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.feenk.pdt2famix.model.famix.Type;
import com.feenk.pdt2famix.test.support.InPhpTestCase;

public class ClassesWithInheritanceInDifferentNamespaces extends InPhpTestCase {

	@Override
	protected String sampleDirectory() {
		return "classes_with_inheritance_in_different_namespaces";
	}
	
	@Test
	public void testModelSize() {
		assertEquals(3, importer.namespaces().size());
		assertEquals(3, importer.types().size());
	}
	
	@Test
	public void testNamespaces() {
		assertNamespacePresent("namespace_superclass");
		assertNamespacePresent("namespace_subclass");
		assertNamespacePresent("namespace_subsubclass");
	}
	
	@Test
	public void testClasses() {	
		assertClassPresent(typeIdentifier("namespace_superclass", "ARootSuperClassNamespace"));
		assertClassPresent(typeIdentifier("namespace_subclass", "ASubClassNamespace"));
		assertClassPresent(typeIdentifier("namespace_subsubclass", "ASubSubClassNamespace"));
	}
	
	@Test
	public void testInheritance() {	
		Type rootSuperclass = typeWithIndentifier(typeIdentifier("namespace_superclass", "ARootSuperClassNamespace"));
		Type subClass = typeWithIndentifier(typeIdentifier("namespace_subclass", "ASubClassNamespace"));
		Type subSubClass = typeWithIndentifier(typeIdentifier("namespace_subsubclass", "ASubSubClassNamespace"));
		
		assertEquals(0, rootSuperclass.getSuperInheritances().size());
		assertEquals(1, rootSuperclass.getSubInheritances().size());
		assertEquals(1, subClass.getSuperInheritances().size());
		assertEquals(1, subClass.getSubInheritances().size());
		assertEquals(1, subSubClass.getSuperInheritances().size());
		assertEquals(0, subSubClass.getSubInheritances().size());
		
		assertSingleInheritance(subClass, rootSuperclass);
		assertSingleInheritance(subSubClass, subClass);
	}

}
