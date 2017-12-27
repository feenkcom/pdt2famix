// Automagically generated code, please do not change
package com.feenk.pdt2famix.exporter.model.famix;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import java.util.*;
import ch.akuhn.fame.FamePackage;


@FamePackage("FAMIX")
@FameDescription("MultipleFileAnchor")
public class MultipleFileAnchor extends SourceAnchor {



    private Collection<AbstractFileAnchor> allFiles; 

    @FameProperty(name = "allFiles")
    public Collection<AbstractFileAnchor> getAllFiles() {
        if (allFiles == null) allFiles = new HashSet<AbstractFileAnchor>();
        return allFiles;
    }
    
    public void setAllFiles(Collection<? extends AbstractFileAnchor> allFiles) {
        this.getAllFiles().clear();
        this.getAllFiles().addAll(allFiles);
    }                    

    public void addAllFiles(AbstractFileAnchor one) {
        this.getAllFiles().add(one);
    }   
    
    public void addAllFiles(AbstractFileAnchor one, AbstractFileAnchor... many) {
        this.getAllFiles().add(one);
        for (AbstractFileAnchor each : many)
            this.getAllFiles().add(each);
    }   
    
    public void addAllFiles(Iterable<? extends AbstractFileAnchor> many) {
        for (AbstractFileAnchor each : many)
            this.getAllFiles().add(each);
    }   
                
    public void addAllFiles(AbstractFileAnchor[] many) {
        for (AbstractFileAnchor each : many)
            this.getAllFiles().add(each);
    }
    
    public int numberOfAllFiles() {
        return getAllFiles().size();
    }

    public boolean hasAllFiles() {
        return !getAllFiles().isEmpty();
    }
    
                


}

