<?php

/**
 * @Annotation_ForClassWithAnnotationsInComments
 */
final class Id_ForClassWithAnnotationsInComments {
	
}
    
/**
 * @Annotation_ForClassWithAnnotationsInComments
 */
final class Entity_ForClassWithAnnotationsInComments {
	
	/**
	 * @var string
 	*/
	public $name;

	/**
	 * @var boolean
	 */
    public $readOnly = false;

    /**
     * @var integer
     */
    public $length;
}

/**
 * This is a class that contains some annotations accessed using the namespace.
 *
 * @\Id_ForClassWithAnnotationsInComments
 * @\Entity_ForClassWithAnnotationsInComments(name="EntityClass",readOnly=true, length=256)
 */  
class ClassWithAnnotationsInComments {
	
}

/**
 * This is a class that contains some annotations that are in the current namespace.
 *
 * @Id_ForClassWithAnnotationsInComments
 * @Entity_ForClassWithAnnotationsInComments(name="AnotherEntityClass", readOnly=false, length=255)
 */  
class ClassWithAnnotationsInComments_CurrentNamespace {
	
}