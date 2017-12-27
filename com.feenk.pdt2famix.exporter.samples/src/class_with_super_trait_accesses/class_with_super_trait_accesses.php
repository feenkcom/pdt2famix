<?php

class ClassWithSuperTraitAccesses {
    use TraitForClassWithSuperTraitAccesses;
 
    public function main1() {
        $this->field1;
    }
}


trait TraitForClassWithSuperTraitAccesses {
    
    public $field1;
}