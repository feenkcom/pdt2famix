<?php

class MethodsWithVariousParameters {
    
    public function methodWithNoParameters() {
        
    }
    
    public function methodWithGenericParameters($a, $b) {
        
    }
    
    public function methodWithPrimitiveDeclaredParameterTypes(int $a, bool $b) {
        
    }
    
    public function methodWithPrimitiveParametersDefaultValue($a = 1, $b = true) {
        
    }
    
    public function methodWithStandardObjectsAsParameters(DateTime $date1,  $date2 = new DateTime()) {
        
    }
    
    public function methodWithSelfParameterTypes (MethodsWithVariousParameters $param1) {
        
    }
    
    public function methodWithDefaultSelfParameterTypes (MethodsWithVariousParameters $param1 = new MethodsWithVariousParameters(), $param2 = new MethodsWithVariousParameters()) {
        
    }
    
    public function methodWithCustomParameterTypes (AClassForMethodParametersUsage $param1, AnInterfaceForMethodParametersUsage $param2) {
        
    }

	public function methodWithPolymorphicParameterType (AnInterfaceForMethodParametersUsage $param = new AClassForMethodParametersUsage()) {
        
    }
}

class AClassForMethodParametersUsage implements AnInterfaceForMethodParametersUsage {
    
}

interface AnInterfaceForMethodParametersUsage {
    
}