package com.feenk.pdt2famix.test.oneSample;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.feenk.pdt2famix.exporter.inphp.Importer;
import com.feenk.pdt2famix.test.support.OneSampleTestCase;

public class ClassWithSimpleMethods extends OneSampleTestCase {

	@Override
	protected String sample() {
		return removeTestSuffix(ClassWithSimpleMethods.class.getSimpleName());
	}
	
	@Test
	public void testModelSize() {
		assertEquals(2 + 1, importer.namespaces().size());  // Account for generate unknown namespace
		assertEquals(2 + 1, importer.types().size());       // Account for generate unknown type
		assertEquals(4, importer.methods().size());
		assertEquals(0, importer.currentInvocations().size());
		assertEquals(0, importer.currentAccesses().size());
		assertEquals(0, importer.attributes().size());
		assertEquals(3, importer.parameters().size());
		
	}
	
	@Test
	public void testUnknownEntities() {
		assertUnknownNamespacePresent();
		assertUnknownTypePresent();
	}
	
	@Test
	public void testNamespace() {	
		assertNamespacesPresent(Importer.DEFAULT_NAMESPACE_NAME, Importer.SYSTEM_NAMESPACE_NAME, Importer.UNKNOWN_NAME);
		assertNamespaceTypes(Importer.DEFAULT_NAMESPACE_NAME, type);
	}
	
	@Test
	public void testClassMethod() {	
		assertClassMethods(type, "method1", "method2", "method3", "method4");
		assertMethod(methodInType(type, "method1"), new String[] {"public"});
		assertMethod(methodInType(type, "method2"), new String[] {"protected"});
		assertMethod(methodInType(type, "method3"), new String[] {"private"});
		assertMethod(methodInType(type, "method4"), new String[] {"public"});
	}
	
	@Test
	public void testMethodParameters() {
		assertEquals(0, methodNamed("method1").getParameters().size());
		assertEquals(1, methodNamed("method2").getParameters().size());
		assertEquals(1, methodNamed("method3").getParameters().size());
		assertEquals(1, methodNamed("method4").getParameters().size());
		
		assertMethodParameter("$x", methodNamed("method2"), numberType());
		assertMethodParameter("$x", methodNamed("method3"), null);
		assertMethodParameter("$x", methodNamed("method4"), numberType());
	}

}
