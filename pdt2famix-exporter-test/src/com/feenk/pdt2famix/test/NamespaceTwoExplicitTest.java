package com.feenk.pdt2famix.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.feenk.pdt2famix.test.support.InPhpTestCase;

public class NamespaceTwoExplicitTest extends InPhpTestCase {
  
	protected String sampleDirectory() {
		return "namespace_two_explicit";
	}
	
	@Test
	public void testTwoNamespace() {
		assertEquals(2, importer.namespaces().size());
	}
	
	@Test
	public void testNamespacesStructure() {
		assertEmptyNamespace("name1");
		assertEmptyNamespace("name2");
	}

}
