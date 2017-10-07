package com.feenk.pdt2famix;

import static org.junit.Assert.*;
import org.junit.Test;

import com.feenk.pdt2famix.model.famix.JavaSourceLanguage;
import com.feenk.pdt2famix.model.famix.Namespace;

public class InJavaImporterTest {

	@Test
	public void testRepositoryMetaModel() {
		Importer importer = new Importer();
		assertNotNull(importer.repository().getMetamodel().getDescription(Namespace.class));
	}

	@Test
	public void testJavaLanguageInRepository() {
		Importer importer = new Importer();
		assertEquals(1, importer.repository().size());
		assertEquals(JavaSourceLanguage.class, importer.repository().getElements().stream().findAny().get().getClass());
	}

}
