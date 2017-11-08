package com.feenk.pdt2famix.test.oneSample;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.Test;

import com.feenk.pdt2famix.model.famix.Attribute;
import com.feenk.pdt2famix.model.famix.Method;
import com.feenk.pdt2famix.test.support.OneSampleTestCase;

public class MethodsWithChainedAttributesAndMessagesTest extends OneSampleTestCase {

	protected String sample() {
		return removeTestSuffix(MethodsWithChainedAttributesAndMessagesTest.class.getSimpleName());
	}

	@Test
	public void testModelSize() {
		assertEquals(1, importer.namespaces().size());
		assertEquals(2, importer.types().size());
		assertEquals(8, importer.methods().size());
		assertEquals(6, importer.currentInvocations().size());
		assertEquals(6, importer.currentAccesses().size());
		assertEquals(4, importer.attributes().size());
		assertEquals(0, importer.parameters().size());
	}
	
	@Test
	public void testMain1InvocationsAndAccesses() {
		Method main1 = methodNamed("main1");
		Method helper1 = methodNamed("helper1");
		Attribute field1 = attributeNamed("$field1");
		
		assertEquals(0, main1.getIncomingInvocations().size());
		assertEquals(2, main1.getOutgoingInvocations().size());
		assertEquals(1, main1.getAccesses().size());
		assertEquals(1, field1.getIncomingAccesses().size());
		assertEquals(2, helper1.getIncomingInvocations().size());
		assertEquals(0, helper1.getOutgoingInvocations().size());
		
		assertInvocationsBetweenMethodsMatching(main1, helper1, 1, invocation -> invocation.getReceiver() == null);
		assertInvocationsBetweenMethodsMatching(main1, helper1, 1, invocation -> field1.equals(invocation.getReceiver()));
		assertAccess(main1, field1, 1, Optional.of(false));
	}

	@Test
	public void testMain2InvocationsAndAccesses() {
		Method main2 = methodNamed("main2");
		Method helper2 = methodNamed("helper2");
		Attribute field2 = attributeNamed("$field2");
		
		assertEquals(0, main2.getIncomingInvocations().size());
		assertEquals(1, main2.getOutgoingInvocations().size());
		assertEquals(2, main2.getAccesses().size());
		assertEquals(2, field2.getIncomingAccesses().size());
		assertEquals(1, helper2.getIncomingInvocations().size());
		assertEquals(0, helper2.getOutgoingInvocations().size());
		
		assertInvocationsBetweenMethods(main2, helper2, 1, field2);
		assertAccess(main2, field2, 2, Optional.of(false));
	}

	@Test
	public void testMain3InvocationsAndAccesses() {
		Method main3 = methodNamed("main3");
		Method helper3 = methodNamed("helper3");
		
		assertEquals(0, main3.getIncomingInvocations().size());
		assertEquals(3, main3.getOutgoingInvocations().size());
		assertEquals(0, main3.getAccesses().size());
		assertEquals(3, helper3.getIncomingInvocations().size());
		assertEquals(0, helper3.getOutgoingInvocations().size());
		
		assertInvocationsBetweenMethods(main3, helper3, 3, null);
	}
	
	@Test
	public void testMain4InvocationsAndAccesses() {
		Method main4 = methodNamed("main4");
		Attribute field4 = attributeNamed("$field4");
		
		assertEquals(0, main4.getIncomingInvocations().size());
		assertEquals(0, main4.getOutgoingInvocations().size());
		assertEquals(3, main4.getAccesses().size());
		assertEquals(3, field4.getIncomingAccesses().size());
		
		assertAccess(main4, field4, 3, Optional.of(false));
	}
}
