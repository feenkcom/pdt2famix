package com.feenk.pdt2famix.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.feenk.pdt2famix.test.support.InPhpTestCase;

public class NamespaceSimpleName extends InPhpTestCase {

	protected String sampleDirectory() {
		return "namespace_simple_name";
	}
	
	@Test
	public void testOneNamespace() {
		assertEquals(1, importer.namespaces().size());
	}
	
	@Test
	public void testNamespaceStructure() {
		assertEmptyNamespace("name");
	}
}
