package com.feenk.pdt2famix.test.oneSample;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.feenk.pdt2famix.exporter.model.famix.Attribute;
import com.feenk.pdt2famix.exporter.model.famix.Method;
import com.feenk.pdt2famix.test.support.OneSampleTestCase;

public class MethodsWithAttributeAccessesTest extends OneSampleTestCase {
	
	protected String sample() {
		return removeTestSuffix(MethodsWithAttributeAccessesTest.class.getSimpleName());
	}
	
	@Test
	public void testModelSize() {
		assertEquals(1, importer.namespaces().size());
		assertEquals(1, importer.types().size());
		assertEquals(6, importer.methods().size());
		assertEquals(6, importer.attributes().size());
		assertEquals(0, importer.currentInvocations().size());
		assertEquals(9, importer.currentAccesses().size());
		assertEquals(0, importer.parameters().size());
	}
	
	@Test
	public void testSingleAccess()  {
		Method main1 = methodNamed("main1");
		Attribute field1 = attributeNamed("$field1");
		
		assertEquals(1, main1.getAccesses().size());
		assertEquals(1, field1.getIncomingAccesses().size());
		
		assertAccess(main1, field1, false);
	}
	
	public void testMultipleAccessesToDifferentAttributes() {
		Method main2 = methodNamed("main2");
		Method main3 = methodNamed("main3");
		Attribute field2 = attributeNamed("$field2");
		Attribute field3 = attributeNamed("$field3");
		
		assertEquals(2, main2.getAccesses().size());
		assertEquals(1, main3.getAccesses().size());
		assertEquals(1, field2.getIncomingAccesses().size());
		assertEquals(2, field3.getIncomingAccesses().size());
		
		assertAccess(main2, field2, false);
		assertAccess(main2, field3, false);
		assertAccess(main3, field3, false);
	}
	
	@Test
	public void testSetterAccess()  {
		Method main4 = methodNamed("main4");
		Attribute field4 = attributeNamed("$field4");
		
		assertEquals(1, main4.getAccesses().size());
		assertEquals(1, field4.getIncomingAccesses().size());
		
		assertAccess(main4, field4, true);
	}
	
	@Test
	public void testDistinctAccessAndSetter()  {
		Method main5 = methodNamed("main5");
		Attribute field5 = attributeNamed("$field5");
		
		assertEquals(2, main5.getAccesses().size());
		assertEquals(2, field5.getIncomingAccesses().size());
		
		assertAccess(main5, field5, 2);
		assertAccessMatching(main5, field5, access -> access.getIsWrite());
		assertAccessMatching(main5, field5, access -> access.getIsWrite() == false);
	}
	
	@Test
	public void testExpressionWithAccessingAndSettingAttribute()  {
		Method main6 = methodNamed("main6");
		Attribute field6 = attributeNamed("$field6");
		
		assertEquals(2, main6.getAccesses().size());
		assertEquals(2, field6.getIncomingAccesses().size());
		
		assertAccess(main6, field6, 2);
		assertAccessMatching(main6, field6, access -> access.getIsWrite());
		assertAccessMatching(main6, field6, access -> access.getIsWrite() == false);
	}

}
