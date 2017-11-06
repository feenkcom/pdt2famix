package com.feenk.pdt2famix.test.oneSample;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.feenk.pdt2famix.Importer;
import com.feenk.pdt2famix.test.support.OneSampleTestCase;

public class InterfaceWithSimpleMethodsTest extends OneSampleTestCase {
	
	protected String sample() {
		return removeTestSuffix(InterfaceWithSimpleMethodsTest.class.getSimpleName());
	}
	
	@Test
	public void testModelSize() {
		assertEquals(2 + 1, importer.namespaces().size());  // Account for generate unknown namespace
		assertEquals(2 + 1, importer.types().size());       // Account for generate unknown type
		assertEquals(4, importer.methods().size());
	}
	
	@Test
	public void testUnknownEntities() {
		assertUnknownNamespacePresent();
		assertUnknownTypePresent();
	}

	@Test
	public void testInterfaceMethod() {	
		assertClassMethods(type, "__construct", "foo", "bar", "baz");
		assertMethod(methodInType(type, "__construct"), new String[] {"public", "abstract"}, Importer.CONSTRUCTOR_KIND);
		assertMethod(methodInType(type, "foo"), new String[] {"public", "abstract"});
		assertMethod(methodInType(type, "bar"), new String[] {"public", "abstract"});
		assertMethod(methodInType(type, "baz"), new String[] {"public", "abstract"});
	}
	
}
