<?php
	
class ClassWithArrayAttributes
{
	public $stringArray = array("a","b","c");
	public $numberArray = array(1, 3, 5);
	public $booleanArray = array(true, false);
	public $nullArray = array(NULL, NULL);
	public $mixedArray = array("a", 1, true, NULL);
	
	public $emptyArray = [];
	public $numberSyntaxShortArray = [1,2,3];
}
