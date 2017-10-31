package com.feenk.pdt2famix.test;

import org.junit.Test;

import com.feenk.pdt2famix.Importer;
import com.feenk.pdt2famix.test.support.InPhpTestCase;

public class NamespaceDefaultThreeClasses  extends InPhpTestCase {

	protected String sampleDirectory() {
		return "namespace_default_three_classes";
	}
	
	@Test
	public void testNamespaceStructure() {	
		assertNamespacesPresent(new String[] {Importer.DEFAULT_NAMESPACE_NAME});
		assertClassPresent("TestClassOneAB");
		assertClassPresent("TestClassTwoAB");
		assertClassPresent("TestClassThreeAB");
		
		assertNamespaceTypes(Importer.DEFAULT_NAMESPACE_NAME, new String[] {
				"TestClassOneAB",
				"TestClassTwoAB",
				"TestClassThreeAB"});
	}
}
