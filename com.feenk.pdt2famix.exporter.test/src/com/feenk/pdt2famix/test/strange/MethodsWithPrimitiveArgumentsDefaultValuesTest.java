package com.feenk.pdt2famix.test.strange;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.feenk.pdt2famix.model.famix.Method;
import com.feenk.pdt2famix.test.support.OneSampleTestCase;

public class MethodsWithPrimitiveArgumentsDefaultValuesTest extends OneSampleTestCase {

	protected String sample() {
		return removeTestSuffix(MethodsWithPrimitiveArgumentsDefaultValuesTest.class.getSimpleName());
	}

	@Test
	public void testModelSize() {
		assertEquals(2 + 1, importer.namespaces().size());
		assertEquals(3 + 1 + 1, importer.types().size()); // Accound for undefined and string
		assertEquals(2, importer.methods().size());
		assertEquals(0, importer.currentInvocations().size());
		assertEquals(0, importer.currentAccesses().size());
		assertEquals(0, importer.attributes().size());
		assertEquals(8, importer.parameters().size());
	}
		
	/**
	 * A parameter declaration like `$b = true` will result in an AST where
	 * `true` is resolved like <Scalar start='195' length='4' type='string' value='true'/>
	 * Hence, the type of the parameter is seen as string. Resolving just the type of
	 * the default value will lead to a boolean type.
	 */
	@Test
	public void assertBooleanParameters() {
		Method method = methodNamed("methodWithBooleanArguments");
		
		assertMethodParameter("$a", method, booleanType());
		assertMethodParameter("$b", method, stringType()); // Strange
		assertMethodParameter("$c", method, stringType()); // Strange
		assertMethodParameter("$d", method, null); 
		assertMethodParameter("$e", method, booleanType());
	}
	
	/**
	 * A parameter declaration like `$a = null` will result in an AST where
	 * `null` is resolved like <Scalar start='195' length='4' type='string' value='true'/>
	 * Hence, the type of the parameter is seen as string. Resolving just the type of
	 * the default value will lead to a null type.
	 */
	@Test
	public void assertUndefinedParameters() {
		Method method = methodNamed("methodWithNull");
		
		assertMethodParameter("$a", method, stringType()); // Strange
		assertMethodParameter("$b", method, null);
		assertMethodParameter("$c", method, null);
	}

}
