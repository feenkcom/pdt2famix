package com.feenk.pdt2famix.test.oneSample;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.feenk.pdt2famix.model.famix.Method;
import com.feenk.pdt2famix.test.support.OneSampleTestCase;

public class MethodInvocationsWithArgumentsTest extends OneSampleTestCase {

	protected String sample() {
		return removeTestSuffix(MethodInvocationsWithArgumentsTest.class.getSimpleName());
	}

	@Test
	public void testModelSize() {
		assertEquals(2+1, importer.namespaces().size());
		assertEquals(3+1, importer.types().size());
		assertEquals(6+2, importer.methods().size());
		assertEquals(4+2, importer.currentInvocations().size());
		assertEquals(1, importer.currentAccesses().size());
		assertEquals(1, importer.attributes().size());
		assertEquals(6, importer.parameters().size());
	}
		
	@Test
	public void testMain1Invocations() {
		Method main1 = methodNamed("main1");
		Method helper1 = methodNamed("helper1");
		
		assertEquals(0, main1.getIncomingInvocations().size());
		assertEquals(1, main1.getOutgoingInvocations().size());
		assertEquals(1, helper1.getIncomingInvocations().size());
		assertEquals(0, helper1.getOutgoingInvocations().size());

		assertInvocationsBetweenMethods(main1, helper1, 1, null);
		assertInvocationProperties(main1.getOutgoingInvocations().stream().findFirst().get(), main1, helper1, null, "$this->helper1(1, 42+2)");
	}

}
