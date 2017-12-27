<?php

class MethodsWithLocalInvocationsToSelfParameters {
 
	private function helper1() {
		
	}
	
	public function main1(MethodsWithLocalInvocationsToSelfParameters $param1, MethodsWithLocalInvocationsToSelfParameters $param2) {
		$param1->helper1();
		$param2->helper2();
	}
	
	private function helper2() {
		
	}
	
	public function main2(MethodsWithLocalInvocationsToSelfParameters $param1, MethodsWithLocalInvocationsToSelfParameters $param2) {
		$param1->helper2();
		$param2->helper2();
	}
	
	public function mainRecursion(MethodsWithLocalInvocationsToSelfParameters $param) {
		$param->mainRecursion();
	}
}