package com.feenk.pdt2famix.test.oneSample;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.feenk.pdt2famix.model.famix.BehaviouralEntity;
import com.feenk.pdt2famix.model.famix.Method;
import com.feenk.pdt2famix.test.support.OneSampleTestCase;

public class MethodsWithMethodInvocationsToGenericParametersTest extends OneSampleTestCase {

	protected String sample() {
		return removeTestSuffix(MethodsWithMethodInvocationsToGenericParametersTest.class.getSimpleName());
	}

	@Test
	public void testModelSize() {
		assertEquals(1 + 1, importer.namespaces().size());
		assertEquals(3 + 1, importer.types().size());
		assertEquals(5, importer.methods().size());
		assertEquals(1+2, importer.currentInvocations().size());
		assertEquals(0 /* 3 */, importer.currentAccesses().size());
		assertEquals(0, importer.attributes().size());
		assertEquals(3, importer.parameters().size());
	}
	
	@Test
	public void testInvocationToUndefinedParameter() {
		Method method = methodNamed("main1");
		
		assertEquals(1, method.getParameters().size());
		assertMethodParameter("$x", method, null);
		
		assertEquals(1, method.getOutgoingInvocations().size());
		assertInvocationProperties(
				method.getOutgoingInvocations().stream().findFirst().get(), 
				method, 
				new BehaviouralEntity[] {}, 
				null, //parameterInBehaviour(method, "$x"), -- this is not resolved for now
				"$x->uniqueMethod_12A()");
	}
	
	@Test
	public void testInvocationFromUndefinedParameterToUniqueMethod() {
		Method method = methodNamed("main2");
		
		assertEquals(1, method.getParameters().size());
		
		assertMethodParameter("$x", method, null);
		assertEquals(1, method.getOutgoingInvocations().size());
		assertInvocationProperties(
				method.getOutgoingInvocations().stream().findFirst().get(), 
				method, 
				new BehaviouralEntity[] {}, 
				null, //this is not resolved for now
				"$x->uniqueMethod_12B()");
	}
	
	@Test
	public void testInvocationFromParameterWithTypeInMethod() {
		Method main3 = methodNamed("main3");
		Method uniqueMethod_12C = methodNamed("uniqueMethod_12C");
		
		assertEquals(1, main3.getParameters().size());
		assertMethodParameter("$x", main3, typeNamed("TestClass1_12C"));
		
		assertEquals(1, main3.getOutgoingInvocations().size());
		assertEquals(1, uniqueMethod_12C.getIncomingInvocations().size());
		assertInvocationsBetweenMethods(main3, uniqueMethod_12C, 1, parameterInBehaviour(main3, "$x"));
		assertInvocationProperties(
				main3.getOutgoingInvocations().stream().findFirst().get(), 
				main3, 
				new BehaviouralEntity[] {uniqueMethod_12C}, 
				parameterInBehaviour(main3, "$x"),
				"$x->uniqueMethod_12C()");
	}
}
