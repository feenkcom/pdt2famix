package com.feenk.pdt2famix.test.oneSample;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.feenk.pdt2famix.test.support.OneSampleTestCase;

public class ClassWithinExplicitNamespace extends OneSampleTestCase {
	private static final String NAMESPACE_NAME = "a\\b\\c\\d\\e";
	private static final String CLASS_NAME = removeTestSuffix(ClassWithinExplicitNamespace.class.getSimpleName());
	
	protected String sample() {
		return importer.makeTypeQualifiedNameFrom(NAMESPACE_NAME, CLASS_NAME);
	}
	
	@Test
	public void testModelSize() {
		assertEquals(5, importer.namespaces().size());
		assertEquals(1, importer.types().size());
	}
	
	@Test
	public void testNamespace() {	
		assertNamespacePresent(NAMESPACE_NAME);
		assertNamespaceTypes(NAMESPACE_NAME, type);
	}
	
	@Test
	public void testClassEntity() {	
		assertClassType(type, CLASS_NAME);		
	}
}
