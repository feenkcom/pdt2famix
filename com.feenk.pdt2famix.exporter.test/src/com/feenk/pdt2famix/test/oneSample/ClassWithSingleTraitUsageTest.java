package com.feenk.pdt2famix.test.oneSample;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.feenk.pdt2famix.exporter.model.famix.Trait;
import com.feenk.pdt2famix.exporter.model.famix.TraitUsage;
import com.feenk.pdt2famix.exporter.model.famix.Type;
import com.feenk.pdt2famix.test.support.OneSampleTestCase;

public class ClassWithSingleTraitUsageTest extends OneSampleTestCase {
	
	protected String sample() {
		return removeTestSuffix(ClassWithSingleTraitUsageTest.class.getSimpleName());
	}
	
	@Test
	public void testModelSize() {
		assertEquals(1, importer.namespaces().size());
		assertEquals(2, importer.types().size());
		assertEquals(0, importer.methods().size());
		assertEquals(0, importer.attributes().size());
		assertEquals(1, importer.currentTraitUsages().size());
	}

	@Test
	public void testTraitUsage() {
		Type targetClass = typeNamed("ClassWithSingleTraitUsage");
		Trait trait = (Trait) typeNamed("TraitForClassWithSingleTraitUsage");
		TraitUsage traitUsage;
		
		
		assertEquals(1, targetClass.getOutgoingTraitUsages().size());
		assertEquals(1, trait.getIncomingTraitUsages().size());
		assertEquals(0, trait.getOutgoingTraitUsages().size());
		
		traitUsage = targetClass.getOutgoingTraitUsages().stream().findFirst().get();
		assertEquals(traitUsage, trait.getIncomingTraitUsages().stream().findFirst().get());
		assertEquals(targetClass, traitUsage.getUser());
		assertEquals(trait, traitUsage.getTrait());
	}
}
