<?php


 trait TraitForClassWithSingleTraitUsage {
     public function sayHello() {
         echo 'Hello ';
     }
}

class ClassWithSingleTraitUsage {
    use TraitForClassWithSingleTraitUsage;
    
    
}