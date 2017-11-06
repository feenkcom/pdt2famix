package com.feenk.pdt2famix.test.oneSample;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.junit.Test;

import com.feenk.pdt2famix.model.famix.Attribute;
import com.feenk.pdt2famix.model.famix.Invocation;
import com.feenk.pdt2famix.model.famix.Method;
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
		assertEquals(7, importer.currentAccesses().size());
		assertEquals(6, importer.attributes().size());
	}
		
	@Test
	public void testMain1Recursion() {
		Method main1 = methodNamed("main1");
		Method helper1 = methodNamed("helper1");
		Attribute field11 = attributeNamed("$field11");
		Attribute field12 = attributeNamed("$field12");

		assertEquals(0, main1.getIncomingInvocations().size());
		assertEquals(2, main1.getOutgoingInvocations().size());
		assertEquals(2, helper1.getIncomingInvocations().size());
		assertEquals(0, helper1.getOutgoingInvocations().size());
		
		Collection<Invocation> outgoingInvocation = main1.getOutgoingInvocations();
		Invocation firstInvocation = outgoingInvocation.stream().sequential().findFirst().get();
		Invocation secondInvocation = outgoingInvocation.stream().sequential().skip(1).findFirst().get();
		
		if (firstInvocation.getReceiver().equals(field11) ) {
			assertInvocationProperties(firstInvocation, main1, helper1, field11);
			assertInvocationProperties(secondInvocation, main1, helper1, field12);
		} else {
			assertInvocationProperties(firstInvocation, main1, helper1, field12);
			assertInvocationProperties(secondInvocation, main1, helper1, field11);
		}
	}
	
	@Test
	public void testMain2Invocations() {
		Method main2 = methodNamed("main2");
		Method helper2 = methodNamed("helper2");
		Attribute field2 = attributeNamed("$field2");
		
		assertEquals(0, main2.getIncomingInvocations().size());
		assertEquals(2, main2.getOutgoingInvocations().size());
		assertEquals(2, helper2.getIncomingInvocations().size());
		assertEquals(0, helper2.getOutgoingInvocations().size());
		
		assertInvocationsBetweenMethods(main2, helper2, 2, field2);
	}
	
	@Test
	public void testMain3Invocations() {
		Method main3 = methodNamed("main3");
		Method helper31 = methodNamed("helper31");
		Method helper32 = methodNamed("helper32");
		Attribute field31 = attributeNamed("$field31");
		Attribute field32 = attributeNamed("$field32");
		
		assertEquals(0, main3.getIncomingInvocations().size());
		assertEquals(2, main3.getOutgoingInvocations().size());
		assertEquals(1, helper31.getIncomingInvocations().size());
		assertEquals(0, helper31.getOutgoingInvocations().size());
		assertEquals(1, helper32.getIncomingInvocations().size());
		assertEquals(0, helper32.getOutgoingInvocations().size());
		
		assertInvocationsBetweenMethods(main3, helper31, 1, field31);
		assertInvocationsBetweenMethods(main3, helper32, 1, field32);
	}
	
	@Test
	public void testMainRecursionInvocations() {
		Method mainRecursion = methodNamed("mainRecursion");
		Attribute fieldRecursion = attributeNamed("$fieldRecursion");
		
		assertEquals(1, mainRecursion.getIncomingInvocations().size());
		assertEquals(1, mainRecursion.getOutgoingInvocations().size());
		
		assertInvocationsBetweenMethods(mainRecursion, mainRecursion, 1, fieldRecursion);
	}
}
