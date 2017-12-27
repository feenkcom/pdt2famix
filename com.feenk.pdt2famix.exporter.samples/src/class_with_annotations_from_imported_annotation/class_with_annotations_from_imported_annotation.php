<?php

namespace annotations_namespace_ForClassWithAnnotationsFromImportedAnnotation {
	/**
 	* @Annotation_ForClassWithAnnotationsFromImportedAnnotation
 	*/
	final class Id_ForClassWithAnnotationsFromImportedAnnotation {
	
	}
}

namespace {
	
	use annotations_namespace_ForClassWithAnnotationsFromImportedAnnotation\Id_ForClassWithAnnotationsFromImportedAnnotation as Id;
    /**
    * This is a class that contains an annotation
    *
    * @Id
    */  
    class ClassWithAnnotationsFromImportedAnnotation {
	
    }

}
