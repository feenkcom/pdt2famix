package com.feenk.pdt2famix.test.oneSample;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.feenk.pdt2famix.exporter.model.famix.Method;
import com.feenk.pdt2famix.test.support.OneSampleTestCase;

public class MethodsWithVariousStaticCallsTest extends OneSampleTestCase {

	protected String sample() {
		return removeTestSuffix(MethodsWithVariousStaticCallsTest.class.getSimpleName());
	}

	@Test
	public void testModelSize() {
		assertEquals(1, importer.namespaces().size());
		assertEquals(2, importer.types().size());
		assertEquals(11+2, importer.methods().size());
		assertEquals(6+2, importer.currentInvocations().size());
		assertEquals(1, importer.currentAccesses().size()); // Not good. 
		assertEquals(1, importer.attributes().size());
		assertEquals(2, importer.parameters().size());
	}
	
	@Test
	public void testMain1StaticCallUsingParameter() {
		Method mainMethod = methodNamed("main1");
		Method helperMethod = methodNamed("helper1");
		
		assertOneInvocation(mainMethod, helperMethod);
		
		assertStaticMethod(mainMethod, new String[] {"public"}, null);
		assertStaticMethod(helperMethod, new String[] {"private"}, null);
		assertInvocationsBetweenMethods(mainMethod, helperMethod, 1, parameterInBehaviour(mainMethod, "$a"));
	}

	@Test
	public void testMain2StatiCallUsingAttribute() {
		Method mainMethod = methodNamed("main2");
		Method helperMethod = methodNamed("helper2");
		
		assertOneInvocation(mainMethod, helperMethod);
		
		assertMethod(mainMethod, new String[] {"public"}, null);
		assertStaticMethod(helperMethod, new String[] {"private"}, null);
		assertInvocationsBetweenMethods(mainMethod, helperMethod, 1, attributeInType(type, "$field2"));
	}
	
	@Test
	public void testMain3StatiCallThroughReturnedValue() {
		Method mainMethod = methodNamed("main3");
		Method helperMethod31 = methodNamed("helper31");
		Method helperMethod32 = methodNamed("helper32");
		
		assertEquals(0, mainMethod.getIncomingInvocations().size());
		assertEquals(2, mainMethod.getOutgoingInvocations().size());
		assertEquals(1, helperMethod31.getIncomingInvocations().size());
		assertEquals(1, helperMethod31.getOutgoingInvocations().size());
		assertEquals(1, helperMethod32.getIncomingInvocations().size());
		assertEquals(0, helperMethod32.getOutgoingInvocations().size());
		
		assertMethod(mainMethod, new String[] {"public"}, null);
		assertMethod(helperMethod31, new String[] {"private"}, null);
		assertStaticMethod(helperMethod32, new String[] {"private"}, null);
		assertInvocationsBetweenMethods(mainMethod, helperMethod32, 1, null);
	}
	
	@Test
	public void testMain4StatiCallToMethodInAnotherClass() {
		Method mainMethod = methodNamed("main4");
		Method helperMethod = methodInType(typeNamed("Util_ForMethodsWithVariousStaticCalls"), "helper4");
		
		assertOneInvocation(mainMethod, helperMethod);
		
		assertMethod(mainMethod, new String[] {"public"}, null);
		assertStaticMethod(helperMethod, new String[] {"public"}, null);
		assertInvocationsBetweenMethods(mainMethod, helperMethod, 1, null);
	}
	
	@Test
	public void testMain5StatiCallUsingParameterToMethodInAnotherClass() {
		Method mainMethod = methodNamed("main5");
		Method helperMethod = methodInType(typeNamed("Util_ForMethodsWithVariousStaticCalls"), "helper5");
		
		assertOneInvocation(mainMethod, helperMethod);
		
		assertMethod(mainMethod, new String[] {"public"}, null);
		assertStaticMethod(helperMethod, new String[] {"public"}, null);
		assertInvocationsBetweenMethods(mainMethod, helperMethod, 1, parameterInBehaviour(mainMethod, "$a"));
	}
}
