<?php

class MethodsWithInvocationsToGenericParameters {
    
    public function main1($x) {
        $x->uniqueMethod_12A();
    }
    
    public function main2($x) {
        $x->uniqueMethod_12B();
    }
    
    /**
     * @param TestClass1_12C $x
     */
    public function main3($x) {
        $x->uniqueMethod_12C();
    }
}


class TestClass1_12B {
    
    public function uniqueMethod_12B() {
        
    }
    
}

class TestClass1_12C {
    
    public function uniqueMethod_12C() {
        
    }
    
}
