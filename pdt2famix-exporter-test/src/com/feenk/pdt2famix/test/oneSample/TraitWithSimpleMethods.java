package com.feenk.pdt2famix.test.oneSample;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.feenk.pdt2famix.test.support.OneSampleTestCase;

public class TraitWithSimpleMethods extends OneSampleTestCase {
	
	protected String sample() {
		return removeTestSuffix(TraitWithSimpleMethods.class.getSimpleName());
	}
	
	@Test
	public void testModelSize() {
		assertEquals(1, importer.namespaces().size());
		assertEquals(1, importer.types().size());
		assertEquals(4, importer.methods().size());
	}
	
	@Test
	public void testTraitMethod() {	
		assertClassMethods(type, "method1", "method2", "method3", "method4");
		assertMethod(methodInType(type, "method1"), new String[] {"public"});
		assertMethod(methodInType(type, "method2"), new String[] {"protected"});
		assertMethod(methodInType(type, "method3"), new String[] {"private"});
		assertMethod(methodInType(type, "method4"), new String[] {"public"});
	}

}

