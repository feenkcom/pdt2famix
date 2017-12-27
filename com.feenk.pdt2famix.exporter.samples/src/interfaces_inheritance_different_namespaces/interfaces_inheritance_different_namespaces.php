<?php

namespace namespace_rootinterface1 {
	interface ARootInterfaceOneAB {
    
	}
}

namespace namespace_rootinterface2 {
	interface ARootInterfaceTwoAB {
    
	}
}

namespace namespace_subinterface {
	interface ASubInterfaceAB extends \namespace_rootinterface1\ARootInterfaceOneAB, \namespace_rootinterface2\ARootInterfaceTwoAB {
    
	}
}