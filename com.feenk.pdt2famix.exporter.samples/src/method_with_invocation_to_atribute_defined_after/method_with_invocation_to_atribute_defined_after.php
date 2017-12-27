<?php

class MethodWithInvocationToAtributeDefinedAfter {
 
	public $field1 = new MethodWithInvocationToAtributeDefinedAfter();

	public function main1() {
		$this->field1->helper1();
		$this->field2->helper2();
	}
	
	private function helper1() {
		
	}
	
	private function helper2() {
		
	}
	
	public $field2 = new MethodWithInvocationToAtributeDefinedAfter();
}