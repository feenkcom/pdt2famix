<?php

class MethodsWithLocalInvocationsToSelfAttributes {
 
	private $field11 = new MethodsWithLocalInvocationsToSelfAttributes();
	public $field12 = new MethodsWithLocalInvocationsToSelfAttributes();
	private $field2 = new MethodsWithLocalInvocationsToSelfAttributes();
	private $field31 = new MethodsWithLocalInvocationsToSelfAttributes();
	public $field32 = new MethodsWithLocalInvocationsToSelfAttributes();
	public $fieldRecursion = new MethodsWithLocalInvocationsToSelfAttributes();

	public function main1() {
		$this->field11->helper1();
		$this->field12->helper1();
	}
	
	private function helper1() {
		
	}
	
	public function main2() {
		$this->field2->helper2();
		$this->field2->helper2();
	}
	
	private function helper2() {
		
	}
	
	public function main3() {
		$this->field31->helper31();
		$this->field32->helper32();
	}
	
	private function helper31() {
		
	}
	
	private function helper32() {
		
	}
	
	public function mainRecursion() {
		$this->fieldRecursion->mainRecursion();
	}
	
}