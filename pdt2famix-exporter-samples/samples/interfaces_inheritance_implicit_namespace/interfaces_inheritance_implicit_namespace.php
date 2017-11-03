<?php

interface ARootSuperInterface {
    
}

interface AnotherSubInterface extends ARootSuperInterface {
    
}

interface ASubInterface extends AnotherSubInterface, ARootSuperInterface { 
    
}

interface ASubSubInterface extends ASubInterface {
    
}

