<?php

class MethodsWithChainedAttributesAndMessages {
 
	public $field1 = new MethodsWithChainedAttributesAndMessages();
	public $field2 = new MethodsWithChainedAttributesAndMessages();
	public $field3 = new MethodsWithChainedAttributesAndMessages();
	public $field4 = new MethodsWithChainedAttributesAndMessages();
	
	public function main1() {
		$this->helper1()->field1->helper1();
	}
	
	/**
	* @return MethodsWithChainedAttributesAndMessages
	*/
	private function helper1() {
		return $this;
	}
	
	public function main2() {
		$this->field2->helper2()->field2;
	}
	
	/**
	* @return MethodsWithChainedAttributesAndMessages
	*/
	private function helper2() {
		return $this;
	}
	
	public function main3() {
		$this->helper3()->helper3()->helper3();
	}
	
	/**
	* @return MethodsWithChainedAttributesAndMessages
	*/
	private function helper3() {
		return $this;
	}
	
	public function main4() {
		$this->field4->field4->field4;
	}
	
	/**
	* @return MethodsWithChainedAttributesAndMessages
	*/
	private function helper4() {
		return $this;
	}
}


class TestClass_QW {
	
}