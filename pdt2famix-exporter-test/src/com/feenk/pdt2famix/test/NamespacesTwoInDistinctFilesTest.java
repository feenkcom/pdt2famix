package com.feenk.pdt2famix.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.feenk.pdt2famix.test.support.InPhpTestCase;

public class NamespacesTwoInDistinctFilesTest extends InPhpTestCase {

	protected String sampleDirectory() {
		return "namespaces_two_in_distinct_files";
	}
	
	@Test
	public void testTwoNamespace() {
		assertEquals(2, importer.namespaces().size());
	}
	
	@Test
	public void testNamespacesStructure() {
		assertEmptyNamespace("namespace1");
		assertEmptyNamespace("namespace2");
	}
}
