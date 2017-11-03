<?php

class MethodsWithLocalInvocationsToSelfParameters {
 
	private function helper1() {
		
	}
	
	public function main1(MethodsWithLocalInvocationsToSelfAttributes $param1, MethodsWithLocalInvocationsToSelfAttributes $param2) {
		$param1->helper1();
		$param2->helper2();
	}
	
	private function helper2() {
		
	}
	
	public function main2(MethodsWithLocalInvocationsToSelfAttributes $param1, MethodsWithLocalInvocationsToSelfAttributes $param2) {
		$param1->helper2();
		$param2->helper2();
	}
	
	public function mainRecursion(MethodsWithLocalInvocationsToSelfAttributes $param) {
		$param->mainRecursion();
	}
}