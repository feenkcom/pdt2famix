package com.feenk.pdt2famix.test.oneSample;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.Test;

import com.feenk.pdt2famix.exporter.model.famix.Attribute;
import com.feenk.pdt2famix.exporter.model.famix.Method;
import com.feenk.pdt2famix.test.support.OneSampleTestCase;

public class ClassWithConstDefinitionsAndAccessesTest extends OneSampleTestCase {

	protected String sample() {
		return removeTestSuffix(ClassWithConstDefinitionsAndAccessesTest.class.getSimpleName());
	}

	@Test
	public void testModelSize() {
		assertEquals(2, importer.namespaces().size());
		assertEquals(5, importer.types().size());
		assertEquals(4+2, importer.methods().size());
		assertEquals(6, importer.attributes().size());
		assertEquals(1, importer.currentInvocations().size());
		assertEquals(3, importer.currentAccesses().size()); // Only accesses using :: are resolved
		assertEquals(0, importer.parameters().size());
	}
		
	@Test
	public void testClassConstants() {		
		assertAttribute("CONST_INT1", type, numberType(), true, new String[] {"const", "public"});
		assertAttribute("CONST_BOOL1", type, booleanType(), true, new String[] {"const", "public"});
		assertAttribute("CONST_STRING1", type, stringType(), true, new String[] {"const", "private"});
		assertAttribute("CONST_STRING2", type, stringType(), true, new String[] {"const", "protected"});
		assertAttribute("ANIMALS", type, arrayType(), true, new String[] {"const", "public"});
		assertAttribute("CONST_OBJECT", type, type, true, new String[] {"const", "public"});
	}

	@Test
	public void testAccessUsingSelfInInstanceSideMethod()  {
		Method main2 = methodNamed("main2SelfConstAccess");
		Attribute constBool1 = attributeNamed("CONST_BOOL1");
		
		assertEquals(1, main2.getAccesses().size());
		assertEquals(1, constBool1.getIncomingAccesses().size());
		
		assertAccess(main2, constBool1, 1, Optional.of(false));
	}
	
	@Test
	public void testAccessUsingClassName()  {
		Method main3 = methodNamed("main3ClassNameConstAccess");
		Attribute constObject = attributeNamed("CONST_OBJECT");
		
		assertEquals(1, main3.getAccesses().size());
		assertEquals(1, constObject.getIncomingAccesses().size());
		
		assertAccess(main3, constObject, 1, Optional.of(false));
	}
	
	@Test
	public void testAccessUsingSelfInStaticMethod()  {
		Method main4 = methodNamed("main4ReturnConstantStatic");
		Attribute constString1 = attributeNamed("CONST_STRING1");
		
		assertEquals(1, main4.getAccesses().size());
		assertEquals(1, constString1.getIncomingAccesses().size());
		
		assertAccess(main4, constString1, 1, Optional.of(false));
	}
	
}
	
