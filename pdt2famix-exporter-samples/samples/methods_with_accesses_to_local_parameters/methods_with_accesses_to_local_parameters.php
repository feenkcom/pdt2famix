<?php

class MethodsWithAccessesToLocalParameters {
    
    public function methodWithSingleAcccesses(bool $a, int $b) {
        if ($a) {
			$b;
		}
		
    }

	public function methodWithMultipleAcccesses(int $b) {
		$b + $b;
    }
    
    public function methodWithAcccessAndAssignment(int $a, int $b) {
		$a = $b + $b;
    }
   
}