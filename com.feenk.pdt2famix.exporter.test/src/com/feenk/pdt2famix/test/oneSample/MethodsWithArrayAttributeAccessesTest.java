package com.feenk.pdt2famix.test.oneSample;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.feenk.pdt2famix.exporter.model.famix.Attribute;
import com.feenk.pdt2famix.exporter.model.famix.Method;
import com.feenk.pdt2famix.exporter.model.famix.Parameter;
import com.feenk.pdt2famix.test.support.OneSampleTestCase;

public class MethodsWithArrayAttributeAccessesTest extends OneSampleTestCase {
	
	protected String sample() {
		return removeTestSuffix(MethodsWithArrayAttributeAccessesTest.class.getSimpleName());
	}
	
	@Test
	public void testModelSize() {
		assertEquals(2, importer.namespaces().size());
		assertEquals(3, importer.types().size());
		assertEquals(5+1, importer.methods().size());
		assertEquals(6, importer.attributes().size());
		assertEquals(0, importer.currentInvocations().size());
		assertEquals(8, importer.currentAccesses().size());
		assertEquals(1, importer.parameters().size());
	}
	
	@Test
	public void testAttributeTypes() {
		assertAttribute("$field1", null); // Type could be inferred based on the assigned value
		assertAttribute("$field2", arrayType());
		assertAttribute("$field3", arrayType());
		assertAttribute("$field4", null); // seems that here type inference does not work
		assertAttribute("$simpleField5", stringType());
		assertAttribute("$field6", null); // seems that here type inference does not work
	}

	@Test
	public void testInitializingAttributeWithArray()  {
		Method main1 = methodNamed("main1");
		Attribute field1 = attributeNamed("$field1");
		
		assertEquals(1, main1.getAccesses().size());
		assertEquals(1, field1.getIncomingAccesses().size());
		assertAccess(main1, field1, true);
	}
	
	@Test
	public void testOneKeyArrayAccess()  {
		Method main2 = methodNamed("main2");
		Attribute field2 = attributeNamed("$field2");
		
		assertEquals(1, main2.getAccesses().size());
		assertEquals(1, field2.getIncomingAccesses().size());
		assertAccess(main2, field2, false);
	}
	
	@Test
	public void testTwoKeyArrayAccessesWithStringKey()  {
		Method main3 = methodNamed("main3");
		Attribute field3 = attributeNamed("$field3");
		
		assertEquals(2, main3.getAccesses().size());
		assertEquals(2, field3.getIncomingAccesses().size());
		assertAccess(main3, field3, 2);
	}
	
	@Test
	public void testTwoKeyArrayAccessesWithVariableKey()  {
		Method main4 = methodNamed("main4");
		Attribute field4 = attributeNamed("$field4");
		Attribute simpleField5 = attributeNamed("$simpleField5");
		Parameter parameter = parameterInBehaviour(main4, "$name");
		
		assertEquals(3 /*+1*/, main4.getAccesses().size()); // here the access to the parameter is not resolved
		assertEquals(2, field4.getIncomingAccesses().size());
		assertEquals(1, simpleField5.getIncomingAccesses().size());
		assertEquals(0, parameter.getIncomingAccesses().size()); // should also be resolved
		
		assertAccess(main4, field4, 2);
		assertAccess(main4, simpleField5, 1);
	}
	
	@Test
	public void testSimpleAssignmentInArray()  {
		Method main6 = methodNamed("main6");
		Attribute field6 = attributeNamed("$field6");
		
		assertEquals(1, main6.getAccesses().size());
		assertEquals(1, field6.getIncomingAccesses().size());
		assertAccess(main6, field6, false); // TODO: fix me
	}

}
