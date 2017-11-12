<?php


interface InterfaceAFor_ClassWithConstAccessesFromInterface {
	const CONST1_INTERFACE = 1;
}

interface InterfaceBFor_ClassWithConstAccessesFromInterface {
	const CONST1_INTERFACE = 1;
}

class ClassWithConstAccessesFromInterface implements InterfaceAFor_ClassWithConstAccessesFromInterface {
	
	public function main1InternalInterfaceConstantAccess() {
		self::CONST1_INTERFACE;
	}

	public function main2ExternalInterfaceConstantAccess() {
		InterfaceBFor_ClassWithConstAccessesFromInterface::CONST1_INTERFACE;
	}
}