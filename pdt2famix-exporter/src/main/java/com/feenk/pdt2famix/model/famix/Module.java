// Automagically generated code, please do not change
package com.feenk.pdt2famix.model.famix;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;


@FamePackage("FAMIX")
@FameDescription("Module")
public class Module extends ScopingEntity {



    private CompilationUnit compilationUnit;
    
    @FameProperty(name = "compilationUnit", opposite = "module", derived = true)
    public CompilationUnit getCompilationUnit() {
        return compilationUnit;
    }

    public void setCompilationUnit(CompilationUnit compilationUnit) {
        if (this.compilationUnit == null ? compilationUnit != null : !this.compilationUnit.equals(compilationUnit)) {
            CompilationUnit old_compilationUnit = this.compilationUnit;
            this.compilationUnit = compilationUnit;
            if (old_compilationUnit != null) old_compilationUnit.setModule(null);
            if (compilationUnit != null) compilationUnit.setModule(this);
        }
    }
    
    private Header header;
    
    @FameProperty(name = "header", opposite = "module", derived = true)
    public Header getHeader() {
        return header;
    }

    public void setHeader(Header header) {
        if (this.header == null ? header != null : !this.header.equals(header)) {
            Header old_header = this.header;
            this.header = header;
            if (old_header != null) old_header.setModule(null);
            if (header != null) header.setModule(this);
        }
    }
    


}

