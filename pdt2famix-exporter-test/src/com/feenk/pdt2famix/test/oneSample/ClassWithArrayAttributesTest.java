package com.feenk.pdt2famix.test.oneSample;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.feenk.pdt2famix.test.support.OneSampleTestCase;

public class ClassWithArrayAttributesTest extends OneSampleTestCase {
	
	protected String sample() {
		return removeTestSuffix(ClassWithArrayAttributesTest.class.getSimpleName());
	}
	
	@Test
	public void testModelSize() {
		assertEquals(2, importer.namespaces().size());
		assertEquals(2, importer.types().size());
		assertEquals(7, importer.attributes().size());
	}
	
	@Test
	public void testClassAttributes() {			
		assertAttribute("$stringArray", arrayType());
		assertAttribute("$numberArray", arrayType());
		assertAttribute("$booleanArray", arrayType());
		assertAttribute("$nullArray", arrayType());
		assertAttribute("$mixedArray", arrayType());
		
		assertAttribute("$emptyArray", arrayType());
		assertAttribute("$numberSyntaxShortArray", arrayType());
	}
	
}
