package com.feenk.pdt2famix.test.oneSample;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.feenk.pdt2famix.test.support.OneSampleTestCase;

public class ClassWithSingleTraitUsageTest extends OneSampleTestCase {
	
	protected String sample() {
		return removeTestSuffix(ClassWithSingleTraitUsageTest.class.getSimpleName());
	}
	
	//@Test
	public void testModelSize() {
		assertEquals(1, importer.namespaces().size());
		assertEquals(2, importer.types().size());
		assertEquals(0, importer.methods().size());
		assertEquals(0, importer.attributes().size());
	}

}
