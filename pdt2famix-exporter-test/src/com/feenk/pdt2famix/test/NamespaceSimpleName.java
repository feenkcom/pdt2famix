package com.feenk.pdt2famix.test;

import org.junit.Test;

import com.feenk.pdt2famix.test.support.InPhpTestCase;

public class NamespaceSimpleName extends InPhpTestCase {

	protected String sampleDirectory() {
		return "namespace_simple_name";
	}
	
	@Test
	public void testNamespaceStructure() {
		assertNamespacesPresent(new String[] {"name"});
		assertEmptyNamespace("name");
	}
}
