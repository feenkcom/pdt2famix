package com.feenk.pdt2famix.test.oneSample;

import static org.junit.Assert.assertEquals;

import java.util.Collection;

import org.junit.Test;

import com.feenk.pdt2famix.model.famix.Invocation;
import com.feenk.pdt2famix.model.famix.Method;
import com.feenk.pdt2famix.model.famix.Parameter;
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
		assertEquals(5, importer.parameters().size());
	}
	
	@Test
	public void testMain1Invocations() {
		Method main1 = methodNamed("main1");
		Method helper1 = methodNamed("helper1");
		Method helper2 = methodNamed("helper2");
		
		assertMethodParameter("$param1", main1, type);
		assertMethodParameter("$param2", main1, type);
		
		assertEquals(0, main1.getIncomingInvocations().size());
		assertEquals(2, main1.getOutgoingInvocations().size());
		assertEquals(1, helper1.getIncomingInvocations().size());
		assertEquals(0, helper1.getOutgoingInvocations().size());
		assertEquals(3, helper2.getIncomingInvocations().size());
		assertEquals(0, helper2.getOutgoingInvocations().size());
		
		assertInvocationsBetweenMethods(main1, helper1, 1, parameterInBehaviour(main1, "$param1"));
		assertInvocationsBetweenMethods(main1, helper2, 1, parameterInBehaviour(main1, "$param2"));
	}
	
	@Test
	public void testMain2Invocations() {
		Method main2 = methodNamed("main2");
		Method helper2 = methodNamed("helper2");
		Parameter parameter1 = parameterInBehaviour(main2, "$param1");
		Parameter parameter2 = parameterInBehaviour(main2, "$param2");
		
		assertMethodParameter("$param1", main2, type);
		assertMethodParameter("$param2", main2, type);
		
		assertEquals(0, main2.getIncomingInvocations().size());
		assertEquals(2, main2.getOutgoingInvocations().size());
		
		Collection<Invocation> outgoingInvocation = main2.getOutgoingInvocations();
		Invocation firstInvocation = outgoingInvocation.stream().sequential().findFirst().get();
		Invocation secondInvocation = outgoingInvocation.stream().sequential().skip(1).findFirst().get();
		
		if (firstInvocation.getReceiver().equals(parameter1) ) {
			assertInvocationProperties(firstInvocation, main2, helper2, parameter1);
			assertInvocationProperties(secondInvocation, main2, helper2, parameter2);
		} else {
			assertInvocationProperties(firstInvocation, main2, helper2, parameter2);
			assertInvocationProperties(secondInvocation, main2, helper2, parameter1);
		}
	}
	
	@Test
	public void testMainRecursionInvocations() {
		Method mainRecursion = methodNamed("mainRecursion");
		
		assertMethodParameter("$param", mainRecursion, type);
		
		assertEquals(1, mainRecursion.getIncomingInvocations().size());
		assertEquals(1, mainRecursion.getOutgoingInvocations().size());
		
		assertInvocationsBetweenMethods(mainRecursion, mainRecursion, 1, parameterInBehaviour(mainRecursion, "$param"));
	}

}
