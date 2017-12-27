package com.feenk.pdt2famix.test.oneSample;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.feenk.pdt2famix.Importer;
import com.feenk.pdt2famix.test.support.OneSampleTestCase;

public class TraitInImplicitNamespaceTest extends OneSampleTestCase {
	private static final String TRAIT_NAME = removeTestSuffix(TraitInImplicitNamespaceTest.class.getSimpleName());
	
	protected String sample() {
		return TRAIT_NAME;
	}
	
	@Test
	public void testModelSize() {
		assertEquals(1, importer.namespaces().size());
		assertEquals(1, importer.types().size());
	}
	
	@Test
	public void testNamespace() {	
		assertNamespacesPresent(Importer.DEFAULT_NAMESPACE_NAME);
		assertNamespaceTypes(Importer.DEFAULT_NAMESPACE_NAME, type);
	}
	
	@Test
	public void testTraitEntity() {	
		assertTraitType(type, TRAIT_NAME);		
	}

}
