// Automagically generated code, please do not change
package com.feenk.pdt2famix.model.moose;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;


@FamePackage("Moose")
@FameDescription("AbstractGroup")
public class AbstractGroup extends Entity {



    private Number numberOfAssociations;
    
    @FameProperty(name = "numberOfAssociations")
    public Number getNumberOfAssociations() {
        return numberOfAssociations;
    }

    public void setNumberOfAssociations(Number numberOfAssociations) {
        this.numberOfAssociations = numberOfAssociations;
    }
    
    private Number numberOfItems;
    
    @FameProperty(name = "numberOfItems")
    public Number getNumberOfItems() {
        return numberOfItems;
    }

    public void setNumberOfItems(Number numberOfItems) {
        this.numberOfItems = numberOfItems;
    }
    
    private Number numberOfEntities;
    
    @FameProperty(name = "numberOfEntities")
    public Number getNumberOfEntities() {
        return numberOfEntities;
    }

    public void setNumberOfEntities(Number numberOfEntities) {
        this.numberOfEntities = numberOfEntities;
    }
    
    private Number numberOfLinesOfCode;
    
    @FameProperty(name = "numberOfLinesOfCode")
    public Number getNumberOfLinesOfCode() {
        return numberOfLinesOfCode;
    }

    public void setNumberOfLinesOfCode(Number numberOfLinesOfCode) {
        this.numberOfLinesOfCode = numberOfLinesOfCode;
    }
    
    private Number numberOfPackages;
    
    @FameProperty(name = "numberOfPackages")
    public Number getNumberOfPackages() {
        return numberOfPackages;
    }

    public void setNumberOfPackages(Number numberOfPackages) {
        this.numberOfPackages = numberOfPackages;
    }
    


}

