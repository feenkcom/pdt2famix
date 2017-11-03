// Automagically generated code, please do not change
package com.feenk.pdt2famix.model.famix;

import com.feenk.pdt2famix.model.moose.Group;
import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;


@FamePackage("FAMIX")
@FameDescription("TypeGroup")
public class TypeGroup extends Group {



    private Number averageNumberOfMethods;
    
    @FameProperty(name = "averageNumberOfMethods")
    public Number getAverageNumberOfMethods() {
        return averageNumberOfMethods;
    }

    public void setAverageNumberOfMethods(Number averageNumberOfMethods) {
        this.averageNumberOfMethods = averageNumberOfMethods;
    }
    
    private Number averageNumberOfStatements;
    
    @FameProperty(name = "averageNumberOfStatements")
    public Number getAverageNumberOfStatements() {
        return averageNumberOfStatements;
    }

    public void setAverageNumberOfStatements(Number averageNumberOfStatements) {
        this.averageNumberOfStatements = averageNumberOfStatements;
    }
    
    private Number averageNumberOfAttributes;
    
    @FameProperty(name = "averageNumberOfAttributes")
    public Number getAverageNumberOfAttributes() {
        return averageNumberOfAttributes;
    }

    public void setAverageNumberOfAttributes(Number averageNumberOfAttributes) {
        this.averageNumberOfAttributes = averageNumberOfAttributes;
    }
    


}

