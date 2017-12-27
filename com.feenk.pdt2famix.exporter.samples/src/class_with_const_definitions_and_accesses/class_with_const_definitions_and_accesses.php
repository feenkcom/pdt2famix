<?php

class ClassWithConstDefinitionsAndAccesses {
	
	const CONST_INT1 = 1;
	public const CONST_BOOL1 = true;
	
	private const CONST_STRING1 = 'Hello';
	protected const CONST_STRING2 = CONST_STRING1.'Hello';

	const ANIMALS = array('dog', 'cat', 'bird');
	
	const CONST_OBJECT = new ClassWithConstDefinitionsAndAccesses();
	
	public function main1DirectConstAccess() {
		$a = CONST_INT1;
	}
	
	public function main2SelfConstAccess() {
		$b = self::CONST_BOOL1;
	}

	public function main3ClassNameConstAccess() {
		$b = ClassWithConstDefinitionsAndAccesses::CONST_OBJECT;
	}
	
	public static function main4ReturnConstantStatic(){
	    return self::CONST_STRING1;
	}
	
}