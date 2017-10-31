package com.feenk.pdt2famix.test;

import org.junit.Test;

import com.feenk.pdt2famix.Importer;
import com.feenk.pdt2famix.test.support.InPhpTestCase;

public class ClassWithPrimitiveAttributesTest extends InPhpTestCase {
	private static final String CLASS_QUALIFIED_NAME = "ClassWithPrimitiveAttributes";
	
	protected String sampleDirectory() {
		return "class_with_primitive_attributes";
	}
	
	@Test
	public void testClassStructure() {	
		assertNamespacesPresent(Importer.DEFAULT_NAMESPACE_NAME);
		assertClassPresent(CLASS_QUALIFIED_NAME);
		
		assertNamespaceTypes(Importer.DEFAULT_NAMESPACE_NAME, new String[] {CLASS_QUALIFIED_NAME});
	}
	
}
