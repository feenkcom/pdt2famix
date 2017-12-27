<?php

interface InterfaceWithSimpleMethods {

	public function __construct($input, $output);
	
	public function foo();	
	
	public function bar($x);
	
	function baz(int $x);
}
