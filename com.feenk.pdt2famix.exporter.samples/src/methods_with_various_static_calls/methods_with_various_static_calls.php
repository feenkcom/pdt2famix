<?php

class Util_ForMethodsWithVariousStaticCalls {
	public static function helper4() {
		
	}
	
	public static function helper5() {
		
	}
}

class MethodsWithVariousStaticCalls {
 
	private $field2 = new MethodsWithVariousStaticCalls();

	public static function main1(MethodsWithVariousStaticCalls $a) {
		$a::helper1();
	}
	
	private static function helper1() {
		
	}
	
	public function main2() {
	    $this->field2::helper2();
	}
	
	private static function helper2() {
		
	}
	
	public function main3() {
		$this->helper31()::helper32();
	}
	
	/**
	 *
	 * @return MethodsWithVariousStaticCalls
	 */
	private function helper31() {
	    return new MethodsWithVariousStaticCalls();
	}
	
	private static function helper32() {
		
	}
	
	public function main4() {
		Util_ForMethodsWithVariousStaticCalls::helper4();
	}
	
	public function main5(Util_ForMethodsWithVariousStaticCalls $a) {
		$a::helper5();
	}
}