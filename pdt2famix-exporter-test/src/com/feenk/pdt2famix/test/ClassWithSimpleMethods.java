package com.feenk.pdt2famix.test;

import org.junit.Test;

import com.feenk.pdt2famix.Importer;
import com.feenk.pdt2famix.test.support.InPhpTestCase;

public class ClassWithSimpleMethods extends InPhpTestCase {
	private static final String CLASS_QUALIFIED_NAME = "ClassWithSimpleMethods";

	protected String sampleDirectory() {
		return "class_with_simple_methods";
	}
	
	@Test
	public void testClassStructure() {	
		assertNamespacesPresent(new String[] {Importer.DEFAULT_NAMESPACE_NAME});
		assertClassPresent("ClassWithSimpleMethods");
		
		assertMethod(makeQualifiedNameFrom(CLASS_QUALIFIED_NAME, "method1"), new String[] {"public"});
		assertMethod(makeQualifiedNameFrom(CLASS_QUALIFIED_NAME, "method2"), new String[] {"protected"});
		assertMethod(makeQualifiedNameFrom(CLASS_QUALIFIED_NAME, "method3"), new String[] {"private"});
		assertMethod(makeQualifiedNameFrom(CLASS_QUALIFIED_NAME, "method4"), new String[] {"public"});
		assertClassMethods(CLASS_QUALIFIED_NAME, "method1", "method2", "method3", "method4");
	}

}
