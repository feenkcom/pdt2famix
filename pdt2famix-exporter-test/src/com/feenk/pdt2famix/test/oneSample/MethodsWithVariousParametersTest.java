package com.feenk.pdt2famix.test.oneSample;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.StringJoiner;

import org.junit.Test;

import com.feenk.pdt2famix.Importer;
import com.feenk.pdt2famix.model.famix.Method;
import com.feenk.pdt2famix.test.support.OneSampleTestCase;

public class MethodsWithVariousParametersTest extends OneSampleTestCase {

	protected String sample() {
		return removeTestSuffix(MethodsWithVariousParametersTest.class.getSimpleName());
	}

	@Test
	public void testModelSize() {
		assertEquals(2 + 1, importer.namespaces().size()); // Account for generated unknown namespace
		assertEquals(8 + 1, importer.types().size());      // Account for generated unknown type
		assertEquals(9+3, importer.methods().size());
		assertEquals(4, importer.currentInvocations().size());
		assertEquals(0, importer.currentAccesses().size());
		assertEquals(0, importer.attributes().size());
		assertEquals(14, importer.parameters().size());
	}
	
	@Test
	public void testUnknownEntities() {
		assertUnknownNamespacePresent();
		assertUnknownTypePresent();
	}
	
	@Test
	public void testMethodWithNoParameters() {
		String methodName = "methodWithNoParameters";
		Method method = methodNamed(methodName);
		
		assertEquals(0, method.getParameters().size());
		assertMethodSignature(method, methodName+"()");
	}
	
	private void assertMethodSignature(Method method, String expectedSignature) {
		assertEquals(expectedSignature, method.getSignature());
	}
	
	private String signatureFromParameterTypes(String methodName, String ...parameterTypes) {
		StringJoiner signatureJoiner = new StringJoiner(", ", "(", ")");
		Arrays.asList(parameterTypes).stream().forEach(
			parameterType -> signatureJoiner.add(parameterType));
		return methodName+signatureJoiner.toString();
	}
	
	private String primitiveTypeSignature(String primitiveTypeName) {
		return importer.systemNamespace().getName()+"."+primitiveTypeName;
	}
	
	private String defaultTypeSignature(String typeName) {
		return typeName;
	}

	@Test
	public void testMethodWithGenericParameters() {
		String methodName = "methodWithGenericParameters";
		Method method = methodNamed(methodName);
		
		assertEquals(2, method.getParameters().size());
		
		assertMethodParameter("$a", method, null);
		assertMethodParameter("$b", method, null);
		assertMethodSignature(method, signatureFromParameterTypes(methodName, Importer.UNKNOWN_NAME, Importer.UNKNOWN_NAME));
	}

	@Test
	public void testMethodWithPrimitiveDeclaredParameterTypes() {
		String methodName = "methodWithPrimitiveDeclaredParameterTypes";
		Method method = methodNamed(methodName);
		
		assertEquals(2, method.getParameters().size());

		assertMethodParameter("$a", method, numberType());
		assertMethodParameter("$b", method, booleanType());
		assertMethodSignature(method, signatureFromParameterTypes(
				methodName, 
				primitiveTypeSignature("number"), 
				primitiveTypeSignature("boolean")));
	}

	@Test
	public void testMethodWithPrimitiveParametersDefaultValue() {
		String methodName = "methodWithPrimitiveParametersDefaultValue";
		Method method = methodNamed(methodName);
		
		assertEquals(2, method.getParameters().size());
		
		assertMethodParameter("$a", method, numberType());
		assertMethodParameter("$b", method, stringType());
		assertMethodSignature(method, signatureFromParameterTypes(
				methodName, 
				primitiveTypeSignature("number"), 
				primitiveTypeSignature("string")));
	}
	
	@Test
	public void testMethodWithStandardObjectsAsParameters() {
		String methodName = "methodWithStandardObjectsAsParameters";
		Method method = methodNamed(methodName);
		
		assertEquals(2, method.getParameters().size());
		
		assertMethodParameter("$date1", method, typeNamed("DateTime"));
		assertMethodParameter("$date2", method, typeNamed("DateTime"));
		assertMethodSignature(method, signatureFromParameterTypes(
				methodName, 
				defaultTypeSignature("DateTime"), 
				defaultTypeSignature("DateTime")));
	}
	
	@Test
	public void testMethodWithSelfParameterTypes() {
		String methodName = "methodWithSelfParameterTypes";
		Method method = methodNamed(methodName);
		
		assertEquals(1, method.getParameters().size());
		
		assertMethodParameter("$param1", method, type);
		assertMethodSignature(method, signatureFromParameterTypes(
				methodName, 
				defaultTypeSignature("MethodsWithVariousParameters")));
	}
	
	@Test
	public void testMethodWithDefaultSelfParameterTypes() {
		String methodName = "methodWithDefaultSelfParameterTypes";
		Method method = methodNamed(methodName);
		
		
		assertEquals(2, method.getParameters().size());
		
		assertMethodParameter("$param1", method, type);
		assertMethodParameter("$param2", method, type);
		assertMethodSignature(method, signatureFromParameterTypes(
				methodName, 
				defaultTypeSignature("MethodsWithVariousParameters"),
				defaultTypeSignature("MethodsWithVariousParameters")));
	}
	
	@Test
	public void testMethodWithCustomParameterTypes() {
		String methodName = "methodWithCustomParameterTypes";
		Method method = methodNamed(methodName);
		
		assertEquals(2, method.getParameters().size());
		
		assertMethodParameter("$param1", method, typeNamed("AClassForMethodParametersUsage"));
		assertMethodParameter("$param2", method, typeNamed("AnInterfaceForMethodParametersUsage"));
		assertMethodSignature(method, signatureFromParameterTypes(
				methodName, 
				defaultTypeSignature("AClassForMethodParametersUsage"),
				defaultTypeSignature("AnInterfaceForMethodParametersUsage")));
	}
	
	@Test
	public void testMethodWithPolymorphicParameterType() {
		String methodName = "methodWithPolymorphicParameterType";
		Method method = methodNamed(methodName);
		
		assertEquals(1, method.getParameters().size());
		
		assertMethodParameter("$param", method, typeNamed("AnInterfaceForMethodParametersUsage"));
		assertMethodSignature(method, signatureFromParameterTypes(
				methodName, 
				defaultTypeSignature("AnInterfaceForMethodParametersUsage")));
	}
}
