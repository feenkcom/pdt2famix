// Automagically generated code, please do not change
package com.feenk.pdt2famix.model.file;

import ch.akuhn.fame.FameProperty;

import com.feenk.pdt2famix.model.famix.Entity;

import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;


@FamePackage("FILE")
@FameDescription("AbstractFile")
public class AbstractFile extends Entity {



    private String name;
    
    @FameProperty(name = "name")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    


}

