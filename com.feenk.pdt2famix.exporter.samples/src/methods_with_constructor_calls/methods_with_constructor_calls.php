<?php

class ClassNoParametersConstructor_ForMethodsWithConstructorCalls {
	public function __construct() {
		
	}
}

class ClassImplicitConstructor_ForMethodsWithConstructorCalls {
	
}


class MethodsWithConstructorCalls {
 	private $field3;

	public function main1SimpleConstructor() {
		new ClassNoParametersConstructor_ForMethodsWithConstructorCalls();
	}

	public function main21ImplicitConstructorCall() {
		$x = new ClassImplicitConstructor_ForMethodsWithConstructorCalls();
	}
	public function main22ImplicitConstructorCall() {
		$y = new ClassImplicitConstructor_ForMethodsWithConstructorCalls();
	}

	public function main3() {
		$this->field3 = new ClassWithConstructorParameters_ForMethodsWithConstructorCalls(1, "2");
	}

	public function main4() {
		new ClassWithConstructorParameters_ForMethodsWithConstructorCalls();
	}

}

class ClassWithConstructorParameters_ForMethodsWithConstructorCalls {
	public function __construct(int $x, string $y) {
		
	}
}





