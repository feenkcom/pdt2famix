<?php

class ClassForTraitAttributeOne {}

trait TraitWithPrimitiveAndTypeAttributes {
	
	public $varUndefinedT;
	public $varNullT = NULL;
	public $varIntT = 1;
	public $varBoolT = true;
	public $varStringT = "abc";
	public $varArrayIntT = array(1, 2);
	
	public $varTypeOne = new ClassForTraitAttributeOne();
	public $varTypeTwo = new ClassForTraitAttributeTwo();
}


class ClassForTraitAttributeTwo {}