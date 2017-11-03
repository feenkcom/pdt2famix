package com.feenk.pdt2famix.test.oneSample;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.feenk.pdt2famix.test.support.OneSampleTestCase;

public class MethodsWithLocalInvocationsToSelfAttributesTest extends OneSampleTestCase {

	protected String sample() {
		return removeTestSuffix(MethodsWithLocalInvocationsToSelfAttributesTest.class.getSimpleName());
	}

	@Test
	public void testModelSize() {
		assertEquals(1, importer.namespaces().size());
		assertEquals(1, importer.types().size());
		assertEquals(8, importer.methods().size());
		assertEquals(7, importer.currentInvocations().size());
		assertEquals(0 /*7*/, importer.currentAccesses().size());
		assertEquals(6, importer.attributes().size());
	}
		
}
