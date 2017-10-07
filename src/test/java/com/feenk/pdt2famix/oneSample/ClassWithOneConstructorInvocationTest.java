package com.feenk.jdt2famix.injava.oneSample;

import static org.junit.Assert.*;

import org.junit.Test;

import com.feenk.jdt2famix.model.famix.Method;
import com.feenk.jdt2famix.samples.basic.ClassWithOneConstructorInvocation;

public class ClassWithOneConstructorInvocationTest extends OneSampleTestCase {

	@Override
	protected Class<?> sampleClass() {
		return ClassWithOneConstructorInvocation.class;
	}

	@Test
	public void testConstructorInvocation() {
		Method defaultConstructor = type.getMethods().stream().filter(m -> m.getParameters().isEmpty()).findAny().get();
		assertEquals(1, defaultConstructor.getOutgoingInvocations().size());
		assertEquals("this(\"test\");", defaultConstructor.getOutgoingInvocations().stream().findAny().get().getSignature());
	}
	
}
