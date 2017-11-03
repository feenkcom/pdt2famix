package com.feenk.pdt2famix.test.oneSample;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.feenk.pdt2famix.Importer;
import com.feenk.pdt2famix.model.famix.Method;
import com.feenk.pdt2famix.test.support.OneSampleTestCase;

public class ClassConstructorEmptyTest extends OneSampleTestCase {
	
	protected String sample() {
		return removeTestSuffix(ClassConstructorEmptyTest.class.getSimpleName());
	}
	
	@Test
	public void testModelSize() {
		assertEquals(1, importer.namespaces().size());
		assertEquals(1, importer.types().size());
		assertEquals(1, importer.methods().size());
	}
	
	@Test
	public void testClassConstructor() {	
		assertClassMethods(type, "__construct");
		
		Method constructor = methodInType(type, "__construct");
		assertMethod(constructor, new String[] {"public"}, Importer.CONSTRUCTOR_KIND);		
	}
	
}