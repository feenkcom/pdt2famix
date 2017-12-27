package com.feenk.pdt2famix.test.oneSample;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

import com.feenk.pdt2famix.Importer;
import com.feenk.pdt2famix.model.famix.Method;
import com.feenk.pdt2famix.test.support.OneSampleTestCase;

public class MethodsWithConstructorCallsTest extends OneSampleTestCase {

	protected String sample() {
		return removeTestSuffix(MethodsWithConstructorCallsTest.class.getSimpleName());
	}

	@Test
	public void testModelSize() {
		assertEquals(2, importer.namespaces().size());
		assertEquals(6, importer.types().size());
		assertEquals(8, importer.methods().size());
		assertEquals(5, importer.currentInvocations().size());
		assertEquals(1, importer.currentAccesses().size());
		assertEquals(1, importer.attributes().size());
		assertEquals(2, importer.parameters().size());
	}

	@Test
	public void testMain1SimpleConstructorInvocations() {
		Method main = methodNamed("main1SimpleConstructor");
		Method constructor = methodInType(typeNamed("ClassNoParametersConstructor_ForMethodsWithConstructorCalls"), Importer.CONSTRUCTOR_NAME);
		
		assertEquals(0, main.getIncomingInvocations().size());
		assertEquals(1, main.getOutgoingInvocations().size());
		assertEquals(1, constructor.getIncomingInvocations().size());
		assertEquals(0, constructor.getOutgoingInvocations().size());
		
		assertInvocationsBetweenMethods(main, constructor, 1, null);
	}
	
	@Test
	public void testMain2ImplicitConstructorCall() {
		Method main1 = methodNamed("main21ImplicitConstructorCall");
		Method main2 = methodNamed("main22ImplicitConstructorCall");
		Method constructor = methodInType(typeNamed("ClassImplicitConstructor_ForMethodsWithConstructorCalls"), Importer.CONSTRUCTOR_NAME);
		
		assertEquals(0, main1.getIncomingInvocations().size());
		assertEquals(1, main1.getOutgoingInvocations().size());
		assertEquals(0, main2.getIncomingInvocations().size());
		assertEquals(1, main2.getOutgoingInvocations().size());
		assertEquals(2, constructor.getIncomingInvocations().size());
		assertEquals(0, constructor.getOutgoingInvocations().size());
		
		assertInvocationsBetweenMethods(main1, constructor, 1, null);
		assertInvocationsBetweenMethods(main2, constructor, 1, null);
		
		assertImplicitConstructor(constructor);
		assertEquals(typeNamed("ClassImplicitConstructor_ForMethodsWithConstructorCalls"), constructor.getParentType());
		
		assertEquals(
				"new ClassImplicitConstructor_ForMethodsWithConstructorCalls()", 
				main1.getOutgoingInvocations().stream().findFirst().get().getSignature());
		assertEquals(
				"new ClassImplicitConstructor_ForMethodsWithConstructorCalls()", 
				main2.getOutgoingInvocations().stream().findFirst().get().getSignature());
	}
	
	@Test
	public void testMain3ConstructorWithParameters() {
		Method main3 = methodNamed("main3");
		Method main4 = methodNamed("main4");
		Method constructor = methodInType(typeNamed("ClassWithConstructorParameters_ForMethodsWithConstructorCalls"), Importer.CONSTRUCTOR_NAME);
		
		assertEquals(0, main3.getIncomingInvocations().size());
		assertEquals(1, main3.getOutgoingInvocations().size());
		assertEquals(0, main4.getIncomingInvocations().size());
		assertEquals(1, main4.getOutgoingInvocations().size());
		assertEquals(2, constructor.getIncomingInvocations().size());
		assertEquals(0, constructor.getOutgoingInvocations().size());
		
		assertInvocationsBetweenMethods(main3, constructor, 1, null);
		assertInvocationsBetweenMethods(main4, constructor, 1, null);
		
		assertEquals(
				"new ClassWithConstructorParameters_ForMethodsWithConstructorCalls(1, \"2\")", 
				main3.getOutgoingInvocations().stream().findFirst().get().getSignature());
		assertEquals(
				"new ClassWithConstructorParameters_ForMethodsWithConstructorCalls()", 
				main4.getOutgoingInvocations().stream().findFirst().get().getSignature());
	}
	
	public void assertImplicitConstructor(Method constructor) {		
		assertEquals(null, constructor.getHasClassScope());
		assertEquals(true, constructor.getIsStub());
		assertEquals(Importer.CONSTRUCTOR_KIND, constructor.getKind());
		assertEquals(new HashSet<>(Arrays.asList(new String[] {"public"})), constructor.getModifiers());
		assertEquals(Importer.CONSTRUCTOR_NAME+"()", constructor.getSignature());
	}
}
