package com.feenk.pdt2famix.test.oneSample;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.feenk.pdt2famix.test.support.OneSampleTestCase;

public class InterfaceInExplicitNamespaceTest extends OneSampleTestCase {
	private static final String NAMESPACE_NAME = "namespace_with_interface";
	private static final String INTERFACE_NAME = removeTestSuffix(InterfaceInExplicitNamespaceTest.class.getSimpleName());
	
	protected String sample() {
		return importer.makeTypeQualifiedNameFrom(NAMESPACE_NAME, INTERFACE_NAME);
	}
	
	@Test
	public void testModelSize() {
		assertEquals(1, importer.namespaces().size());
		assertEquals(1, importer.types().size());
	}
	
	@Test
	public void testNamespace() {	
		assertNamespacesPresent(NAMESPACE_NAME);
		assertNamespaceTypes(NAMESPACE_NAME, type);
	}
	
	@Test
	public void testInterfaceEntity() {	
		assertInterfaceType(type, INTERFACE_NAME);		
	}

}
