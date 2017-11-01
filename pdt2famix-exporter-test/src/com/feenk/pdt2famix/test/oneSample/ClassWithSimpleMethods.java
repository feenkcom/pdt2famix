package com.feenk.pdt2famix.test.oneSample;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.feenk.pdt2famix.Importer;
import com.feenk.pdt2famix.test.support.OneSampleTestCase;

public class ClassWithSimpleMethods extends OneSampleTestCase {
	private static final String CLASS_QUALIFIED_NAME = "ClassWithSimpleMethods";

	@Override
	protected String sample() {
		return CLASS_QUALIFIED_NAME;
	}
	
	@Test
	public void testModelSize() {
		assertEquals(1, importer.namespaces().size());
		assertEquals(1, importer.types().size());
		assertEquals(4, importer.methods().size());
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
	
	@Test
	public void testClassMethod() {	
		assertClassMethods(type, "method1", "method2", "method3", "method4");
		assertMethod(methodInType(type, "method1"), new String[] {"public"});
		assertMethod(methodInType(type, "method2"), new String[] {"protected"});
		assertMethod(methodInType(type, "method3"), new String[] {"private"});
		assertMethod(methodInType(type, "method4"), new String[] {"public"});
	}

}
