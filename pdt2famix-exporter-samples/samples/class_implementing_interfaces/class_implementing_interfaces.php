<?php
	
namespace {
    
    class ClassImplementingInterfaces implements \InterfaceWithOneMethodAndConstructorAB, \for_ClassImplementingInterfaces\InterfaceInAnExplicitNamespaceAB
    {
	   public function __construct() {
	   	
	   }
	   
	   public function test1() {
		
	   }
	
        public function test2() {
		
     	}
    }

    interface InterfaceWithOneMethodAndConstructorAB {
	
	   function __construct();
	   public function test1();
	
    }   
}

namespace for_ClassImplementingInterfaces {
    
    interface InterfaceInAnExplicitNamespaceAB {
        public function test1();
        public function test2();
    }
}