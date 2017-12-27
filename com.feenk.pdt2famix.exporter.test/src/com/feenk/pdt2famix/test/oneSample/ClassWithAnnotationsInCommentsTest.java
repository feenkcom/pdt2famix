package com.feenk.pdt2famix.test.oneSample;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.feenk.pdt2famix.exporter.model.famix.AnnotationInstance;
import com.feenk.pdt2famix.exporter.model.famix.AnnotationType;
import com.feenk.pdt2famix.exporter.model.famix.Type;
import com.feenk.pdt2famix.test.support.OneSampleTestCase;

public class ClassWithAnnotationsInCommentsTest extends OneSampleTestCase {
	
	protected String sample() {
		return removeTestSuffix(ClassWithAnnotationsInCommentsTest.class.getSimpleName());
	}
	
	protected String annotationTagName() {
		return "Annotation_ForClassWithAnnotationsInComments";
	}
	
	protected String defaultAnnotationsNamespaceName() {
		return ""; // locate annotations in the default namespace
	}
	
	@Test
	public void testModelSize() {
		assertEquals(2, importer.namespaces().size());
		assertEquals(6, importer.types().size());
		assertEquals(0, importer.methods().size());
		assertEquals(3, importer.attributes().size());
		assertEquals(0, importer.currentInvocations().size());
		assertEquals(0, importer.currentAccesses().size());
		assertEquals(0, importer.currentTraitUsages().size());
	}

	@Test
	public void testAnnotationTypesPresent() {
		assertAnnotationTagType();
		assertAnnotationTypePresent(typeIdentifier("Id_ForClassWithAnnotationsInComments"));
		assertAnnotationTypePresent(typeIdentifier("Entity_ForClassWithAnnotationsInComments"));
	}
	
	@Test
	public void testAnnotationTypes() {
		AnnotationType annotationTagType = annotationTypeNamed(annotationTagTypeName()); 
		AnnotationType annotationId = annotationTypeNamed("Id_ForClassWithAnnotationsInComments");
		AnnotationType annotationEntity = annotationTypeNamed("Entity_ForClassWithAnnotationsInComments");
		
		assertEquals(1, annotationId.getAnnotationInstances().size());
		assertEquals(1, annotationEntity.getAnnotationInstances().size());
		assertInstancesForAnnotationType(annotationTagType, new AnnotationType[] {annotationId, annotationEntity});
	}

	@Test
	public void testClientClassAnnotations() {
		Type class1 = type;
		Type class2 = typeNamed("ClassWithAnnotationsInComments_CurrentNamespace");
		
		AnnotationType annotationId = annotationTypeNamed("Id_ForClassWithAnnotationsInComments");
		AnnotationType annotationEntity = annotationTypeNamed("Entity_ForClassWithAnnotationsInComments");
		
		assertInstancesForAnnotationType(annotationId, new Type[] {class1, class2});
		assertInstancesForAnnotationType(annotationEntity, new Type[] {class1, class2});

		assertAnnotationInstancesForType(class1, new AnnotationType[] {annotationId, annotationEntity});
		assertAnnotationInstancesForType(class2, new AnnotationType[] {annotationId, annotationEntity});
	}
	
	@Test
	public void testAnnotationInstancesInClient() {
		Type targetClass = type;
		AnnotationType annotationId = annotationTypeNamed("Id_ForClassWithAnnotationsInComments");
		AnnotationType annotationEntity = annotationTypeNamed("Entity_ForClassWithAnnotationsInComments");
		
		AnnotationInstance idInstance = locateAnnotationInstanceInType(targetClass, annotationId);
		AnnotationInstance entityInstance = locateAnnotationInstanceInType(targetClass, annotationEntity);
		assertEquals(0, idInstance.getAttributes().size());
		assertEquals(3, entityInstance.getAttributes().size());
	}
}
