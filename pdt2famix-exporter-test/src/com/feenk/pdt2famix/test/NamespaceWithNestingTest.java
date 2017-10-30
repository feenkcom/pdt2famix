package com.feenk.pdt2famix.test;

import org.junit.Test;

import com.feenk.pdt2famix.test.support.InPhpTestCase;

public class NamespaceWithNestingTest extends InPhpTestCase {

	protected String sampleDirectory() {
		return "namespace_with_nesting";
	}
	
	@Test
	public void testNamespacesStructure() {
		assertNamespacesPresent(new String[] {"root", "root\\nesting", "root\\nesting\\leaf"});
		assertEmptyNamespace("root");
		assertEmptyNamespace("root\\nesting", "root");
		assertEmptyNamespace("root\\nesting\\leaf", "root\\nesting");
	}

}
