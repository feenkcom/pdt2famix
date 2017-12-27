package com.feenk.pdt2famix.test;

import org.junit.Test;

import com.feenk.pdt2famix.test.support.InPhpTestCase;

public class NamespacesTwoInSingleFile extends InPhpTestCase {

	protected String sampleDirectory() {
		return "namespaces_two_in_single_file";
	}
	
	@Test
	public void testNamespacesStructure() {
		assertNamespacesPresent(new String[] {"namespace1", "namespace2"});
		assertEmptyNamespace("namespace1");
		assertEmptyNamespace("namespace2");
	}
}
