package com.feenk.jdt2famix.injava.multipleSamples;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Ignore;
import org.junit.Test;

import com.feenk.jdt2famix.JavaFiles;
import com.feenk.jdt2famix.injava.InJavaImporter;
import com.feenk.jdt2famix.model.famix.AnnotationInstance;
import com.feenk.jdt2famix.model.famix.AnnotationType;
import com.feenk.jdt2famix.model.famix.Parameter;
import com.feenk.jdt2famix.model.famix.Type;
import com.feenk.jdt2famix.samples.basic.AnnotationTypeWithOneAttributeForAll;
import com.feenk.jdt2famix.samples.basic.AnnotationTypeWithValueAttributeForType;
import com.feenk.jdt2famix.samples.basic.AnnotationTypeWithoutAttributesForAll;
import com.feenk.jdt2famix.samples.basic.ClassWithVariousAnnotations;

public class ClassWithVariousAnnotationsTest extends
		MultipleSamplesTestCase {

	@Override
	protected void sampleClassesIn(JavaFiles javaFiles) {
		javaFiles.oneJavaFile(this.fileNameFor(ClassWithVariousAnnotations.class));
		javaFiles.oneJavaFile(this.fileNameFor(AnnotationTypeWithValueAttributeForType.class));
		javaFiles.oneJavaFile(this.fileNameFor(AnnotationTypeWithoutAttributesForAll.class));
		javaFiles.oneJavaFile(this.fileNameFor(AnnotationTypeWithOneAttributeForAll.class));
	}

	@Test
	public void testTypes() {
		assertEquals(4, importer.types().stream().filter(t -> ! t.getIsStub()).count());
	}
	
	@Test
	public void testAnnotationTypeForTypeSourceAnchor() {
		AnnotationType annotationType = (AnnotationType) importer.types().named(AnnotationTypeWithValueAttributeForType.class.getName());
		assertEquals(1, annotationType.getInstances().size());
		assertNotNull(annotationType.getSourceAnchor());
	}
	
	@Test
	public void testAnnotationInstanceOnType() {
		Type type = importer.types().named(ClassWithVariousAnnotations.class.getName());
		assertEquals(1, type.getAnnotationInstances().size());
	}

	@Ignore
	@Test
	public void testAnnotationInstanceOnTypeSourceAnchor() {
		Type type = importer.types().named(ClassWithVariousAnnotations.class.getName());
		assertNotNull(type.getAnnotationInstances().stream().findAny().get().getSourceAnchor());
	}

	@Test
	public void testAnnotationInstanceOnAttributes() {
		Type type = importer.types().named(ClassWithVariousAnnotations.class.getName());
		type.getAttributes().stream().forEach(a -> assertEquals(1, a.getAnnotationInstances().size()));
	}

	@Test
	public void testAnnotationInstanceOnMethods() {
		Type type = importer.types().named(ClassWithVariousAnnotations.class.getName());
		type.getMethods().stream()
			.filter(m -> !m.getName().equals(InJavaImporter.INITIALIZER_NAME))
			.forEach(m -> assertEquals(1, m.getAnnotationInstances().size()));
	}
	
	@Test
	public void testMethodWithConstantAnnotationValue() {
		AnnotationInstance annotationInstance = methodNamed("methodWithConstantAnnotationValue").getAnnotationInstances().stream().findAny().get();
		assertEquals(1, annotationInstance.getAttributes().size());
		assertEquals("string", annotationInstance.getAttributes().stream().findAny().get().getValue());
	}

	@Test
	public void testMethodWithAnnotationForParameter() {
		Parameter parameter = methodNamed("methodWithAnnotationForParameter").getParameters().stream().findAny().get();
		assertEquals(1, parameter.getAnnotationInstances().size());
		AnnotationInstance annotationInstance = parameter.getAnnotationInstances().stream().findAny().get();
		assertNotNull(annotationInstance);
	}

	@Test
	public void testMethodWithExpressionAnnotationValue() {
		AnnotationInstance annotationInstance = methodNamed("methodWithExpressionAnnotationValue").getAnnotationInstances().stream().findAny().get();
		assertEquals(1, annotationInstance.getAttributes().size());
		assertEquals("methodstring", annotationInstance.getAttributes().stream().findAny().get().getValue());
	}
	
	@Test
	public void testAccessesToAttributeMethodConstant() {
		assertEquals(3, attributeNamed("DEFAULT").getIncomingAccesses().size());
	}

	@Test
	public void testAccessesToTypeConstant() {
		assertEquals(2, attributeNamed("DEFAUlT_FOR_TYPE").getIncomingAccesses().size());
	}
}
