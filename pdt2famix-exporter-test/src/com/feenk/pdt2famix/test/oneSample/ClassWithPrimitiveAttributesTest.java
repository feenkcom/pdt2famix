package com.feenk.pdt2famix.test.oneSample;

import org.junit.Test;

import com.feenk.pdt2famix.Importer;
import com.feenk.pdt2famix.test.support.OneSampleTestCase;

public class ClassWithPrimitiveAttributesTest extends OneSampleTestCase {
	private static final String CLASS_QUALIFIED_NAME = "ClassWithPrimitiveAttributes";
	
	protected String sample() {
		return CLASS_QUALIFIED_NAME;
	}
	
	@Test
	public void testNamespace() {	
		assertNamespacesPresent(Importer.DEFAULT_NAMESPACE_NAME);
		assertNamespaceTypes(Importer.DEFAULT_NAMESPACE_NAME, type);
	}
	
	@Test
	public void testClassEntity() {	
		assertClassType(type, CLASS_QUALIFIED_NAME);		
	}
	
}
