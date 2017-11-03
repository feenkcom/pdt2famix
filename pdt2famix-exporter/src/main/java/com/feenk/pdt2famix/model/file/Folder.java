// Automagically generated code, please do not change
package com.feenk.pdt2famix.model.file;

import ch.akuhn.fame.internal.MultivalueSet;
import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import java.util.*;
import ch.akuhn.fame.FamePackage;


@FamePackage("FILE")
@FameDescription("Folder")
public class Folder extends AbstractFile {



    private Collection<AbstractFile> childrenFileSystemEntities; 

    @FameProperty(name = "childrenFileSystemEntities", opposite = "parentFolder", derived = true)
    public Collection<AbstractFile> getChildrenFileSystemEntities() {
        if (childrenFileSystemEntities == null) {
            childrenFileSystemEntities = new MultivalueSet<AbstractFile>() {
                @Override
                protected void clearOpposite(AbstractFile e) {
                    e.setParentFolder(null);
                }
                @Override
                protected void setOpposite(AbstractFile e) {
                    e.setParentFolder(Folder.this);
                }
            };
        }
        return childrenFileSystemEntities;
    }
    
    public void setChildrenFileSystemEntities(Collection<? extends AbstractFile> childrenFileSystemEntities) {
        this.getChildrenFileSystemEntities().clear();
        this.getChildrenFileSystemEntities().addAll(childrenFileSystemEntities);
    }                    
    
        
    public void addChildrenFileSystemEntities(AbstractFile one) {
        this.getChildrenFileSystemEntities().add(one);
    }   
    
    public void addChildrenFileSystemEntities(AbstractFile one, AbstractFile... many) {
        this.getChildrenFileSystemEntities().add(one);
        for (AbstractFile each : many)
            this.getChildrenFileSystemEntities().add(each);
    }   
    
    public void addChildrenFileSystemEntities(Iterable<? extends AbstractFile> many) {
        for (AbstractFile each : many)
            this.getChildrenFileSystemEntities().add(each);
    }   
                
    public void addChildrenFileSystemEntities(AbstractFile[] many) {
        for (AbstractFile each : many)
            this.getChildrenFileSystemEntities().add(each);
    }
    
    public int numberOfChildrenFileSystemEntities() {
        return getChildrenFileSystemEntities().size();
    }

    public boolean hasChildrenFileSystemEntities() {
        return !getChildrenFileSystemEntities().isEmpty();
    }
    
                


}

