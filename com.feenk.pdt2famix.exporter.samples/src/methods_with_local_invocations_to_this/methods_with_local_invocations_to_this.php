<?php

class MethodsWithLocalInvocationsToThis {
 
	private function test1() {
		
	}

	public function main1() {
		$this->test1();
		$this->test2();
	}
	
	public function main2() {
		$this->test1();
		$this->test3();
		$this->test3();
	}
	
	public function test2() {
		$this->test21();
	}
	
	public function test3() {
	}

	private function test21() {

	}
	
	private function testRecursion() {
		$this->testRecursion();
	}
}