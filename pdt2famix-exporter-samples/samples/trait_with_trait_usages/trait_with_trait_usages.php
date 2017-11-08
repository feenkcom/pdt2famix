<?php

trait RootTraitForTraitWithTraitUsages {
 
}

trait HelloTraitForTraitWithTraitUsages {
	use RootTraitForTraitWithTraitUsages;
    
}

trait TraitWithTraitUsages {
	use HelloTraitForTraitWithTraitUsages, WorldTraitForTraitWithTraitUsages;
	
}

trait WorldTraitForTraitWithTraitUsages {
   
}