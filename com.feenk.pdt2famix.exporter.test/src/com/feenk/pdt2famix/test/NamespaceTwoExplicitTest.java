package com.feenk.pdt2famix.test;

import org.junit.Test;

import com.feenk.pdt2famix.test.support.InPhpTestCase;

public class NamespaceTwoExplicitTest extends InPhpTestCase {
  
	protected String sampleDirectory() {
		return "namespace_two_explicit";
	}
	
	@Test
	public void testNamespacesStructure() {
		assertNamespacesPresent(new String[] {"name1", "name2"});
		assertEmptyNamespace("name1");
		assertEmptyNamespace("name2");
	}

}
