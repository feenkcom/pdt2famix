package com.feenk.pdt2famix.test.oneSample;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.feenk.pdt2famix.test.support.OneSampleTestCase;

public class InterfaceWithConstDefinitionsTest extends OneSampleTestCase {

	protected String sample() {
		return removeTestSuffix(InterfaceWithConstDefinitionsTest.class.getSimpleName());
	}

	@Test
	public void testModelSize() {
		assertEquals(2, importer.namespaces().size());
		assertEquals(2, importer.types().size());
		assertEquals(0, importer.methods().size());
		assertEquals(2, importer.attributes().size());
		assertEquals(0, importer.currentInvocations().size());
		assertEquals(0, importer.currentAccesses().size()); 
		assertEquals(0, importer.parameters().size());
	}
		
	@Test
	public void testClassConstants() {		
		assertAttribute("CONST1", type, numberType(), true, new String[] {"const", "public"});
		assertAttribute("CONST2", type, numberType(), true, new String[] {"const", "public"});
	}

}
