package com.feenk.pdt2famix.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.feenk.pdt2famix.exporter.model.famix.Type;
import com.feenk.pdt2famix.test.support.InPhpTestCase;

public class InterfacesInheritanceDifferentNamespacesTest  extends InPhpTestCase {

	@Override
	protected String sampleDirectory() {
		return "interfaces_inheritance_different_namespaces";
	}
	
	@Test
	public void testModelSize() {
		assertEquals(3, importer.namespaces().size());
		assertEquals(3, importer.types().size());
	}
	
	@Test
	public void testNamespaces() {
		assertNamespacePresent("namespace_rootinterface1");
		assertNamespacePresent("namespace_rootinterface2");
		assertNamespacePresent("namespace_subinterface");
	}
	
	@Test
	public void testInterfaces() {	
		assertInterfacePresent(typeIdentifier("namespace_rootinterface1", "ARootInterfaceOneAB"));
		assertInterfacePresent(typeIdentifier("namespace_rootinterface2", "ARootInterfaceTwoAB"));
		assertInterfacePresent(typeIdentifier("namespace_subinterface", "ASubInterfaceAB"));
	}
	
	@Test
	public void testInheritance() {	
		Type rootInterface = typeWithIndentifier(typeIdentifier("namespace_rootinterface1", "ARootInterfaceOneAB"));
		Type anotherRootInterface = typeWithIndentifier(typeIdentifier("namespace_rootinterface2", "ARootInterfaceTwoAB"));
		Type subInterface = typeWithIndentifier(typeIdentifier("namespace_subinterface", "ASubInterfaceAB"));
		
		assertEquals(0, rootInterface.getSuperInheritances().size());
		assertEquals(1, rootInterface.getSubInheritances().size());
		assertEquals(0, anotherRootInterface.getSuperInheritances().size());
		assertEquals(1, anotherRootInterface.getSubInheritances().size());
		assertEquals(2, subInterface.getSuperInheritances().size());
		assertEquals(0, subInterface.getSubInheritances().size());
		
		assertInheritance(subInterface, anotherRootInterface);
		assertInheritance(subInterface, rootInterface);
	}

}
