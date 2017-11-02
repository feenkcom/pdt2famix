package com.feenk.pdt2famix.test.oneSample;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.feenk.pdt2famix.Importer;
import com.feenk.pdt2famix.test.support.OneSampleTestCase;

public class ClassWithPrimitiveAttributesTest extends OneSampleTestCase {
	
	protected String sample() {
		return removeTestSuffix(ClassWithPrimitiveAttributesTest.class.getSimpleName());
	}
	
	@Test
	public void testModelSize() {
		assertEquals(2, importer.namespaces().size());
		assertEquals(5, importer.types().size());
		assertEquals(7, importer.attributes().size());
	}
	
	@Test
	public void testNamespace() {	
		assertNamespacesPresent(Importer.DEFAULT_NAMESPACE_NAME, Importer.SYSTEM_NAMESPACE_NAME);
		assertNamespaceTypes(Importer.DEFAULT_NAMESPACE_NAME, type);
	}
	
	@Test
	public void testClassAttributes() {			
		assertAttribute("$varUndefined", null);
		assertAttribute("$varInt", numberType());
		assertAttribute("$varFloat", numberType());
		assertAttribute("$varString1", stringType());
		assertAttribute("$varString2", stringType());
		assertAttribute("$varBool", booleanType());
		assertAttribute("$varNull", nullType());
	}
	
}
