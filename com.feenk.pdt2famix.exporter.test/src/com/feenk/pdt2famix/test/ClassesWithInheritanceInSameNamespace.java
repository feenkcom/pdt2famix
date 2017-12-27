package com.feenk.pdt2famix.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.feenk.pdt2famix.exporter.inphp.Importer;
import com.feenk.pdt2famix.exporter.model.famix.Type;
import com.feenk.pdt2famix.test.support.InPhpTestCase;

public class ClassesWithInheritanceInSameNamespace extends InPhpTestCase {

	@Override
	protected String sampleDirectory() {
		return "classes_with_inheritance_in_same_namespace";
	}
	
	@Test
	public void testModelSize() {
		assertEquals(1, importer.namespaces().size());
		assertEquals(3, importer.types().size());
		assertNamespacesPresent(Importer.DEFAULT_NAMESPACE_NAME);
	}
	
	@Test
	public void testClasses() {	
		assertClassPresent(typeIdentifier("ARootSuperClass"));
		assertClassPresent(typeIdentifier("ASubClass"));
		assertClassPresent(typeIdentifier("ASubSubClass"));
	}
	
	@Test
	public void testInheritance() {	
		Type rootSuperclass = typeWithIndentifier(typeIdentifier("ARootSuperClass"));
		Type subClass = typeWithIndentifier(typeIdentifier("ASubClass"));
		Type subSubClass = typeWithIndentifier(typeIdentifier("ASubSubClass"));
		
		assertEquals(0, rootSuperclass.getSuperInheritances().size());
		assertEquals(1, rootSuperclass.getSubInheritances().size());
		assertEquals(1, subClass.getSuperInheritances().size());
		assertEquals(1, subClass.getSubInheritances().size());
		assertEquals(1, subSubClass.getSuperInheritances().size());
		assertEquals(0, subSubClass.getSubInheritances().size());
		
		assertInheritance(subClass, rootSuperclass);
		assertInheritance(subSubClass, subClass);
	}
	
}
