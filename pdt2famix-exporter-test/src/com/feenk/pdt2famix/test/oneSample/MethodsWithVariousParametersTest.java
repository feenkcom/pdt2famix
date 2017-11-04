package com.feenk.pdt2famix.test.oneSample;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import com.feenk.pdt2famix.model.famix.BehaviouralEntity;
import com.feenk.pdt2famix.model.famix.Method;
import com.feenk.pdt2famix.model.famix.Parameter;
import com.feenk.pdt2famix.model.famix.Type;
import com.feenk.pdt2famix.test.support.OneSampleTestCase;

public class MethodsWithVariousParametersTest extends OneSampleTestCase {

	protected String sample() {
		return removeTestSuffix(MethodsWithVariousParametersTest.class.getSimpleName());
	}

	@Test
	public void testModelSize() {
		assertEquals(2, importer.namespaces().size());
		assertEquals(7, importer.types().size());
		assertEquals(9, importer.methods().size());
		assertEquals(0, importer.currentInvocations().size());
		assertEquals(0, importer.currentAccesses().size());
		assertEquals(0, importer.attributes().size());
	}
	
	@Test
	public void testMethodWithNoParameters() {
		Method method = methodNamed("methodWithNoParameters");
		
		assertEquals(0, method.getParameters().size());
	}
	
	@Test
	public void testMethodWithGenericParameters() {
		Method method = methodNamed("methodWithGenericParameters");
		
		assertEquals(2, method.getParameters().size());
		
		assertMethodParameter("$a", method, null);
		assertMethodParameter("$b", method, null);
	}

	@Test
	public void testMethodWithPrimitiveDeclaredParameterTypes() {
		Method method = methodNamed("methodWithPrimitiveDeclaredParameterTypes");
		
		assertEquals(2, method.getParameters().size());
		
		assertMethodParameter("$a", method, numberType());
		assertMethodParameter("$b", method, booleanType());
	}
	
	@Test
	public void testMethodWithPrimitiveParametersDefaultValue() {
		Method method = methodNamed("methodWithPrimitiveParametersDefaultValue");
		
		assertEquals(2, method.getParameters().size());
		
		assertMethodParameter("$a", method, numberType());
		assertMethodParameter("$b", method, booleanType());
	}
	
	@Test
	public void testMethodWithStandardObjectsAsParameters() {
		Method method = methodNamed("methodWithStandardObjectsAsParameters");
		
		assertEquals(2, method.getParameters().size());
		
		assertMethodParameter("$date1", method, typeNamed("DateTime"));
		assertMethodParameter("$date2", method, typeNamed("DateTime"));
	}
	
	@Test
	public void testMethodWithSelfParameterTypes() {
		Method method = methodNamed("methodWithSelfParameterTypes");
		
		assertEquals(1, method.getParameters().size());
		
		assertMethodParameter("$param1", method, type);
	}
	
	@Test
	public void testMethodWithDefaultSelfParameterTypes() {
		Method method = methodNamed("methodWithDefaultSelfParameterTypes");
		
		assertEquals(2, method.getParameters().size());
		
		assertMethodParameter("$param1", method, type);
		assertMethodParameter("$param2", method, type);
	}
	
	@Test
	public void testMethodWithCustomParameterTypes() {
		Method method = methodNamed("methodWithCustomParameterTypes");
		
		assertEquals(2, method.getParameters().size());
		
		assertMethodParameter("$param1", method, typeNamed("AClassForMethodParametersUsage"));
		assertMethodParameter("$param2", method, typeNamed("AnInterfaceForMethodParametersUsage"));
	}
	
	@Test
	public void testMethodWithPolymorphicParameterType() {
		Method method = methodNamed("methodWithPolymorphicParameterType");
		
		assertEquals(1, method.getParameters().size());
		
		assertMethodParameter("$param", method, typeNamed("AnInterfaceForMethodParametersUsage"));
	}
	
	private void assertMethodParameter(String parameterName, BehaviouralEntity parentBehaviour, Type declaredType) {
		List<Parameter> possibleParameters = parentBehaviour.getParameters().stream()
				.filter( aParameter -> aParameter.getName().equals(parameterName))
				.collect(Collectors.toList());
		assertEquals(1, possibleParameters.size());
		
		Parameter targetParameter = possibleParameters.get(0);
		assertEquals(parentBehaviour, targetParameter.getParentBehaviouralEntity());
		assertEquals(declaredType, targetParameter.getDeclaredType());
	}

}
