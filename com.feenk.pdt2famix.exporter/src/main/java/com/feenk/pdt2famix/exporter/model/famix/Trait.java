// Automagically generated code, please do not change
package com.feenk.pdt2famix.exporter.model.famix;

import ch.akuhn.fame.internal.MultivalueSet;
import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import java.util.*;
import ch.akuhn.fame.FamePackage;


@FamePackage("FAMIX")
@FameDescription("Trait")
public class Trait extends Type {



    private Collection<TraitUsage> incomingTraitUsages; 

    @FameProperty(name = "incomingTraitUsages", opposite = "trait", derived = true)
    public Collection<TraitUsage> getIncomingTraitUsages() {
        if (incomingTraitUsages == null) {
            incomingTraitUsages = new MultivalueSet<TraitUsage>() {
                @Override
                protected void clearOpposite(TraitUsage e) {
                    e.setTrait(null);
                }
                @Override
                protected void setOpposite(TraitUsage e) {
                    e.setTrait(Trait.this);
                }
            };
        }
        return incomingTraitUsages;
    }
    
    public void setIncomingTraitUsages(Collection<? extends TraitUsage> incomingTraitUsages) {
        this.getIncomingTraitUsages().clear();
        this.getIncomingTraitUsages().addAll(incomingTraitUsages);
    }                    
    
        
    public void addIncomingTraitUsages(TraitUsage one) {
        this.getIncomingTraitUsages().add(one);
    }   
    
    public void addIncomingTraitUsages(TraitUsage one, TraitUsage... many) {
        this.getIncomingTraitUsages().add(one);
        for (TraitUsage each : many)
            this.getIncomingTraitUsages().add(each);
    }   
    
    public void addIncomingTraitUsages(Iterable<? extends TraitUsage> many) {
        for (TraitUsage each : many)
            this.getIncomingTraitUsages().add(each);
    }   
                
    public void addIncomingTraitUsages(TraitUsage[] many) {
        for (TraitUsage each : many)
            this.getIncomingTraitUsages().add(each);
    }
    
    public int numberOfIncomingTraitUsages() {
        return getIncomingTraitUsages().size();
    }

    public boolean hasIncomingTraitUsages() {
        return !getIncomingTraitUsages().isEmpty();
    }
    
                


}

