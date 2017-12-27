package com.feenk.pdt2famix.test.strange;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.feenk.pdt2famix.test.support.OneSampleTestCase;

public class ClassWithMultipleConstsDefinitionTest extends OneSampleTestCase {

	protected String sample() {
		return removeTestSuffix(ClassWithMultipleConstsDefinitionTest.class.getSimpleName());
	}

	@Test
	public void testModelSize() {
		assertEquals(3, importer.namespaces().size());
		assertEquals(3, importer.types().size());
		assertEquals(0, importer.methods().size());
		assertEquals(3, importer.attributes().size());
		assertEquals(0, importer.currentInvocations().size());
		assertEquals(0, importer.currentAccesses().size()); 
		assertEquals(0, importer.parameters().size());
	}
		
	@Test
	public void testClassConstants() {		
		assertAttribute("CONST_ONE", type, importer.unknownType(), true, new String[] {"const", "public"}); // Resolved incorrectly
		assertAttribute("CONST_TWO", type, importer.unknownType(), true, new String[] {"const", "public"}); // Resolved incorrectly
		assertAttribute("CONST_THREE", type, numberType(), true, new String[] {"const", "public"});
	}

}
