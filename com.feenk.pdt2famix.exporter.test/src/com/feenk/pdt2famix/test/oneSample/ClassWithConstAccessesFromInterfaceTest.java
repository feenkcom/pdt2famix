package com.feenk.pdt2famix.test.oneSample;

import static org.junit.Assert.assertEquals;

import java.util.Optional;

import org.junit.Test;

import com.feenk.pdt2famix.exporter.model.famix.Attribute;
import com.feenk.pdt2famix.exporter.model.famix.Method;
import com.feenk.pdt2famix.test.support.OneSampleTestCase;

public class ClassWithConstAccessesFromInterfaceTest  extends OneSampleTestCase {

	protected String sample() {
		return removeTestSuffix(ClassWithConstAccessesFromInterfaceTest.class.getSimpleName());
	}

	@Test
	public void testModelSize() {
		assertEquals(2, importer.namespaces().size());
		assertEquals(4, importer.types().size());
		assertEquals(2, importer.methods().size());
		assertEquals(2, importer.attributes().size());
		assertEquals(0, importer.currentInvocations().size());
		assertEquals(2, importer.currentAccesses().size()); 
		assertEquals(0, importer.parameters().size());
	}
		
	@Test
	public void testClassConstants() {		
		assertAttribute("CONST1_INTERFACE", typeNamed("InterfaceAFor_ClassWithConstAccessesFromInterface"), numberType(), true, new String[] {"const", "public"});
		assertAttribute("CONST1_INTERFACE", typeNamed("InterfaceBFor_ClassWithConstAccessesFromInterface"), numberType(), true, new String[] {"const", "public"});
	}
	
	@Test
	public void testConstInterfaceInternalAccess()  {
		Method main1 = methodNamed("main1InternalInterfaceConstantAccess");
		Attribute const1 = attributeInType(typeNamed("InterfaceAFor_ClassWithConstAccessesFromInterface"), "CONST1_INTERFACE");
		
		assertEquals(1, main1.getAccesses().size());
		assertEquals(1, const1.getIncomingAccesses().size());
		
		assertAccess(main1, const1, 1, Optional.of(false));
	}
	
	@Test
	public void testConstInterfaceExternalAccess()  {
		Method main1 = methodNamed("main2ExternalInterfaceConstantAccess");
		Attribute const1 = attributeInType(typeNamed("InterfaceBFor_ClassWithConstAccessesFromInterface"), "CONST1_INTERFACE");
		
		assertEquals(1, main1.getAccesses().size());
		assertEquals(1, const1.getIncomingAccesses().size());
		
		assertAccess(main1, const1, 1, Optional.of(false));
	}

}
