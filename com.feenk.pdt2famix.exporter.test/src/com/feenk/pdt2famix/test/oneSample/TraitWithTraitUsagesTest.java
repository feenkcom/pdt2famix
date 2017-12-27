package com.feenk.pdt2famix.test.oneSample;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.feenk.pdt2famix.model.famix.Trait;
import com.feenk.pdt2famix.test.support.OneSampleTestCase;

public class TraitWithTraitUsagesTest extends OneSampleTestCase {
	
	protected String sample() {
		return removeTestSuffix(TraitWithTraitUsagesTest.class.getSimpleName());
	}
	
	@Test
	public void testModelSize() {
		assertEquals(1, importer.namespaces().size()); 
		assertEquals(4, importer.types().size());      
		assertEquals(0, importer.methods().size());
		assertEquals(0, importer.attributes().size());
		assertEquals(0, importer.parameters().size());
		assertEquals(0, importer.currentInvocations().size());
		assertEquals(3, importer.currentTraitUsages().size());
	}
	
	@Test
	public void testTraitUsageSizes() {
		Trait mainTrait = (Trait) typeNamed("TraitWithTraitUsages");
		Trait rootTrait = (Trait) typeNamed("RootTraitForTraitWithTraitUsages");
		Trait helloTrait = (Trait) typeNamed("HelloTraitForTraitWithTraitUsages");
		Trait worldTrait = (Trait) typeNamed("WorldTraitForTraitWithTraitUsages");
		
		assertEquals(0, rootTrait.getOutgoingTraitUsages().size());
		assertEquals(1, rootTrait.getIncomingTraitUsages().size());
		assertEquals(1, helloTrait.getOutgoingTraitUsages().size());
		assertEquals(1, helloTrait.getIncomingTraitUsages().size());
		assertEquals(0, worldTrait.getOutgoingTraitUsages().size());
		assertEquals(1, worldTrait.getIncomingTraitUsages().size());
		assertEquals(2, mainTrait.getOutgoingTraitUsages().size());
		assertEquals(0, mainTrait.getIncomingTraitUsages().size());
	}

	@Test 
	public void testTraitUsageRelations() {
		Trait mainTrait = (Trait) typeNamed("TraitWithTraitUsages");
		Trait rootTrait = (Trait) typeNamed("RootTraitForTraitWithTraitUsages");
		Trait helloTrait = (Trait) typeNamed("HelloTraitForTraitWithTraitUsages");
		Trait worldTrait = (Trait) typeNamed("WorldTraitForTraitWithTraitUsages");
		
		assertTraitUsage(helloTrait, rootTrait);
		assertTraitUsage(mainTrait, helloTrait);
		assertTraitUsage(mainTrait, worldTrait);
	}
	
}
