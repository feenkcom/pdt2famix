package com.feenk.pdt2famix.test.oneSample;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.feenk.pdt2famix.model.famix.Attribute;
import com.feenk.pdt2famix.model.famix.Method;
import com.feenk.pdt2famix.test.support.OneSampleTestCase;

public class MethodsWithLocalInvocationsToSelfParametersTest extends OneSampleTestCase {

	protected String sample() {
		return removeTestSuffix(MethodsWithLocalInvocationsToSelfParametersTest.class.getSimpleName());
	}

	@Test
	public void testModelSize() {
		assertEquals(1, importer.namespaces().size());
		assertEquals(1, importer.types().size());
		assertEquals(5, importer.methods().size());
		assertEquals(5, importer.currentInvocations().size());
		assertEquals(0, importer.currentAccesses().size());
		assertEquals(0, importer.attributes().size());
	}
	
	@Test
	public void testMain1Invocations() {
		Method main1 = methodNamed("main1");
		Method helper1 = methodNamed("helper1");
		Method helper2 = methodNamed("helper2");
		
		assertEquals(0, main1.getIncomingInvocations().size());
		assertEquals(2, main1.getOutgoingInvocations().size());
		assertEquals(1, helper1.getIncomingInvocations().size());
		assertEquals(0, helper1.getOutgoingInvocations().size());
		assertEquals(1, helper2.getIncomingInvocations().size());
		assertEquals(0, helper2.getOutgoingInvocations().size());
		
		assertInvocationsBetweenMethods(main1, helper1, 2, null);
		assertInvocationsBetweenMethods(main1, helper2, 2, null);
	}
	
	@Test
	public void testMainRecursionInvocations() {
		Method mainRecursion = methodNamed("mainRecursion");
//		Attribute fieldRecursion = attributeNamed("$fieldRecursion");
		
		assertEquals(1, mainRecursion.getIncomingInvocations().size());
		assertEquals(1, mainRecursion.getOutgoingInvocations().size());
		
		assertInvocationsBetweenMethods(mainRecursion, mainRecursion, 1, null);
	}

}
