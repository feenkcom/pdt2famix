<?php

class ClassWithSuperTraitCalls {
    use TraitForClassWithSuperTraitCalls;
 
    public function main1() {
        $this->helper1();
    }
    
    public function main2() {
        $this->helper2();
    }
    
    public function helper2 () {
        
    }
}


trait TraitForClassWithSuperTraitCalls {
    
    public function helper1 () {
        
    }
    
    public function helper2 () {
        
    }
}