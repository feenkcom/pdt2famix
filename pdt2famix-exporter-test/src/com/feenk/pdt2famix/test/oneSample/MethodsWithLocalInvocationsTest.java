package com.feenk.pdt2famix.test.oneSample;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.Test;

import com.feenk.pdt2famix.model.famix.BehaviouralEntity;
import com.feenk.pdt2famix.model.famix.Invocation;
import com.feenk.pdt2famix.model.famix.Method;
import com.feenk.pdt2famix.model.famix.NamedEntity;
import com.feenk.pdt2famix.test.support.OneSampleTestCase;

public class MethodsWithLocalInvocationsTest extends OneSampleTestCase {

	protected String sample() {
		return removeTestSuffix(MethodsWithLocalInvocationsTest.class.getSimpleName());
	}
	
	@Test
	public void testModelSize() {
		assertEquals(1, importer.namespaces().size());
		assertEquals(1, importer.types().size());
		assertEquals(7, importer.methods().size());
		assertEquals(7, importer.currentInvocations().size());
		assertEquals(0, importer.attributes().size());
	}
	
	@Test
	public void testInvocationsCount() {
		Method main1 = methodNamed("main1");
		Method main2 = methodNamed("main2");
		Method test1 = methodNamed("test1");
		Method test2 = methodNamed("test2");
		Method test21 = methodNamed("test21");
		Method test3 = methodNamed("test3");
		
		assertEquals(0, main1.getIncomingInvocations().size());
		assertEquals(2, main1.getOutgoingInvocations().size());
		assertEquals(0, main2.getIncomingInvocations().size());
		assertEquals(3, main2.getOutgoingInvocations().size());
		
		assertEquals(2, test1.getIncomingInvocations().size());
		assertEquals(0, test1.getOutgoingInvocations().size());
		assertEquals(1, test2.getIncomingInvocations().size());
		assertEquals(1, test2.getOutgoingInvocations().size());
		assertEquals(1, test21.getIncomingInvocations().size());
		assertEquals(0, test21.getOutgoingInvocations().size());	
		assertEquals(2, test3.getIncomingInvocations().size());
		assertEquals(0, test3.getOutgoingInvocations().size());	
	}
	
	@Test
	public void testInvocationsRecursion() {
		Method testRecursion = methodNamed("testRecursion");
		assertEquals(1, testRecursion.getIncomingInvocations().size());
		assertEquals(1, testRecursion.getOutgoingInvocations().size());
		
		Invocation incomingInvocation = testRecursion.getIncomingInvocations().stream().findFirst().get();
		Invocation outgoingInvocation = testRecursion.getOutgoingInvocations().stream().findFirst().get();
		assertEquals(incomingInvocation, outgoingInvocation);		
		assertInvocationProperties(incomingInvocation, testRecursion, testRecursion, null);
	}
	
	
	@Test 
	public void testMain2Invocations() {
		Method main2 = methodNamed("main2");
		Method test1 = methodNamed("test1");
		Method test3 = methodNamed("test3");
		
		assertInvocationsBetweenMethods(main2, test1, 1, null);
		assertInvocationsBetweenMethods(main2, test3, 2, null);
	}

	private void assertInvocationsBetweenMethods(BehaviouralEntity sender, BehaviouralEntity candidate, int numberOfInvocations, NamedEntity receiver) {
		List<Invocation> outgoingInvocations =  sender.getOutgoingInvocations().stream()
			.filter(invocation -> invocation.getCandidates().contains(candidate))
			.collect(Collectors.toList());
		assertEquals(numberOfInvocations, outgoingInvocations.size());
		List<Invocation> incomingInvocations = candidate.getIncomingInvocations().stream()
			.filter(invocation -> invocation.getSender().equals(sender))
			.collect(Collectors.toList());
		assertEquals(new HashSet<>(Arrays.asList(outgoingInvocations)), new HashSet<>(Arrays.asList(incomingInvocations)));
		outgoingInvocations.stream().forEach(
			invocation -> assertInvocationProperties(invocation, sender, candidate, receiver));
	}

	private void assertInvocationProperties(Invocation invocation, BehaviouralEntity sender, BehaviouralEntity candidate, NamedEntity receiver) {
		assertInvocationProperties(invocation, sender, new BehaviouralEntity[] {candidate}, receiver);	
	}

	private void assertInvocationProperties(Invocation invocation, BehaviouralEntity sender, BehaviouralEntity[] candidates, NamedEntity receiver) {
		invocation.getSender();
		assertEquals(candidates.length, invocation.getCandidates().size());
		assertEquals(receiver, invocation.getReceiver());
		assertEquals(new HashSet<>(Arrays.asList(candidates)), new HashSet<>(invocation.getCandidates()));
	}
	
}
