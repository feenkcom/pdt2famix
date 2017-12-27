package com.feenk.pdt2famix.test.oneSample;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.feenk.pdt2famix.model.famix.Method;
import com.feenk.pdt2famix.model.famix.Type;
import com.feenk.pdt2famix.test.support.OneSampleTestCase;

public class ClassWithSuperTraitCallsTest extends OneSampleTestCase {

	protected String sample() {
		return removeTestSuffix(ClassWithSuperTraitCallsTest.class.getSimpleName());
	}

	@Test
	public void testModelSize() {
		assertEquals(1, importer.namespaces().size());
		assertEquals(2, importer.types().size());
		assertEquals(5, importer.methods().size());
		assertEquals(0, importer.attributes().size());
		assertEquals(2, importer.currentInvocations().size());
		assertEquals(0, importer.currentAccesses().size());
		assertEquals(1, importer.currentTraitUsages().size());
	}
	
	@Test
	public void testMain1Invocations() {
		Type trait = this.typeNamed("TraitForClassWithSuperTraitCalls");
		Method main1 = methodNamed("main1");
		Method helper1 = methodInType(trait, "helper1");
		
		assertEquals(0, main1.getIncomingInvocations().size());
		assertEquals(1, main1.getOutgoingInvocations().size());
		assertEquals(1, helper1.getIncomingInvocations().size());
		assertEquals(0, helper1.getOutgoingInvocations().size());
		
		assertInvocationsBetweenMethods(main1, helper1, 1, null);
	}
	
	@Test
	public void testMain2Invocations() {
		Method main2 = methodNamed("main2");
		Method helper2 = methodInType(type, "helper2");
		
		assertEquals(0, main2.getIncomingInvocations().size());
		assertEquals(1, main2.getOutgoingInvocations().size());
		assertEquals(1, helper2.getIncomingInvocations().size());
		assertEquals(0, helper2.getOutgoingInvocations().size());
		
		assertInvocationsBetweenMethods(main2, helper2, 1, null);
	}

}
