<?php




class MethodsWithConstructorInSuperclassCalls {
 	
	public function main1SimpleConstructor() {
		new SubclassWithoutConstructor_ForMethodsWithConstructorInSuperclassCalls();
	}


}


class SubclassWithoutConstructor_ForMethodsWithConstructorInSuperclassCalls extends SuperclassWithoutConstructor_ForMethodsWithConstructorInSuperclassCalls {

} 

class SuperclassWithoutConstructor_ForMethodsWithConstructorInSuperclassCalls {
	public function __construct(int $x, string $y) {
		
	}
} 






