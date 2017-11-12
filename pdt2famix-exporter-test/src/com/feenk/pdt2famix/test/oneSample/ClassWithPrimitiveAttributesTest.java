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
		assertAttribute("$varUndefined", type, null, false, new String[] {"public"});
		assertAttribute("$varInt", type, numberType(), false, new String[] {"protected"});
		assertAttribute("$varFloat", type, numberType(), false, new String[] {"private"});
		assertAttribute("$varString1", type, stringType(), false, new String[] {"public"});
		assertAttribute("$varString2", type, stringType(), true, new String[] {"public"});
		assertAttribute("$varBool", type, booleanType(), true, new String[] {"protected"});
		assertAttribute("$varNull", type, nullType(), true, new String[] {"private"});
	}
	
}
