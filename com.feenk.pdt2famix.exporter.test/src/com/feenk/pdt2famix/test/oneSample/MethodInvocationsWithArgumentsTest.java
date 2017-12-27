package com.feenk.pdt2famix.test.oneSample;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.feenk.pdt2famix.exporter.model.famix.Method;
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
	
	@Test
	public void testMain2Invocations() {
		Method main2 = methodNamed("main2");
		Method helper2 = methodNamed("helper2");
		
		assertEquals(0, main2.getIncomingInvocations().size());
		assertEquals(2, main2.getOutgoingInvocations().size());
		assertEquals(1, helper2.getIncomingInvocations().size());
		assertEquals(0, helper2.getOutgoingInvocations().size());

		assertInvocationsBetweenMethods(main2, helper2, 1, attributeNamed("$field"));
		//assertInvocationProperties(main2.getOutgoingInvocations().stream().findFirst().get(), main2, helper2, attributeNamed("$field"), "$this->field->helper2(10, 12==12, new MethodInvocationsWithArguments())");
	}

	@Test
	public void testMain3Invocations() {
		Method main3 = methodNamed("main3");
		Method helper3 = methodNamed("helper3");
		
		assertEquals(0, main3.getIncomingInvocations().size());
		assertEquals(2, main3.getOutgoingInvocations().size());
		assertEquals(2, helper3.getIncomingInvocations().size());
		assertEquals(0, helper3.getOutgoingInvocations().size());

		assertInvocationsBetweenMethods(main3, helper3, 2, null);
	}
}
