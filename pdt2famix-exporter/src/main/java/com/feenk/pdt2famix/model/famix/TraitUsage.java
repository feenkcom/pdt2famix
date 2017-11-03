// Automagically generated code, please do not change
package com.feenk.pdt2famix.model.famix;

import ch.akuhn.fame.FameProperty;
import ch.akuhn.fame.FameDescription;
import ch.akuhn.fame.FamePackage;


@FamePackage("FAMIX")
@FameDescription("TraitUsage")
public class TraitUsage extends Association {



    private Trait trait;
    
    @FameProperty(name = "trait", opposite = "incomingTraitUsages")
    public Trait getTrait() {
        return trait;
    }

    public void setTrait(Trait trait) {
        if (this.trait != null) {
            if (this.trait.equals(trait)) return;
            this.trait.getIncomingTraitUsages().remove(this);
        }
        this.trait = trait;
        if (trait == null) return;
        trait.getIncomingTraitUsages().add(this);
    }
    
    private Type user;
    
    @FameProperty(name = "user", opposite = "outgoingTraitUsages")
    public Type getUser() {
        return user;
    }

    public void setUser(Type user) {
        if (this.user != null) {
            if (this.user.equals(user)) return;
            this.user.getOutgoingTraitUsages().remove(this);
        }
        this.user = user;
        if (user == null) return;
        user.getOutgoingTraitUsages().add(this);
    }
    


}

