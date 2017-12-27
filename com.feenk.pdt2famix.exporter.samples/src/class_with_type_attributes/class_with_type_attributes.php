<?php

class TypeForOneAttribute{}
	
class ClassWithTypeAttributes {
    
 	public $first = new TypeForOneAttribute();
	public $second = new TypeForOnetherAttribute();
}

class TypeForOnetherAttribute{}