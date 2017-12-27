// Automagically generated code, please do not change
package com.feenk.pdt2famix.exporter.model.famix;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;


@FamePackage("FAMIX")
@FameDescription("PreprocessorIfdef")
public class PreprocessorIfdef extends PreprocessorStatement {



    private String macro;
    
    @FameProperty(name = "macro")
    public String getMacro() {
        return macro;
    }

    public void setMacro(String macro) {
        this.macro = macro;
    }
    
    private Boolean negated;
    
    @FameProperty(name = "negated")
    public Boolean getNegated() {
        return negated;
    }

    public void setNegated(Boolean negated) {
        this.negated = negated;
    }
    


}

