package com.feenk.pdt2famix.test.oneSample;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.feenk.pdt2famix.exporter.model.famix.Method;
import com.feenk.pdt2famix.exporter.model.famix.Parameter;
import com.feenk.pdt2famix.test.support.OneSampleTestCase;

public class MethodsWithAccessesToLocalParametersTest extends OneSampleTestCase {
	
	protected String sample() {
		return removeTestSuffix(MethodsWithAccessesToLocalParametersTest.class.getSimpleName());
	}
	
	@Test
	public void testModelSize() {
		assertEquals(2, importer.namespaces().size());
		assertEquals(3, importer.types().size());
		assertEquals(3, importer.methods().size());
		assertEquals(0, importer.attributes().size());
		assertEquals(0, importer.currentInvocations().size());
		assertEquals(0, importer.currentAccesses().size());
		assertEquals(5, importer.parameters().size());
	}
	
	//@Test
	public void testSingleAccesses()  {
		Method method = methodNamed("methodWithSingleAcccesses");
		Parameter a = parameterInBehaviour(method, "$a");
		Parameter b = parameterInBehaviour(method, "$b");
		
		assertEquals(2, method.getAccesses().size());
		assertEquals(1, a.getIncomingAccesses().size());
		assertEquals(1, b.getIncomingAccesses().size());
		
		assertAccess(method, a, false);
		assertAccess(method, b, false);
	}

}
