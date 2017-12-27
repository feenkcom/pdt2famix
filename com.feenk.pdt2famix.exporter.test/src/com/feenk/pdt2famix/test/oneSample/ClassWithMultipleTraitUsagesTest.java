package com.feenk.pdt2famix.test.oneSample;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

import com.feenk.pdt2famix.exporter.model.famix.Trait;
import com.feenk.pdt2famix.exporter.model.famix.TraitUsage;
import com.feenk.pdt2famix.exporter.model.famix.Type;
import com.feenk.pdt2famix.test.support.OneSampleTestCase;

public class ClassWithMultipleTraitUsagesTest extends OneSampleTestCase {
	
	protected String sample() {
		return removeTestSuffix(ClassWithMultipleTraitUsagesTest.class.getSimpleName());
	}
	
	@Test
	public void testModelSize() {
		assertEquals(1, importer.namespaces().size());
		assertEquals(3, importer.types().size());
		assertEquals(0, importer.methods().size());
		assertEquals(0, importer.attributes().size());
		assertEquals(2, importer.currentTraitUsages().size());
	}

	@Test
	public void testTraitUsage() {
		Type targetClass = typeNamed("ClassWithMultipleTraitUsages");
		Trait traitA = (Trait) typeNamed("TraitAForClassWithMultipleTraitUsages");
		Trait traitB = (Trait) typeNamed("TraitBForClassWithMultipleTraitUsages");
		TraitUsage traitUsageA, traitUsageB;
		
		
		assertEquals(2, targetClass.getOutgoingTraitUsages().size());
		assertEquals(1, traitA.getIncomingTraitUsages().size());
		assertEquals(0, traitA.getOutgoingTraitUsages().size());
		assertEquals(1, traitB.getIncomingTraitUsages().size());
		assertEquals(0, traitB.getOutgoingTraitUsages().size());
		
		traitUsageA = traitA.getIncomingTraitUsages().stream().findFirst().get();
		traitUsageB = traitB.getIncomingTraitUsages().stream().findFirst().get();
		
		assertEquals(targetClass, traitUsageA.getUser());
		assertEquals(targetClass, traitUsageB.getUser());
		assertEquals(traitA, traitUsageA.getTrait());
		assertEquals(traitB, traitUsageB.getTrait());
		assertEquals(new HashSet<>(Arrays.asList(traitUsageA, traitUsageB)), new HashSet<>(targetClass.getOutgoingTraitUsages()));
	}
	
}
