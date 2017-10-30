package com.feenk.pdt2famix.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.feenk.pdt2famix.model.famix.Type;
import com.feenk.pdt2famix.test.support.InPhpTestCase;

public class ClassWithPrimitiveAttributesTest extends InPhpTestCase {
	private static final String CLASS_NAME = "ClassWithPrimitiveAttributes";
	protected String sampleDirectory() {
		return "class_with_primitive_attributes";
	}
	
	@Test
	public void testClassType() {
		Type classType = importer.types().named("\\"+CLASS_NAME);
		
		assertEquals(CLASS_NAME, classType.getName());
		assertEquals(false, classType.getIsStub());
		assertTrue(classType instanceof com.feenk.pdt2famix.model.famix.Class);
		assertEquals(false, ((com.feenk.pdt2famix.model.famix.Class)(classType)).getIsInterface());
		
		assertClassContainer("\\"+CLASS_NAME, "");
	}
	
	private void assertClassContainer(String classQualifiedname, String containerQualifiedName) {
		assertEquals(
			importer.namespaces().named(containerQualifiedName), 
			importer.types().named(classQualifiedname).getContainer());
	}

	@Test
	public void testDefaultNamespace() {
//		assertNamespacePresent("");
//		assertNamespaceTypes(new String[] {"/ClassWithPrimitiveAttributes"});
	}
	
}
