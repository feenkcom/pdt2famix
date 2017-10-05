package com.feenk.jdt2famix.samples.basic;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.FIELD, ElementType.METHOD, ElementType.TYPE, ElementType.PARAMETER })
public @interface AnnotationTypeWithOneAttributeForAll {
	public static final String DEFAULT = "string";
	
	public abstract String stringAnnotationAttribute();
}
