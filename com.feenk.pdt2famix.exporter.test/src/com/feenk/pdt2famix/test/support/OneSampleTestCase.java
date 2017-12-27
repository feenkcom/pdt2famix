package com.feenk.pdt2famix.test.support;

import static org.junit.Assert.assertTrue;

import org.junit.Before;

import com.feenk.pdt2famix.exporter.inphp.Importer;
import com.feenk.pdt2famix.exporter.model.famix.Type;

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
	
	public static String removeTestSuffix(String target) {
		String suffix = "Test";
	    if (!target.endsWith(suffix)) {
	        return target;
	    }

	    String prefix = target.substring(0, target.length() - suffix.length());
	    return prefix;
	}
	
	// ASSERTIONS ATTRIBUTES

	protected void assertAttribute(String attributeName, Type declaredType) {
		super.assertAttribute(attributeName, type, declaredType);
	}
	
	protected void assertUnknownTypePresent() {
		assertTrue(importer.namespaces().has(Importer.UNKNOWN_NAME));
	}

	protected void assertUnknownNamespacePresent() {
		assertTrue(importer.types().has(Importer.UNKNOWN_NAME + "." + Importer.UNKNOWN_NAME));
	}
}
