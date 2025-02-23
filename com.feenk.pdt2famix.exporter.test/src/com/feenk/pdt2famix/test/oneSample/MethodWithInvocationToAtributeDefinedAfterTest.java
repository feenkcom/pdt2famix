package com.feenk.pdt2famix.test.oneSample;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.feenk.pdt2famix.exporter.model.famix.Attribute;
import com.feenk.pdt2famix.exporter.model.famix.Method;
import com.feenk.pdt2famix.test.support.OneSampleTestCase;

public class MethodWithInvocationToAtributeDefinedAfterTest extends OneSampleTestCase {

	protected String sample() {
		return removeTestSuffix(MethodWithInvocationToAtributeDefinedAfterTest.class.getSimpleName());
	}

	@Test
	public void testModelSize() {
		assertEquals(1, importer.namespaces().size());
		assertEquals(1, importer.types().size());
		assertEquals(3+2, importer.methods().size());
		assertEquals(2+2, importer.currentInvocations().size());
		assertEquals(2, importer.currentAccesses().size());
		assertEquals(2, importer.attributes().size());
		assertEquals(0, importer.parameters().size());
	}
	
	@Test
	public void testMain1Invocations() {
		Method main1 = methodNamed("main1");
		Method helper1 = methodNamed("helper1");
		Method helper2 = methodNamed("helper2");
		Attribute field1 = attributeNamed("$field1");
		Attribute field2 = attributeNamed("$field2");
		
		assertEquals(0, main1.getIncomingInvocations().size());
		assertEquals(2, main1.getOutgoingInvocations().size());
		assertEquals(1, helper1.getIncomingInvocations().size());
		assertEquals(0, helper1.getOutgoingInvocations().size());
		assertEquals(1, helper2.getIncomingInvocations().size());
		assertEquals(0, helper2.getOutgoingInvocations().size());
		
		assertInvocationsBetweenMethods(main1, helper1, 1, field1);
		assertInvocationsBetweenMethods(main1, helper2, 1, field2);
		
		assertAttribute("$field1", type);
		assertAttribute("$field2", type);
	}

}
