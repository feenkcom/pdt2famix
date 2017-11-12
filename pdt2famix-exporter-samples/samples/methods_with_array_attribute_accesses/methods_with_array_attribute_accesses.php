<?php

class MethodsWithArrayAttributeAccesses {
 
	public $field1;

	public $field2 = [];
	public $field3 = array();
	
	/** @var array */
	public $field4;
	
	public $simpleField5 = 'key5';
	
	/** @var array */
	public $field6;
	
	public function main1() {
		$this->field1 = [];
	}

	public function main2() {
		$this->field2['key'];
	}
	
	public function main3() {
		$this->field3['key1'];
		$this->field3['key2'];
	}
	
	/**
     * @param string $name
     */
	public function main4($name) {
		$this->field4[$name];
		$this->field4[$this->simpleField5];
	}
	
	public function main6() {
		$this->field6['key6'] = 1;
	}
}