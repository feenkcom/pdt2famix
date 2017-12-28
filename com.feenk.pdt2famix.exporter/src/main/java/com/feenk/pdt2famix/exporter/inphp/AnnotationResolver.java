package com.feenk.pdt2famix.exporter.inphp;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.index2.search.ISearchEngine.MatchRule;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.SearchEngine;
import org.eclipse.php.core.ast.nodes.ASTNode;
import org.eclipse.php.core.ast.nodes.ITypeBinding;
import org.eclipse.php.core.compiler.ast.nodes.NamespaceReference;
import org.eclipse.php.internal.core.model.PHPModelAccess;

import com.dubture.doctrine.annotation.model.Annotation;
import com.dubture.doctrine.annotation.model.AnnotationBlock;
import com.dubture.doctrine.annotation.model.AnnotationClass;
import com.dubture.doctrine.annotation.model.Argument;
import com.dubture.doctrine.annotation.model.NamedArgument;
import com.dubture.doctrine.annotation.parser.AnnotationCommentParser;
import com.dubture.doctrine.core.utils.AnnotationUtils;
import com.feenk.pdt2famix.exporter.model.famix.AnnotationInstance;
import com.feenk.pdt2famix.exporter.model.famix.AnnotationInstanceAttribute;
import com.feenk.pdt2famix.exporter.model.famix.AnnotationType;
import com.feenk.pdt2famix.exporter.model.famix.Namespace;
import com.feenk.pdt2famix.exporter.model.famix.Type;

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
	
	public List<AnnotationInstance> extractAnnotationInstancesFromComment(String commentSource, ASTNode targetNode) {
		List<AnnotationInstance> annotationInstances = new ArrayList<>();
		Map<String, String> usedStatementParts = UseStatementsVisitor.extractUseStatements(targetNode);
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
			AnnotationInstance annotationInstance = buildAnnotationInstance(annotationElement, targetNode, usedStatementParts);
			annotationInstances.add(annotationInstance);
			importer.repository().add(annotationInstance);
		}
		return annotationInstances;
	}
	
	private AnnotationInstance buildAnnotationInstance(Annotation annotationElement, ASTNode targetNode, Map<String, String> usedStatementParts) {
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
			annotationFamixType = resolveAnnotationType(annotationElement, targetNode, usedStatementParts);	
		}
		annotationInstance.setAnnotationType(annotationFamixType);
		
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
	
	
//	private AnnotationType resolveAnnotationType(Annotation annotationElement, Program rootAST) {
//		AnnotationType annotationFamixType = null;
//		ITypeBinding typeBinding = resolveAnnotationTypeBinding(annotationElement, rootAST);
//		
//		if (typeBinding != null) {
//			Type famixType = importer.ensureTypeFromTypeBinding(typeBinding);
//			if (famixType instanceof AnnotationType) {
//				annotationFamixType = (AnnotationType)famixType;
//			} else {
//				throw new RuntimeException("The type should always be an annotation type");
//			}
//		}
//		// If an annotation type is not found we create one in the unknown namespace, 
//		// if one is not already present there.
//		// As an alternative we could check if the annotation defines a namespace, and
//		// then create the unknow type in that namespace. However, it seems better to
//		// create unknows types only in the unknown namespace.
//		if (annotationFamixType == null) {
//			String annotationName = annotationElement.getAnnotationClass().getClassName();
//			annotationFamixType = ensureAnnotationTypeInNamespace(annotationName, importer.unknownNamespace());
//		}
//		return annotationFamixType;
//	}
	
	/**
	 * Computes the type binding for the given annotation element.
	 * <p>
	 * Normally, I should use the given type binding resolver to locate a type with the same name as the one of the annotation.
	 * Nonetheless, the API provided by BindingResolver is quite limited. All methods that could be used to resolve
	 * a type are declared protected. Because of this this method reimplements the logic of DefaultBindingResolver#resolveType.
	 * This require the creation if a BindingUtility object that us then used to locate the model element that has the same
	 * name as the class name in the annotation. If a model element can be found then a TypeBinding for that element is
	 * manually created. 
	 * 
	 * @param annotationElement the annotation element for which we want to resolve the type.
	 * @param rootAST the Program AST node in which the given annotation element is located.
	 * @return the type binding for the given annotation, or null if a binding cannot be computer.
	 */
//	private TypeBinding resolveAnnotationTypeBinding(Annotation annotationElement, Program rootAST) {
//		PerFileModelAccessCache modelAccessCache = new PerFileModelAccessCache(rootAST.getSourceModule());
//		BindingUtility bindingUtil = new BindingUtility(rootAST.getSourceModule(), modelAccessCache);
//		IModelElement[] modelElements;
//		try {
//			modelElements = bindingUtil.getModelElement(
//					annotationElement.getSourcePosition().startOffset,
//					annotationElement.getSourcePosition().length, 
//					modelAccessCache);
//		} catch (ModelException e) {
//			e.printStackTrace();
//			return null;
//		}
//		if (modelElements!= null && modelElements.length == 1) {
//			if (modelElements[0].getElementType() == IModelElement.TYPE) {
//				return new TypeBinding(rootAST.getAST().getBindingResolver(), PHPClassType.fromIType((IType)modelElements[0]), modelElements[0]);
//			}
//		}
//		return null;
//	}
	
	private AnnotationType ensureAnnotationTypeInNamespace(String entityName, Namespace namespace) {
		String qualifiedName = importer.makeTypeQualifiedNameFrom(namespace.getName(), entityName); 
		if (importer.types().has(qualifiedName))
			// If this cast fails then somehow an entity with the same name was created from somewhere else.
			// Normally this should not happen.
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
	
	private AnnotationType resolveAnnotationType(Annotation annotationElement, ASTNode targetNode, Map<String, String> usedStatementParts) {
		AnnotationType annotationFamixType = null;
		IType annotationType = detectAnotationTypeBinding(annotationElement.getAnnotationClass(), usedStatementParts);
		
		if (annotationType != null) {
			Type famixType = null;
			// Once we have a type we need to call #getTypeBinding in the BindingResolver to have access to the type binding.
			// However, that method is protected within the BindingResolver. Because of that we recreate here the actual
			// TypeBinding object, as we already have all the necessary information. 
			//ITypeBinding typeBinding = new TypeBinding(targetNode.getProgramRoot().getAST().getBindingResolver(), PHPClassType.fromIType(annotationType), annotationType);
			ITypeBinding typeBinding = importer.createTypeBinding(annotationType, targetNode.getProgramRoot().getAST().getBindingResolver());
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
	
	private IType detectAnotationTypeBinding(AnnotationClass node, Map<String, String> usedStatementParts) {
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
			if (usedStatementParts.containsKey(node.getClassName().toLowerCase())) {
				fullName = usedStatementParts.get(node.getClassName().toLowerCase());
			} else {
				if (annotationTagName.equals(node.getClassName())) {
					return null;
				}
				fullName = defaultAnnotationNamespaceName + NamespaceReference.NAMESPACE_SEPARATOR + node.getClassName();
				// fullName = node.getClassName();
			}
		} else {
			if (usedStatementParts.containsKey(node.getFirstNamespacePart().toLowerCase())) {
				fullName = usedStatementParts.get(node.getFirstNamespacePart().toLowerCase()) + NamespaceReference.NAMESPACE_SEPARATOR + node.getClassName();
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
