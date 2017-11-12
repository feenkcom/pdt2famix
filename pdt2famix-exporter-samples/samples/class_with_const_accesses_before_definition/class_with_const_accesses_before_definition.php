<?php

class ClassWithConstAccessesBeforeDefinition {
		
	public function main1() {
		self::CONST1;
		self::CONST2;
		ClassWithConstAccessesBeforeDefinition::CONST3;
	}
	
	const CONST1 = 1;
	protected const CONST2 = 1;
	private const CONST3 = 1;
	
}