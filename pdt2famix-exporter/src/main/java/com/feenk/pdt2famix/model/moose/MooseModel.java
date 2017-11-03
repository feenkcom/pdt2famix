// Automagically generated code, please do not change
package com.feenk.pdt2famix.model.moose;

import ch.akuhn.fame.MetaRepository;

public class MooseModel {

    public static MetaRepository metamodel() {
        MetaRepository metamodel = new MetaRepository();
        importInto(metamodel);
        return metamodel;
    }
    
    public static void importInto(MetaRepository metamodel) {
		metamodel.with(com.feenk.pdt2famix.model.moose.Entity.class);
		metamodel.with(com.feenk.pdt2famix.model.moose.PropertyGroup.class);
		metamodel.with(com.feenk.pdt2famix.model.moose.Model.class);
		metamodel.with(com.feenk.pdt2famix.model.moose.AbstractGroup.class);
		metamodel.with(com.feenk.pdt2famix.model.moose.Group.class);

    }

}

