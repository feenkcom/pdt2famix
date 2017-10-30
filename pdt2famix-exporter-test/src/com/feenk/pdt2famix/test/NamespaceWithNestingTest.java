package com.feenk.pdt2famix.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.feenk.pdt2famix.test.support.InPhpTestCase;

public class NamespaceWithNestingTest extends InPhpTestCase {

	protected String sampleDirectory() {
		return "namespace_with_nesting";
	}
	
	@Test
	public void testTwoNamespace() {
		assertEquals(3, importer.namespaces().size());
	}
	
	@Test
	public void testNamespacesStructure() {
		assertEmptyNamespace("root");
		assertEmptyNamespace("root\\nesting", "root");
		assertEmptyNamespace("root\\nesting\\leaf", "root\\nesting");
	}

}
