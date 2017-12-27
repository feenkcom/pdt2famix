<?php

class MethodInvocationsWithArguments {
 
	public $field = new MethodInvocationsWithArguments();
	
	public function main1() {
		$this->helper1(1, 42+2);
	}
	
	public function main2() {
		$this->field->helper2(10, 12==12, new MethodInvocationsWithArguments());
	}
	
	public function main3() {
		$this->helper3();
		$this->helper3(1, 2, 4, 5);
	}
	
	private function helper1(int $a, int $b) {
		
	}
	
	private function helper2(int $x, bool $y, $z) {
		
	}
	
	private function helper3(int $a) {
		
	}
}