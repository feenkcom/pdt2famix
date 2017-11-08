<?php

trait TraitAForClassWithMultipleTraitsUsage {
    
}

class ClassWithMultipleTraitsUsage {
    use TraitAForClassWithMultipleTraitsUsage, TraitBForClassWithMultipleTraitsUsage;
    
    
}

trait TraitBForClassWithMultipleTraitsUsage {
    
}