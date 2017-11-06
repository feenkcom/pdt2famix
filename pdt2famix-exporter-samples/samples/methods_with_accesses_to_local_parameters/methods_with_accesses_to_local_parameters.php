<?php

class MethodsWithAccessesToLocalParameters {
    
	public 	$haba;

    public function methodWithSingleAcccesses(bool $a, int $b) {
		$this->haba;
		$this->methodWithMultipleAcccesses();
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