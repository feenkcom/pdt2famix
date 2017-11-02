package com.feenk.pdt2famix.test.oneSample;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.feenk.pdt2famix.test.support.OneSampleTestCase;

public class ClassWithTypeAttributesTest extends OneSampleTestCase {
	
	protected String sample() {
		return removeTestSuffix(ClassWithTypeAttributesTest.class.getSimpleName());
	}
	
	@Test
	public void testModelSize() {
		assertEquals(1, importer.namespaces().size());
		assertEquals(3, importer.types().size());
		assertEquals(2, importer.attributes().size());
	}
	
	@Test
	public void testClassAttributes() {			
		assertAttribute("$first", typeNamed("TypeForOneAttribute"));
		assertAttribute("$second", typeNamed("TypeForOnetherAttribute"));
	}
	
}
