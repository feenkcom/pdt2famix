package com.feenk.pdt2famix;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.index2.search.ISearchEngine.MatchRule;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.SearchEngine;
import org.eclipse.php.core.ast.nodes.BindingResolver;
import org.eclipse.php.core.ast.nodes.TypeBinding;
import org.eclipse.php.core.compiler.ast.nodes.NamespaceReference;
import org.eclipse.php.internal.core.model.PHPModelAccess;
import org.eclipse.php.internal.core.typeinference.PHPClassType;

import com.dubture.doctrine.annotation.model.Annotation;
import com.dubture.doctrine.annotation.model.AnnotationBlock;
import com.dubture.doctrine.annotation.model.AnnotationClass;
import com.dubture.doctrine.annotation.model.Argument;
import com.dubture.doctrine.annotation.model.NamedArgument;
import com.dubture.doctrine.annotation.parser.AnnotationCommentParser;
import com.dubture.doctrine.core.utils.AnnotationUtils;
import com.feenk.pdt2famix.model.famix.AnnotationInstance;
import com.feenk.pdt2famix.model.famix.AnnotationInstanceAttribute;
import com.feenk.pdt2famix.model.famix.AnnotationType;
import com.feenk.pdt2famix.model.famix.Attribute;
import com.feenk.pdt2famix.model.famix.Namespace;
import com.feenk.pdt2famix.model.famix.Type;

public class AnnotationResolver {
	public static final String ANNOTATION_TAG_PREFIX = "_AnnotationTag";
	
	private Importer importer;
	private String annotationTagName;
	private String defaultAnnotationNamespaceName;
	private AnnotationType annotationTagType;
	
	public AnnotationResolver(String annotationTagName, String defaultAnnotationNamespaceName) {
		this.annotationTagName = annotationTagName;
		this.defaultAnnotationNamespaceName = defaultAnnotationNamespaceName;
	}
	
	public void setImporter(Importer importer) {
		this.importer = importer;
	}
	
	public AnnotationType annotationTagType() {
		if (annotationTagType == null) {
			// We append the suffix "_AnnotationTag" as usually there is an entity with the name annotationTagName.
			// For example there can be an interface with this name that is implemented by annotation classes.
			annotationTagType = ensureAnnotationTypeInNamespace(annotationTagName+ANNOTATION_TAG_PREFIX, importer.systemNamespace());
		}
		return annotationTagType;
	}
	
	private AnnotationType ensureAnnotationTypeInNamespace(String entityName, Namespace namespace) {
		String qualifiedName = importer.makeTypeQualifiedNameFrom(entityName, namespace.getName()); 
		if (importer.types().has(qualifiedName))
			// If this cast fails then somehow an entity with the same name was created from somewhere else.
			return (AnnotationType) importer.types().named(qualifiedName); 
		else {
			AnnotationType newType = new AnnotationType();
			
			newType.setName(entityName);
			newType.setContainer(namespace);
			newType.setIsStub(true);
			importer.types().add(qualifiedName, newType);
			return newType;
		}
	}
	
	public List<AnnotationInstance> extractAnnotationInstancesFromComment(String commentSource, BindingResolver bindingResolver) {
		List<AnnotationInstance> annotationInstances = new ArrayList<>();
		AnnotationCommentParser commentParser = AnnotationUtils.createParser();
		AnnotationBlock annotationBlock;
		try {
			// Sometimes the parser fails
			annotationBlock = commentParser.parse(commentSource);
		} catch (Exception e) {
			e.printStackTrace();
			return annotationInstances; 
		}
		for (Annotation annotationElement: annotationBlock.getAnnotations()) {
			AnnotationInstance annotationInstance = buildAnnotationInstance(annotationElement, bindingResolver);
			annotationInstances.add(annotationInstance);
			importer.repository().add(annotationInstance);
		}
		return annotationInstances;
	}
	
	private AnnotationInstance buildAnnotationInstance(Annotation annotationElement, BindingResolver bindingResolver) {
		AnnotationInstance annotationInstance = new AnnotationInstance();	
		AnnotationType annotationFamixType = null;
		
		// The annotation tag (for example @Annotation) is used to annotate classes
		// that can be used as annotations. This means that the Annotation class is
		// not itself an annotation, as then it needs to be anotated with @Annotation.
		// Usually this entity is an interface that is implemented by annotation classes.
		// To avoid circular relations we use a dedicated annotation type in this case.
		if (isMainAnnotationTag(annotationElement)) {
			annotationFamixType = annotationTagType();
		} else {
			annotationFamixType = resolveAnnotationType(annotationElement, bindingResolver);	
		}
		
		if (annotationFamixType!= null && annotationFamixType.getName() != null && annotationFamixType.getName().equals("$message") ) {
			System.out.println();
		}
		if (annotationFamixType!= null) {
			annotationInstance.setAnnotationType(annotationFamixType);
		}
		
		for (Argument argumentNode: annotationElement.getArguments()) {
			AnnotationInstanceAttribute annotationInstanceAttribute = new AnnotationInstanceAttribute();
			
			String argumentString = "";
			if (argumentNode.getValue()==null && argumentNode instanceof NamedArgument) {
				argumentString = ((NamedArgument)argumentNode).getName() + "=" + "null";
			} else {
				argumentString = argumentNode.toString();
			}
			annotationInstanceAttribute.setValue(argumentString);
			
			annotationInstance.addAttributes(annotationInstanceAttribute);
			importer.repository().add(annotationInstanceAttribute);
		}
		
		return annotationInstance;
	}
	
	public boolean isMainAnnotationTag(AnnotationInstance annotationInstance) {
		return annotationInstance.getAnnotationType().equals(this.annotationTagType());
	}
	
	private boolean isMainAnnotationTag(Annotation annotationElement) {
		return !annotationElement.getAnnotationClass().hasNamespace() && annotationTagName.equals(annotationElement.getClassName());
	}
	
	private AnnotationType resolveAnnotationType(Annotation annotationElement, BindingResolver bindingResolver) {
		AnnotationType annotationFamixType = null;
		IType annotationType = detectAnotationTypeBinding(annotationElement.getAnnotationClass());
		
		if (annotationType != null) {
			Type famixType = null;
			// Once we have a type we need to call #getTypeBinding in the BindingResolver to have access to the type binding.
			// However, that method is protected within the BindingResolver. Because of that we recreate here the actual
			// TypeBinding object, as we already have all the necessary information. 
			TypeBinding typeBinding = new TypeBinding(bindingResolver, PHPClassType.fromIType(annotationType), annotationType);
			famixType = importer.ensureTypeFromTypeBinding(typeBinding);
			if (famixType instanceof AnnotationType) {
				annotationFamixType = (AnnotationType)famixType;
			} else {
				throw new RuntimeException("The type should always be an annotation type");
			}
		}
		if (annotationFamixType == null) {
			// TODO: If an annotation type is not found we create one in the unknown namespace, 
			// if one is not already present there.
			String annotationName = annotationElement.getAnnotationClass().getClassName();
			annotationFamixType = ensureAnnotationTypeInNamespace(annotationName, importer.unknownNamespace());
		}
		return annotationFamixType;
	}
	
	private IType detectAnotationTypeBinding(AnnotationClass node) {
		// TODO: not sure in what case this can happen.
		if (node.getClassName().length() == 0) {
			return null;
		}
		// TODO: For now, ignore lowercase "annotations", phpdoc and phpunit tags.
		// Thus should be handled by creating anntation types in the system namespace.
		if (Character.isLowerCase(node.getClassName().charAt(0))) {
			return null; 
		}
		String fullName;
		if (!node.hasNamespace()) {
			if (importer.usedStatementParts().containsKey(node.getClassName().toLowerCase())) {
				fullName = importer.usedStatementParts().get(node.getClassName().toLowerCase());
			} else {
				if (annotationTagName.equals(node.getClassName())) {
					return null;
				}
				fullName = defaultAnnotationNamespaceName + NamespaceReference.NAMESPACE_SEPARATOR + node.getClassName();
			}
		} else {
			if (importer.usedStatementParts().containsKey(node.getFirstNamespacePart().toLowerCase())) {
				fullName = importer.usedStatementParts().get(node.getFirstNamespacePart().toLowerCase()) + NamespaceReference.NAMESPACE_SEPARATOR + node.getClassName();
			} else {
				fullName = node.getFullyQualifiedName();
			}
		}
		
		IDLTKSearchScope searchScope = SearchEngine.createSearchScope(importer.getCurrentSourceModel().getScriptProject());
		IType[] types = PHPModelAccess.getDefault().findTypes(fullName,
				MatchRule.EXACT, 0, 0, searchScope, new NullProgressMonitor());

		if (types.length != 1) {
			// TODO: Update this once we can handle ambigous types.
			return null;
		}
		return types[0];
	}
}
