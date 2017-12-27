package com.feenk.pdt2famix.test.oneSample;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.feenk.pdt2famix.Importer;
import com.feenk.pdt2famix.test.support.OneSampleTestCase;

public class ClassImplementingInterfacesTest extends OneSampleTestCase {
	private static final String INTERFACE_NAMESPACE = "for_ClassImplementingInterfaces";
	
	@Override
	protected String sample() {
		return removeTestSuffix(ClassImplementingInterfacesTest.class.getSimpleName());
	}
	
	@Test
	public void testModelSize() {
		assertEquals(2, importer.namespaces().size());
		assertEquals(3, importer.types().size());
		assertEquals(7, importer.methods().size());
	}
	
	@Test
	public void testNamespace() {	
		assertNamespacesPresent(Importer.DEFAULT_NAMESPACE_NAME, INTERFACE_NAMESPACE);
		assertNamespaceTypes(Importer.DEFAULT_NAMESPACE_NAME, type, typeWithIndentifier(typeIdentifier("InterfaceWithOneMethodAndConstructorAB")));
		assertNamespaceTypes(INTERFACE_NAMESPACE, typeNamed("InterfaceInAnExplicitNamespaceAB"));
	}
	
	@Test
	public void testClassImplementingInterfaces() {
		assertInheritance(type, typeWithIndentifier(typeIdentifier("InterfaceWithOneMethodAndConstructorAB")));
		assertInheritance(type, typeWithIndentifier(typeIdentifier(INTERFACE_NAMESPACE, "InterfaceInAnExplicitNamespaceAB")));
	}
	
	@Test
	public void testClassMethod() {	
		assertClassMethods(type, "__construct", "test1", "test2");
		assertMethod(methodInType(type, "__construct"), new String[] {"public"}, Importer.CONSTRUCTOR_KIND);
		assertMethod(methodInType(type, "test1"), new String[] {"public"});
		assertMethod(methodInType(type, "test2"), new String[] {"public"});
	}
}
