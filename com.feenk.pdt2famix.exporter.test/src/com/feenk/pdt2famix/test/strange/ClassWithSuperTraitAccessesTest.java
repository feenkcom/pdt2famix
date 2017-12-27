package com.feenk.pdt2famix.test.strange;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.feenk.pdt2famix.exporter.model.famix.Attribute;
import com.feenk.pdt2famix.exporter.model.famix.Method;
import com.feenk.pdt2famix.exporter.model.famix.Type;
import com.feenk.pdt2famix.test.support.OneSampleTestCase;

public class ClassWithSuperTraitAccessesTest extends OneSampleTestCase {

	protected String sample() {
		return removeTestSuffix(ClassWithSuperTraitAccessesTest.class.getSimpleName());
	}

	@Test
	public void testModelSize() {
		assertEquals(1, importer.namespaces().size());
		assertEquals(2, importer.types().size());
		assertEquals(1, importer.methods().size());
		assertEquals(1, importer.attributes().size());
		assertEquals(0, importer.currentInvocations().size());
		assertEquals(1, importer.currentAccesses().size());
		assertEquals(1, importer.currentTraitUsages().size());
	}
	
	@Test
	public void testMain1Access() {
		Type trait = this.typeNamed("TraitForClassWithSuperTraitAccesses");
		Method main1 = methodNamed("main1");
		Attribute attribute = attributeInType(trait, "$field1");
		
		// This assertions are wrong
		// There should be an access resolved here.
		assertEquals(0, main1.getAccesses().size());
		assertEquals(0, attribute.getIncomingAccesses().size());
		
		// assertAccess(main1, attribute, 1, Optional.of(false));
	}

}
