<?php

namespace annotations_namespace_ForClassWithAnnotationsFromExternalDefaultNamespace {
	/**
 	* @Annotation_ForClassWithAnnotationsFromExternalDefaultNamespace
 	*/
	final class Id_ClassWithAnnotationsFromExternalDefaultNamespace {
	
	}
}

namespace {
    /**
    * This is a class that contains an annotation
    *
    * @Id_ClassWithAnnotationsFromExternalDefaultNamespace
    */  
    class ClassWithAnnotationsFromExternalDefaultNamespace {
	
    }

}
