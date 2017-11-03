// Automagically generated code, please do not change
package com.feenk.pdt2famix.model.famix;

import com.feenk.pdt2famix.model.file.File;
import ch.akuhn.fame.internal.MultivalueSet;
import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import java.util.*;
import ch.akuhn.fame.FamePackage;


@FamePackage("FAMIX")
@FameDescription("CFile")
public class CFile extends File {



    private Collection<Include> incomingIncludeRelations; 

    @FameProperty(name = "incomingIncludeRelations", opposite = "target", derived = true)
    public Collection<Include> getIncomingIncludeRelations() {
        if (incomingIncludeRelations == null) {
            incomingIncludeRelations = new MultivalueSet<Include>() {
                @Override
                protected void clearOpposite(Include e) {
                    e.setTarget(null);
                }
                @Override
                protected void setOpposite(Include e) {
                    e.setTarget(CFile.this);
                }
            };
        }
        return incomingIncludeRelations;
    }
    
    public void setIncomingIncludeRelations(Collection<? extends Include> incomingIncludeRelations) {
        this.getIncomingIncludeRelations().clear();
        this.getIncomingIncludeRelations().addAll(incomingIncludeRelations);
    }                    
    
        
    public void addIncomingIncludeRelations(Include one) {
        this.getIncomingIncludeRelations().add(one);
    }   
    
    public void addIncomingIncludeRelations(Include one, Include... many) {
        this.getIncomingIncludeRelations().add(one);
        for (Include each : many)
            this.getIncomingIncludeRelations().add(each);
    }   
    
    public void addIncomingIncludeRelations(Iterable<? extends Include> many) {
        for (Include each : many)
            this.getIncomingIncludeRelations().add(each);
    }   
                
    public void addIncomingIncludeRelations(Include[] many) {
        for (Include each : many)
            this.getIncomingIncludeRelations().add(each);
    }
    
    public int numberOfIncomingIncludeRelations() {
        return getIncomingIncludeRelations().size();
    }

    public boolean hasIncomingIncludeRelations() {
        return !getIncomingIncludeRelations().isEmpty();
    }
    
                
    private Collection<Include> outgoingIncludeRelations; 

    @FameProperty(name = "outgoingIncludeRelations", opposite = "source", derived = true)
    public Collection<Include> getOutgoingIncludeRelations() {
        if (outgoingIncludeRelations == null) {
            outgoingIncludeRelations = new MultivalueSet<Include>() {
                @Override
                protected void clearOpposite(Include e) {
                    e.setSource(null);
                }
                @Override
                protected void setOpposite(Include e) {
                    e.setSource(CFile.this);
                }
            };
        }
        return outgoingIncludeRelations;
    }
    
    public void setOutgoingIncludeRelations(Collection<? extends Include> outgoingIncludeRelations) {
        this.getOutgoingIncludeRelations().clear();
        this.getOutgoingIncludeRelations().addAll(outgoingIncludeRelations);
    }                    
    
        
    public void addOutgoingIncludeRelations(Include one) {
        this.getOutgoingIncludeRelations().add(one);
    }   
    
    public void addOutgoingIncludeRelations(Include one, Include... many) {
        this.getOutgoingIncludeRelations().add(one);
        for (Include each : many)
            this.getOutgoingIncludeRelations().add(each);
    }   
    
    public void addOutgoingIncludeRelations(Iterable<? extends Include> many) {
        for (Include each : many)
            this.getOutgoingIncludeRelations().add(each);
    }   
                
    public void addOutgoingIncludeRelations(Include[] many) {
        for (Include each : many)
            this.getOutgoingIncludeRelations().add(each);
    }
    
    public int numberOfOutgoingIncludeRelations() {
        return getOutgoingIncludeRelations().size();
    }

    public boolean hasOutgoingIncludeRelations() {
        return !getOutgoingIncludeRelations().isEmpty();
    }
    
                


}

