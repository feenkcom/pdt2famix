package com.feenk.pdt2famix.test.oneSample;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.feenk.pdt2famix.exporter.model.famix.Method;
import com.feenk.pdt2famix.test.support.OneSampleTestCase;

public class MethodsWithInternalStaticCallsTest extends OneSampleTestCase {

	protected String sample() {
		return removeTestSuffix(MethodsWithInternalStaticCallsTest.class.getSimpleName());
	}

	@Test
	public void testModelSize() {
		assertEquals(1, importer.namespaces().size());
		assertEquals(1, importer.types().size());
		assertEquals(10, importer.methods().size());
		assertEquals(5, importer.currentInvocations().size());
		assertEquals(0, importer.currentAccesses().size());
		assertEquals(0, importer.attributes().size());
		assertEquals(0, importer.parameters().size());
	}
	
	@Test
	public void testMain1StaticSelfCallFromStaticMethod() {
		Method mainMethod = methodNamed("main1");
		Method helperMethod = methodNamed("helper1");
		
		assertOneInvocation(mainMethod, helperMethod);
		
		assertStaticMethod(mainMethod, new String[] {"public"}, null);
		assertStaticMethod(helperMethod, new String[] {"private"}, null);
		assertInvocationsBetweenMethods(mainMethod, helperMethod, 1, null);
	}
	
	@Test
	public void testMain2StaticClassNameCallFromStaticMethod() {
		Method mainMethod = methodNamed("main2");
		Method helperMethod = methodNamed("helper2");
		
		assertOneInvocation(mainMethod, helperMethod);
		
		assertStaticMethod(mainMethod, new String[] {"public"}, null);
		assertStaticMethod(helperMethod, new String[] {"private"}, null);
		assertInvocationsBetweenMethods(mainMethod, helperMethod, 1, null);
	}
	
	@Test
	public void testMain3StaticStaticCallFromStaticMethod() {
		Method mainMethod = methodNamed("main3");
		Method helperMethod = methodNamed("helper3");
		
		assertOneInvocation(mainMethod, helperMethod);
		
		assertStaticMethod(mainMethod, new String[] {"public"}, null);
		assertStaticMethod(helperMethod, new String[] {"private"}, null);
		assertInvocationsBetweenMethods(mainMethod, helperMethod, 1, null);
	}
	
	@Test
	public void testMain4StaticSelfCallFromInstanceMethod() {
		Method mainMethod = methodNamed("main4");
		Method helperMethod = methodNamed("helper4");
		
		assertOneInvocation(mainMethod, helperMethod);
		
		assertMethod(mainMethod, new String[] {"public"}, null);
		assertStaticMethod(helperMethod, new String[] {"private"}, null);
		assertInvocationsBetweenMethods(mainMethod, helperMethod, 1, null);
	}
	
	@Test
	public void testMain4StaticThisCallFromInstanceMethod() {
		Method mainMethod = methodNamed("main5");
		Method helperMethod = methodNamed("helper5");
		
		assertOneInvocation(mainMethod, helperMethod);
		
		assertMethod(mainMethod, new String[] {"public"}, null);
		assertStaticMethod(helperMethod, new String[] {"private"}, null);
		assertInvocationsBetweenMethods(mainMethod, helperMethod, 1, null);
	}
}
