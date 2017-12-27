<?php

class MethodsWithAttributeAccesses {
 
	public $field1;
	public $field2;
	public $field3;
	public $field4;
	public $field5;
	public $field6;


	public function main1() {
		$this->field1;
	}

	private function main2() {
		$this->field2;
		$this->field3;
	}
	
	public function main3() {
		$this->field3;
	}
	
	protected function main4() {
		$this->field4 = 1;
	}
	
	protected function main5() {
		$this->field5;
		$this->field5 = 1;
	}
	
	protected function main6() {
		$this->field6 = $this->field6 + 1;
	}	
}