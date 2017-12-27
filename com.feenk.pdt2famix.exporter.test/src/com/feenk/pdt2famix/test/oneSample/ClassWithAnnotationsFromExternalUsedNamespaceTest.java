package com.feenk.pdt2famix.test.oneSample;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.feenk.pdt2famix.exporter.model.famix.AnnotationInstance;
import com.feenk.pdt2famix.exporter.model.famix.AnnotationType;
import com.feenk.pdt2famix.exporter.model.famix.Type;
import com.feenk.pdt2famix.test.support.OneSampleTestCase;

public class ClassWithAnnotationsFromExternalUsedNamespaceTest extends OneSampleTestCase {
	
	protected String sample() {
		return removeTestSuffix(ClassWithAnnotationsFromExternalUsedNamespaceTest.class.getSimpleName());
	}
	
	protected String annotationTagName() {
		return "Annotation_ForClassWithAnnotationsFromExternalUsedNamespace";
	}
	
	protected String defaultAnnotationsNamespaceName() {
		return "";
	}
	
	@Test
	public void testModelSize() {
		assertEquals(3, importer.namespaces().size());
		assertEquals(3, importer.types().size());
		assertEquals(0, importer.methods().size());
		assertEquals(0, importer.attributes().size());
		assertEquals(0, importer.currentInvocations().size());
		assertEquals(0, importer.currentAccesses().size());
		assertEquals(0, importer.currentTraitUsages().size());
	}

	@Test
	public void testAnnotationTypesPresent() {
		assertAnnotationTagType();
		assertAnnotationTypePresent(typeIdentifier(
				"annotations_namespace_ForClassWithAnnotationsFromExternalUsedNamespace",
				"Id_ClassWithAnnotationsFromExternalUsedNamespace"));
	}
	
	@Test
	public void testAnnotationTypes() {
		AnnotationType annotationTagType = annotationTypeNamed(annotationTagTypeName()); 
		AnnotationType annotationId = annotationTypeNamed("Id_ClassWithAnnotationsFromExternalUsedNamespace");
		
		assertEquals(1, annotationId.getAnnotationInstances().size());
		assertInstancesForAnnotationType(annotationTagType, new AnnotationType[] {annotationId});
	}
	
	@Test
	public void testClientClassAnnotations() {		
		AnnotationType annotationId = annotationTypeNamed("Id_ClassWithAnnotationsFromExternalUsedNamespace");
		assertInstancesForAnnotationType(annotationId, new Type[] {type});
		assertAnnotationInstancesForType(type, new AnnotationType[] {annotationId});
		
		AnnotationInstance idInstance = locateAnnotationInstanceInType(type, annotationId);
		assertEquals(0, idInstance.getAttributes().size());
	}

}
