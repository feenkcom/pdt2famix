package com.feenk.pdt2famix.test.support;

import org.junit.Before;

import com.feenk.pdt2famix.model.famix.Type;

public abstract class OneSampleTestCase extends InPhpTestCase {
	protected Type type;
	
	@Before
	public void setUp() throws Exception {
		super.setUp();
		type = importer.types().named(sampleIdentifier());
	}
	
	@Override
	protected String sampleDirectory() {
		return toSnakeCase(importer.entityBasenameFrom(sample()));
	}
	
	protected abstract String sample();
	
	private String toSnakeCase(String camelCase) {
		return camelCase.replaceAll("(.)(\\p{Upper})", "$1_$2").toLowerCase();
	}
	
	private String sampleIdentifier() {
		return typeIdentifier(sample());
	}
}
