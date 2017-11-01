package com.feenk.pdt2famix.model.php;

import java.util.Collection;

import com.feenk.pdt2famix.model.famix.Class;
import com.feenk.pdt2famix.model.famix.Type;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;
import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.internal.MultivalueSet;

@FamePackage("FAMIX")
@FameDescription("Trait")
public class Trait extends Type {

	private Collection<Class> classes; 

    @FameProperty(name = "classes", opposite = "traits")
    public Collection<Class> getClasses() {
        if (classes == null) {
        	classes = new MultivalueSet<Class>() {
                @Override
                protected void clearOpposite(Class e) {
                    e.getTraits().remove(Trait.this);
                }
                @Override
                protected void setOpposite(Class e) {
                    e.getTraits().add(Trait.this);
                }
            };
        }
        return classes;
    }
    
    public void setClasses(Collection<? extends Class> arguments) {
        this.getClasses().clear();
        this.getClasses().addAll(arguments);
    }
    
    public void addClasses(Class one) {
        this.getClasses().add(one);
    }   
    
    public void addClasses(Class one, Class... many) {
        this.getClasses().add(one);
        for (Class each : many)
            this.getClasses().add(each);
    }   
    
    public void addClasses(Iterable<? extends Class> many) {
        for (Class each : many)
            this.getClasses().add(each);
    }   
                
    public void addClasses(Class[] many) {
        for (Class each : many)
            this.getClasses().add(each);
    }
    
    public int numberOfClasses() {
        return getClasses().size();
    }

    public boolean hasClasses() {
        return !getClasses().isEmpty();
    }
	
}
