package com.feenk.pdt2famix.test.oneSample;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.feenk.pdt2famix.test.support.OneSampleTestCase;

public class TraitWithPrimitiveAndTypeAttributesTest extends OneSampleTestCase {
	
	protected String sample() {
		return removeTestSuffix(TraitWithPrimitiveAndTypeAttributesTest.class.getSimpleName());
	}
	
	@Test
	public void testModelSize() {
		assertEquals(2, importer.namespaces().size());
		assertEquals(8, importer.types().size());
		assertEquals(8, importer.attributes().size());
	}
	
	
	@Test
	public void testTraitPrimitiveAttributes() {		
		assertAttribute("$varUndefinedT", null);
		assertAttribute("$varNullT", nullType());
		assertAttribute("$varIntT", numberType());
		assertAttribute("$varStringT", stringType());
		assertAttribute("$varBoolT", booleanType());
		assertAttribute("$varArrayIntT", arrayType());
	}
	
	@Test
	public void testTraitTypeAttributes() {			
		assertAttribute("$varTypeOne", typeNamed("ClassForTraitAttributeOne"));
		assertAttribute("$varTypeTwo", typeNamed("ClassForTraitAttributeTwo"));
	}
}
