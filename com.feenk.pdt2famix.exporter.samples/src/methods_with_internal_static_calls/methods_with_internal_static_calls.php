<?php

class MethodsWithInternalStaticCalls {
 
	public static function main1() {
		self::helper1();
	}
	
	private static function helper1() {
		
	}
	
	public static function main2() {
		MethodsWithInternalStaticCalls::helper2();
	}
	
	private static function helper2() {
		
	}
	
	public static function main3() {
		static::helper3();
	}
	
	private static function helper3() {
		
	}
	
	public function main4() {
		self::helper4();
	}
	
	private static function helper4() {
		
	}
	
	public function main5() {
		$this->helper5();
	}
	
	private static function helper5() {
		
	}
}