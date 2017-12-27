// Automagically generated code, please do not change
package com.feenk.pdt2famix.exporter.model.famix;

import ch.akuhn.fame.internal.MultivalueSet;
import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import java.util.*;
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
    
    private Collection<Exception> exceptions; 

    @FameProperty(name = "exceptions", opposite = "exceptionClass", derived = true)
    public Collection<Exception> getExceptions() {
        if (exceptions == null) {
            exceptions = new MultivalueSet<Exception>() {
                @Override
                protected void clearOpposite(Exception e) {
                    e.setExceptionClass(null);
                }
                @Override
                protected void setOpposite(Exception e) {
                    e.setExceptionClass(Class.this);
                }
            };
        }
        return exceptions;
    }
    
    public void setExceptions(Collection<? extends Exception> exceptions) {
        this.getExceptions().clear();
        this.getExceptions().addAll(exceptions);
    }                    
    
        
    public void addExceptions(Exception one) {
        this.getExceptions().add(one);
    }   
    
    public void addExceptions(Exception one, Exception... many) {
        this.getExceptions().add(one);
        for (Exception each : many)
            this.getExceptions().add(each);
    }   
    
    public void addExceptions(Iterable<? extends Exception> many) {
        for (Exception each : many)
            this.getExceptions().add(each);
    }   
                
    public void addExceptions(Exception[] many) {
        for (Exception each : many)
            this.getExceptions().add(each);
    }
    
    public int numberOfExceptions() {
        return getExceptions().size();
    }

    public boolean hasExceptions() {
        return !getExceptions().isEmpty();
    }
    
                


}

