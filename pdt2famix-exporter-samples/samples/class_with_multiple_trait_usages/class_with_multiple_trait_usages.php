<?php

trait TraitAForClassWithMultipleTraitUsages {}

class ClassWithMultipleTraitUsages {
	use TraitAForClassWithMultipleTraitUsages, TraitBForClassWithMultipleTraitUsages;
    
}

trait TraitBForClassWithMultipleTraitUsages {}