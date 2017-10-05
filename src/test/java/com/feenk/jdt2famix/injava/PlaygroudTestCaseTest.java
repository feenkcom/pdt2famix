package com.feenk.jdt2famix.injava;

import static org.junit.Assert.*;

import org.junit.Test;

import com.feenk.jdt2famix.model.famix.Attribute;

public class PlaygroudTestCaseTest {

	@Test
	public void test() {
		com.feenk.jdt2famix.model.famix.Class class1 = new com.feenk.jdt2famix.model.famix.Class();
		Attribute attribute = new Attribute();
		class1.addAttributes(attribute);
		assertTrue(attribute.getParentType() == class1);
	}

}
