package com.feenk.jdt2famix.samples.basic;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target( { ElementType.TYPE})
public @interface AnnotationTypeWithValueAttributeForType {
	String value() default "";
	public static final String DEFAUlT_FOR_TYPE = "default"; 
}
