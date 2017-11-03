// Automagically generated code, please do not change
package com.feenk.pdt2famix.model.moose;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import com.feenk.pdt2famix.model.famix.SourceLanguage;
import ch.akuhn.fame.FamePackage;


@FamePackage("Moose")
@FameDescription("Model")
public class Model extends AbstractGroup {



    private Number numberOfClassesPerPackage;
    
    @FameProperty(name = "numberOfClassesPerPackage")
    public Number getNumberOfClassesPerPackage() {
        return numberOfClassesPerPackage;
    }

    public void setNumberOfClassesPerPackage(Number numberOfClassesPerPackage) {
        this.numberOfClassesPerPackage = numberOfClassesPerPackage;
    }
    
    private Number numberOfModelMethods;
    
    @FameProperty(name = "numberOfModelMethods")
    public Number getNumberOfModelMethods() {
        return numberOfModelMethods;
    }

    public void setNumberOfModelMethods(Number numberOfModelMethods) {
        this.numberOfModelMethods = numberOfModelMethods;
    }
    
    private Number numberOfMethods;
    
    @FameProperty(name = "numberOfMethods")
    public Number getNumberOfMethods() {
        return numberOfMethods;
    }

    public void setNumberOfMethods(Number numberOfMethods) {
        this.numberOfMethods = numberOfMethods;
    }
    
    private Number numberOfClasses;
    
    @FameProperty(name = "numberOfClasses")
    public Number getNumberOfClasses() {
        return numberOfClasses;
    }

    public void setNumberOfClasses(Number numberOfClasses) {
        this.numberOfClasses = numberOfClasses;
    }
    
    private Number numberOfModelClasses;
    
    @FameProperty(name = "numberOfModelClasses")
    public Number getNumberOfModelClasses() {
        return numberOfModelClasses;
    }

    public void setNumberOfModelClasses(Number numberOfModelClasses) {
        this.numberOfModelClasses = numberOfModelClasses;
    }
    
    private Number numberOfLinesOfCodePerPackage;
    
    @FameProperty(name = "numberOfLinesOfCodePerPackage")
    public Number getNumberOfLinesOfCodePerPackage() {
        return numberOfLinesOfCodePerPackage;
    }

    public void setNumberOfLinesOfCodePerPackage(Number numberOfLinesOfCodePerPackage) {
        this.numberOfLinesOfCodePerPackage = numberOfLinesOfCodePerPackage;
    }
    
    private Number numberOfLinesOfCodePerMethod;
    
    @FameProperty(name = "numberOfLinesOfCodePerMethod")
    public Number getNumberOfLinesOfCodePerMethod() {
        return numberOfLinesOfCodePerMethod;
    }

    public void setNumberOfLinesOfCodePerMethod(Number numberOfLinesOfCodePerMethod) {
        this.numberOfLinesOfCodePerMethod = numberOfLinesOfCodePerMethod;
    }
    
    private SourceLanguage sourceLanguage;
    
    @FameProperty(name = "sourceLanguage")
    public SourceLanguage getSourceLanguage() {
        return sourceLanguage;
    }

    public void setSourceLanguage(SourceLanguage sourceLanguage) {
        this.sourceLanguage = sourceLanguage;
    }
    
    private Number numberOfLinesOfCodePerClass;
    
    @FameProperty(name = "numberOfLinesOfCodePerClass")
    public Number getNumberOfLinesOfCodePerClass() {
        return numberOfLinesOfCodePerClass;
    }

    public void setNumberOfLinesOfCodePerClass(Number numberOfLinesOfCodePerClass) {
        this.numberOfLinesOfCodePerClass = numberOfLinesOfCodePerClass;
    }
    


}

