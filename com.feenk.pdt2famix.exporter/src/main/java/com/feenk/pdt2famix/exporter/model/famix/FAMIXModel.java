// Automagically generated code, please do not change
package com.feenk.pdt2famix.exporter.model.famix;

import ch.akuhn.fame.MetaRepository;

public class FAMIXModel {

    public static MetaRepository metamodel() {
        MetaRepository metamodel = new MetaRepository();
        importInto(metamodel);
        return metamodel;
    }
    
    public static void importInto(MetaRepository metamodel) {
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.Class.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.PreprocessorIfdef.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.Comment.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.SmalltalkSourceLanguage.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.Invocation.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.Entity.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.SourceLanguage.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.ContainerEntity.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.Package.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.Attribute.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.IndexedFileAnchor.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.Namespace.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.AnnotationType.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.Reference.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.Include.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.CompilationUnit.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.CustomSourceLanguage.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.MultipleFileAnchor.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.GlobalVariable.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.Header.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.UnknownVariable.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.Module.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.PreprocessorStatement.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.AbstractFileAnchor.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.Exception.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.CFile.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.Function.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.CppSourceLanguage.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.FileAnchor.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.EnumValue.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.StructuralEntity.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.PharoAnchor.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.DereferencedInvocation.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.ScopingEntity.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.Inheritance.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.TypeAlias.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.TraitUsage.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.Enum.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.ParameterizableClass.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.Parameter.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.BehaviouralEntity.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.ThrownException.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.SourceTextAnchor.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.CSourceLanguage.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.LocalVariable.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.PrimitiveType.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.Trait.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.ParameterizedType.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.CaughtException.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.LeafEntity.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.Association.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.ImplicitVariable.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.Method.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.DeclaredException.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.PreprocessorDefine.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.SourcedEntity.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.NamedEntity.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.SmalltalkMonticelloSourceLanguage.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.SourceAnchor.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.JavaSourceLanguage.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.UnknownSourceLanguage.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.Access.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.Type.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.AnnotationInstance.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.AnnotationTypeAttribute.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.ParameterType.class);
		metamodel.with(com.feenk.pdt2famix.exporter.model.famix.AnnotationInstanceAttribute.class);

    }

}

