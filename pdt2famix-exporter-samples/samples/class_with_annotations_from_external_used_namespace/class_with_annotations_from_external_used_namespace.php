<?php

namespace annotations_namespace_ForClassWithAnnotationsFromExternalUsedNamespace {
	/**
 	* @Annotation_ForClassWithAnnotationsFromExternalUsedNamespace
 	*/
	final class Id_ClassWithAnnotationsFromExternalUsedNamespace {
	
	}
}

namespace {
	use annotations_namespace_ForClassWithAnnotationsFromExternalUsedNamespace as AnnotationsProvider;
    /**
    * This is a class that contains an annotation
    *
    * @AnnotationsProvider\Id_ClassWithAnnotationsFromExternalUsedNamespace
    */  
    class ClassWithAnnotationsFromExternalUsedNamespace {
	
    }

}
