package com.feenk.pdt2famix.test.oneSample;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.Test;

import com.feenk.pdt2famix.exporter.model.famix.Attribute;
import com.feenk.pdt2famix.exporter.model.famix.Method;
import com.feenk.pdt2famix.test.support.OneSampleTestCase;

public class ClassWithConstAccessesBeforeDefinitionTest extends OneSampleTestCase {

	protected String sample() {
		return removeTestSuffix(ClassWithConstAccessesBeforeDefinitionTest.class.getSimpleName());
	}

	@Test
	public void testModelSize() {
		assertEquals(1, importer.namespaces().size());
		assertEquals(1, importer.types().size());
		assertEquals(1, importer.methods().size());
		assertEquals(3, importer.attributes().size());
		assertEquals(0, importer.currentInvocations().size());
		assertEquals(3, importer.currentAccesses().size()); 
		assertEquals(0, importer.parameters().size());
	}
		
	@Test
	public void testClassConstants() {		
		assertAttribute("CONST1", type, numberType(), true, new String[] {"const", "public"});
		assertAttribute("CONST2", type, numberType(), true, new String[] {"const", "protected"});
		assertAttribute("CONST3", type, numberType(), true, new String[] {"const", "private"});
	}

	@Test
	public void testConstAccesses()  {
		Method main1 = methodNamed("main1");
		Attribute const1 = attributeNamed("CONST1");
		Attribute const2 = attributeNamed("CONST2");
		Attribute const3 = attributeNamed("CONST3");
		
		assertEquals(3, main1.getAccesses().size());
		assertEquals(1, const1.getIncomingAccesses().size());
		assertEquals(1, const2.getIncomingAccesses().size());
		assertEquals(1, const3.getIncomingAccesses().size());
		
		assertAccess(main1, const1, 1, Optional.of(false));
		assertAccess(main1, const2, 1, Optional.of(false));
		assertAccess(main1, const3, 1, Optional.of(false));
	}

}
