package com.feenk.pdt2famix.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.feenk.pdt2famix.Importer;
import com.feenk.pdt2famix.model.famix.Type;
import com.feenk.pdt2famix.test.support.InPhpTestCase;

public class InterfacesInheritanceImplicitNamespaceTest extends InPhpTestCase {

	@Override
	protected String sampleDirectory() {
		return "interfaces_inheritance_implicit_namespace";
	}
	
	@Test
	public void testModelSize() {
		assertEquals(1, importer.namespaces().size());
		assertEquals(4, importer.types().size());
		assertNamespacesPresent(Importer.DEFAULT_NAMESPACE_NAME);
	}
	
	@Test
	public void testClasses() {	
		assertInterfacePresent(typeIdentifier("ARootSuperInterface"));
		assertInterfacePresent(typeIdentifier("AnotherSubInterface"));
		assertInterfacePresent(typeIdentifier("ASubInterface"));
		assertInterfacePresent(typeIdentifier("ASubSubInterface"));
	}
	
	@Test
	public void testInheritance() {	
		Type rootInterface = typeWithIndentifier(typeIdentifier("ARootSuperInterface"));
		Type anotherSubInterface = typeWithIndentifier(typeIdentifier("AnotherSubInterface"));
		Type subInterface = typeWithIndentifier(typeIdentifier("ASubInterface"));
		Type subSubInterface = typeWithIndentifier(typeIdentifier("ASubSubInterface"));
		
		assertEquals(0, rootInterface.getSuperInheritances().size());
		assertEquals(2, rootInterface.getSubInheritances().size());
		assertEquals(2, subInterface.getSuperInheritances().size());
		assertEquals(1, subInterface.getSubInheritances().size());
		
		assertEquals(1, anotherSubInterface.getSuperInheritances().size());
		assertEquals(1, anotherSubInterface.getSubInheritances().size());
		
		assertEquals(1, subSubInterface.getSuperInheritances().size());
		assertEquals(0, subSubInterface.getSubInheritances().size());
		
		assertInheritance(subSubInterface, subInterface);
		assertInheritance(subInterface, anotherSubInterface);
		assertInheritance(subInterface, rootInterface);
		assertInheritance(anotherSubInterface, rootInterface);
	}

}
