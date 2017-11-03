// Automagically generated code, please do not change
package com.feenk.pdt2famix.model.moose;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;


@FamePackage("Moose")
@FameDescription("PropertyGroup")
public class PropertyGroup extends Group {



    private Number sizeOriginal;
    
    @FameProperty(name = "sizeOriginal")
    public Number getSizeOriginal() {
        return sizeOriginal;
    }

    public void setSizeOriginal(Number sizeOriginal) {
        this.sizeOriginal = sizeOriginal;
    }
    
    private Number sizeRatio;
    
    @FameProperty(name = "sizeRatio")
    public Number getSizeRatio() {
        return sizeRatio;
    }

    public void setSizeRatio(Number sizeRatio) {
        this.sizeRatio = sizeRatio;
    }
    
    private String property;
    
    @FameProperty(name = "property")
    public String getProperty() {
        return property;
    }

    public void setProperty(String property) {
        this.property = property;
    }
    
    private Number propertyRatio;
    
    @FameProperty(name = "propertyRatio")
    public Number getPropertyRatio() {
        return propertyRatio;
    }

    public void setPropertyRatio(Number propertyRatio) {
        this.propertyRatio = propertyRatio;
    }
    
    private Number propertyTotal;
    
    @FameProperty(name = "propertyTotal")
    public Number getPropertyTotal() {
        return propertyTotal;
    }

    public void setPropertyTotal(Number propertyTotal) {
        this.propertyTotal = propertyTotal;
    }
    
    private Number propertyTotalOriginal;
    
    @FameProperty(name = "propertyTotalOriginal")
    public Number getPropertyTotalOriginal() {
        return propertyTotalOriginal;
    }

    public void setPropertyTotalOriginal(Number propertyTotalOriginal) {
        this.propertyTotalOriginal = propertyTotalOriginal;
    }
    


}

