// Automagically generated code, please do not change
package com.feenk.pdt2famix.model.famix;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.internal.MultivalueSet;

import java.util.Collection;

import com.feenk.pdt2famix.model.php.Trait;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;


@FamePackage("FAMIX")
@FameDescription("Class")
public class Class extends Type {



    private Boolean isInterface;
    
    @FameProperty(name = "isInterface")
    public Boolean getIsInterface() {
        return isInterface;
    }

    public void setIsInterface(Boolean isInterface) {
        this.isInterface = isInterface;
    }
    
    
    
    private Collection<Trait> traits; 

    @FameProperty(name = "traits", opposite = "classes", derived = true)
    public Collection<Trait> getTraits() {
        if (traits == null) {
        	traits = new MultivalueSet<Trait>() {
                @Override
                protected void clearOpposite(Trait e) {
                    e.getClasses().remove(Class.this);
                }
                @Override
                protected void setOpposite(Trait e) {
                    e.getClasses().add(Class.this);
                }
            };
        }
        return traits;
    }
    
    public void setTraits(Collection<? extends Trait> traits) {
        this.getTraits().clear();
        this.getTraits().addAll(traits);
    }
    
    public void addTraits(Trait one) {
        this.getTraits().add(one);
    }   
    
    public void addTraits(Trait one, Trait... many) {
        this.getTraits().add(one);
        for (Trait each : many)
            this.getTraits().add(each);
    }   
    
    public void addTraits(Iterable<? extends Trait> many) {
        for (Trait each : many)
            this.getTraits().add(each);
    }   
                
    public void addTraits(Trait[] many) {
        for (Trait each : many)
            this.getTraits().add(each);
    }
    
    public int numberOfTraits() {
        return getTraits().size();
    }

    public boolean hasTraits() {
        return !getTraits().isEmpty();
    }


}

