package com.feenk.pdt2famix.exporter.inphp;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.StringJoiner;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.dltk.core.DLTKCore;
import org.eclipse.dltk.core.IField;
import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IParent;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.index2.search.ISearchEngine.MatchRule;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.SearchEngine;
import org.eclipse.dltk.evaluation.types.SimpleType;
import org.eclipse.dltk.evaluation.types.UnknownType;
import org.eclipse.dltk.ti.types.ClassType;
import org.eclipse.dltk.ti.types.IEvaluatedType;
import org.eclipse.php.core.PHPVersion;
import org.eclipse.php.core.ast.nodes.AST;
import org.eclipse.php.core.ast.nodes.ASTNode;
import org.eclipse.php.core.ast.nodes.ASTParser;
import org.eclipse.php.core.ast.nodes.BindingResolver;
import org.eclipse.php.core.ast.nodes.Bindings;
import org.eclipse.php.core.ast.nodes.ClassDeclaration;
import org.eclipse.php.core.ast.nodes.Comment;
import org.eclipse.php.core.ast.nodes.Expression;
import org.eclipse.php.core.ast.nodes.FieldAccess;
import org.eclipse.php.core.ast.nodes.FormalParameter;
import org.eclipse.php.core.ast.nodes.IMethodBinding;
import org.eclipse.php.core.ast.nodes.ITypeBinding;
import org.eclipse.php.core.ast.nodes.IVariableBinding;
import org.eclipse.php.core.ast.nodes.Identifier;
import org.eclipse.php.core.ast.nodes.InterfaceDeclaration;
import org.eclipse.php.core.ast.nodes.MethodInvocation;
import org.eclipse.php.core.ast.nodes.NamespaceDeclaration;
import org.eclipse.php.core.ast.nodes.Program;
import org.eclipse.php.core.ast.nodes.Reference;
import org.eclipse.php.core.ast.nodes.ReflectionVariable;
import org.eclipse.php.core.ast.nodes.Scalar;
import org.eclipse.php.core.ast.nodes.SingleFieldDeclaration;
import org.eclipse.php.core.ast.nodes.StaticConstantAccess;
import org.eclipse.php.core.ast.nodes.StaticMethodInvocation;
import org.eclipse.php.core.ast.nodes.TraitDeclaration;
import org.eclipse.php.core.ast.nodes.TypeBinding;
import org.eclipse.php.core.ast.nodes.TypeDeclaration;
import org.eclipse.php.core.ast.nodes.Variable;
import org.eclipse.php.core.ast.nodes.VariableBase;
import org.eclipse.php.core.ast.nodes.VariableBinding;
import org.eclipse.php.core.ast.visitor.Visitor;
import org.eclipse.php.core.compiler.PHPFlags;
import org.eclipse.php.core.compiler.ast.nodes.NamespaceReference;
import org.eclipse.php.internal.core.model.PHPModelAccess;
import org.eclipse.php.internal.core.typeinference.PHPClassType;

import com.feenk.pdt2famix.exporter.Famix;
import com.feenk.pdt2famix.exporter.NamedEntityAccumulator;
import com.feenk.pdt2famix.exporter.model.famix.Access;
import com.feenk.pdt2famix.exporter.model.famix.AnnotationInstance;
import com.feenk.pdt2famix.exporter.model.famix.AnnotationType;
import com.feenk.pdt2famix.exporter.model.famix.AnnotationTypeAttribute;
import com.feenk.pdt2famix.exporter.model.famix.Attribute;
import com.feenk.pdt2famix.exporter.model.famix.ContainerEntity;
import com.feenk.pdt2famix.exporter.model.famix.Entity;
import com.feenk.pdt2famix.exporter.model.famix.FAMIXModel;
import com.feenk.pdt2famix.exporter.model.famix.IndexedFileAnchor;
import com.feenk.pdt2famix.exporter.model.famix.Inheritance;
import com.feenk.pdt2famix.exporter.model.famix.Invocation;
import com.feenk.pdt2famix.exporter.model.famix.JavaSourceLanguage;
import com.feenk.pdt2famix.exporter.model.famix.Method;
import com.feenk.pdt2famix.exporter.model.famix.NamedEntity;
import com.feenk.pdt2famix.exporter.model.famix.Namespace;
import com.feenk.pdt2famix.exporter.model.famix.Parameter;
import com.feenk.pdt2famix.exporter.model.famix.PrimitiveType;
import com.feenk.pdt2famix.exporter.model.famix.SourcedEntity;
import com.feenk.pdt2famix.exporter.model.famix.StructuralEntity;
import com.feenk.pdt2famix.exporter.model.famix.Trait;
import com.feenk.pdt2famix.exporter.model.famix.TraitUsage;
import com.feenk.pdt2famix.exporter.model.famix.Type;
import com.feenk.pdt2famix.exporter.model.famix.UnknownVariable;
import com.feenk.pdt2famix.exporter.model.java.JavaModel;

import ch.akuhn.fame.MetaRepository;
import ch.akuhn.fame.Repository;
import pdt2famix_exporter.Activator;

public class Importer {	
	private static final Activator logger = Activator.getDefault();
	 
	private static final char NAME_SEPARATOR = '$';
	private static final char NAMESPACE_SEPARATOR = NamespaceReference.NAMESPACE_SEPARATOR;
	public static final String INITIALIZER_NAME = "<init>";
	private static final String INITIALIZER_KIND = "initializer";
	public static final String CONSTRUCTOR_KIND = "constructor";
	public static final String CONSTRUCTOR_NAME = "__construct";
	public static final String DEFAULT_NAMESPACE_NAME = "";
	public static final String SYSTEM_NAMESPACE_NAME = "__SYSTEM__";
	public static final String UNKNOWN_NAME = "__UNKNOWN__";
	
	
	private AnnotationResolver annotationResolver;
	public AnnotationResolver annotationResolver() {
		return annotationResolver;
	}
	
	private Map<Entity,String> entitiesToKeys = new HashMap<>();
	
	private Namespace systemNamespace;
	private Namespace unknownNamespace;
	private Type unknownType;
	private UnknownVariable unknownVariable;
	
	private Repository repository;
	public Repository repository() { return repository; }
	
	private NamedEntityAccumulator<Namespace> namespaces;
	public NamedEntityAccumulator<Namespace> namespaces() {return namespaces;}
	
	private NamedEntityAccumulator<Type> types; 
	public NamedEntityAccumulator<Type> types() {return types;}

	private NamedEntityAccumulator<Method> methods;
	public NamedEntityAccumulator<Method> methods() {return methods;}

	private NamedEntityAccumulator<Attribute> attributes;
	public NamedEntityAccumulator<Attribute> attributes() {return attributes;}
	
	private NamedEntityAccumulator<Parameter> parameters;
	public NamedEntityAccumulator<Parameter> parameters() {return parameters;}
	
	private ISourceModule currentSourceModel;
	public ISourceModule getCurrentSourceModel() {return currentSourceModel;}
	public void setCurrentSourceModel(ISourceModule currentSourceModel) {this.currentSourceModel = currentSourceModel;}
	
	public String getCurrentFilePath() {
		// Remove the first segment in order to remove the name of the folder containing the project.
		return getCurrentSourceModel().getPath().makeRelative().removeFirstSegments(1).toString(); }
	
	public Collection currentInvocations() {
		return (Collection) repository().getElements().stream()
			.filter(entity -> entity instanceof Invocation)
			.collect(Collectors.toList());
	}
	
	public Collection currentAccesses() {
		return (Collection) repository().getElements().stream()
			.filter(entity -> entity instanceof Access)
			.collect(Collectors.toList());
	}
	
	public Collection currentTraitUsages() {
		return (Collection) repository().getElements().stream()
			.filter(entity -> entity instanceof TraitUsage)
			.collect(Collectors.toList());
	}
		
	/**
	 * This is a structure that keeps track of the current stack of containers
	 * It is particularly useful when we deal with inner or anonymous classes
	 */ 
	private Deque<ContainerEntity> containerStack = new ArrayDeque<ContainerEntity>();
	public void pushOnContainerStack(ContainerEntity namespace) {this.containerStack.push(namespace);}
	public ContainerEntity popFromContainerStack() {return this.containerStack.pop();}
	public ContainerEntity topOfContainerStack() {return this.containerStack.peek();}
	@SuppressWarnings("unchecked")
	public <T> T topFromContainerStack(java.lang.Class<T> clazz) { 
		for (Iterator<ContainerEntity> iterator = containerStack.iterator(); iterator.hasNext();) {
			ContainerEntity next = iterator.next();
			if (clazz.isInstance(next)) return (T) next;
		}
		return null;
	}
	public void containerStackForEach(Consumer<? super ContainerEntity> action) {
		containerStack.stream().forEachOrdered(action);
	}
	public ContainerEntity topOfContainerStackOrDefaultNamespace() {
		if (this.containerStack.isEmpty()==false) {
			return topOfContainerStack();
		}
		return ensureNamespaceNamed(DEFAULT_NAMESPACE_NAME);
	}
	
	
	private IScriptProject scriptProject;
	public IScriptProject getScriptProject() {
		return scriptProject;
	}
	
	public Importer(IScriptProject projectPHP, AnnotationResolver annotationResolver) {
		MetaRepository metaRepository = new MetaRepository();
		FAMIXModel.importInto(metaRepository);
		JavaModel.importInto(metaRepository);
		repository = new Repository(metaRepository);
		repository.add(new JavaSourceLanguage());
		
		namespaces = new NamedEntityAccumulator<Namespace>(repository);
		types = new NamedEntityAccumulator<Type>(repository);
		methods = new NamedEntityAccumulator<Method>(repository);
		attributes = new NamedEntityAccumulator<Attribute>(repository);
		parameters = new NamedEntityAccumulator<Parameter>(repository);
		
		scriptProject = projectPHP; 
		this.annotationResolver = annotationResolver;
		this.annotationResolver.setImporter(this);
	}
	
	//NAMESPACE
	
	public Namespace ensureNamespaceFromNamespaceDeclaration(NamespaceDeclaration declaration) {
//		String sourceIdentifier = getCurrentSourceModel().getHandleIdentifier();
		String namespaceQualifiedName = declaration.getName() == null ? DEFAULT_NAMESPACE_NAME : declaration.getName().getName();
		return ensureNamespaceNamed(namespaceQualifiedName);
	}
	
	public Namespace ensureNamespaceNamed(String namespaceQualifiedName) {
		if (namespaces.has(namespaceQualifiedName)) 
			return namespaces.named(namespaceQualifiedName);
		else
			return namespaces.add(namespaceQualifiedName, createNamespaceNamed(namespaceQualifiedName));
	}
	
	private Namespace createNamespaceNamed(String qualifiedName) {
		int lastIndexOfSeparator = qualifiedName.lastIndexOf(NAMESPACE_SEPARATOR);
		Namespace namespace = new Namespace();
		namespace.setIsStub(true);
		if (lastIndexOfSeparator <= 0)
			namespace.setName(qualifiedName);
		else {
			/* PHP namespaces are not nested, even though they look like they are.
			 * But, in Famix, namespaces are nested. So we create nesting based on the namespace separator
			 */
			namespace.setName(qualifiedName.substring(lastIndexOfSeparator+1));
			Namespace parentNamespace = ensureNamespaceNamed(qualifiedName.substring(0, lastIndexOfSeparator));
			namespace.setParentScope(parentNamespace);
		}
		return namespace;
	}
	
	public Namespace unknownNamespace() {
		if (unknownNamespace == null) {
			unknownNamespace = new Namespace();
			unknownNamespace.setName(UNKNOWN_NAME);
			unknownNamespace.setIsStub(true);
			namespaces.add(Famix.qualifiedNameOf(unknownNamespace), unknownNamespace); //TODO: this uses the java qualified like name
		}
		return unknownNamespace;
	}
	
	public Namespace systemNamespace() {
		if (systemNamespace == null) {
			systemNamespace = new Namespace();
			systemNamespace.setName(SYSTEM_NAMESPACE_NAME);
			systemNamespace.setIsStub(true);
			namespaces.add(Famix.qualifiedNameOf(systemNamespace), systemNamespace); //TODO: this uses the java qualified like name
		}
		return systemNamespace;
	}
	
	// TYPE
	
	private Type protectedEnsureTypeFromTypeBinding(ITypeBinding binding, String debugData) {
		if (binding == null) {
			logNullBinding("type binding resolving", debugData);
			return unknownType();
		}
		if (isValidTypeBinding(binding) == false) {
			logInvalidBinding("type binding resolving", debugData);
			return unknownType();
		}
		return ensureTypeFromTypeBinding(binding);
	}
	
	public boolean isValidTypeBinding(ITypeBinding binding) {
		if ((binding.getPHPElement() != null && binding.getPHPElement() == currentSourceModel)) {
			return false;
		}
		return true;
	}
	
	public Type ensureTypeFromTypeBinding(ITypeBinding binding) {
		return ensureTypeFromTypeBinding(binding, null);
	}
	
	public Type ensureTypeFromTypeBinding(ITypeBinding binding, TypeDeclaration typeDeclaration) {
		// The implementation of #isUnknown is broken in the binging. 
		// It should be an unknown binding also if the associated array 
		// of elements has size 0 (now only if it's null)
		if (binding.isUnknown() || (binding.getPHPElements().length == 0 && !(binding.isPrimitive() || binding.isNullType() || binding.isArray())) ) {
			return unknownType();
		} 
		
		String qualifiedName = getQualifiedNameForTypeBinding(binding); 
		if (qualifiedName == null) {
			return unknownType();
			//throw new RuntimeException("Handle the case of unknown types");
		} 
		if (types.has(qualifiedName)) { 
			return types.named(qualifiedName); };
			
		ASTNode astNode = typeDeclaration;
		if (astNode == null && binding.getPHPElement() instanceof IType) {
			ASTParser parser = ASTParser.newParser(PHPVersion.PHP7_1, ((IType)binding.getPHPElement()).getSourceModule());
			try {
				Program program = parser.createAST(null);
				astNode = program.findDeclaringNode(binding);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
			
		// Before creating the type of the entity we need to determine if it's an annotation type.
		// The convention for determining when a type is an annotation can differ from framework to framework.
		// Currently we will assume the Doctrine convention and consider that an type is an anotation type
		// if it has @Annotation annotation in the comment, regardless of what the Annotation type resolves to.
		// To do this we extract here the actual annotations from the comment, and set the to the type
		// after we create it.
		List<AnnotationInstance> annotationInstances = new ArrayList<>();	
		com.feenk.pdt2famix.exporter.model.famix.Comment famixComment = null;
		if (astNode != null) {
			famixComment = extractCommentFromASTNode(astNode);
			annotationInstances = annotationResolver.extractAnnotationInstancesFromComment(
					getSourceFromFamixComment(famixComment, astNode.getProgramRoot()), 
					astNode);
		}
		
		Type famixType = createTypeFromTypeBinding(binding, annotationInstances);
		if (binding.getPHPElement() != null) {
			// For classes that are in a namespace the name returned by the binding also includes the namespace.
			famixType.setName(binding.getPHPElement().getElementName());
		} else {
			famixType.setName(binding.getName());
		}
		
		if (famixComment != null) {
			famixType.addComments(famixComment);
			repository.add(famixComment);
		}
		famixType.setAnnotationInstances(annotationInstances);
		
		types.add(qualifiedName, famixType);
		entitiesToKeys.put(famixType, qualifiedName);
		famixType.setIsStub(true);		
		
		//extractBasicModifiersFromBinding(binding.getModifiers(), type);
		famixType.setContainer(ensureContainerEntityForTypeBinding(binding));
		if (binding.getSuperclass() != null) 
			createInheritanceFromSubtypeToSuperTypeBinding(famixType, binding.getSuperclass());
		for (ITypeBinding interfaceBinding : binding.getInterfaces()) {
			createInheritanceFromSubtypeToSuperTypeBinding(famixType, interfaceBinding);
		}
		return famixType;
	}
	
	private Type createTypeFromTypeBinding(ITypeBinding binding, Collection<AnnotationInstance> annotations) {
		//TODO: binding.isAmbiguous()
		//TODO: binding.isNullType()
		
		if (binding.isPrimitive() || binding.isNullType())
			return new PrimitiveType();
		
		if (binding.isArray())
			// For now arrays are just primitive types
			// Ideally we should the the binding return by #getElementType();
			return new PrimitiveType();
			//return createTypeFromTypeBinding(binding.getElementType());
		
		if (binding.isTrait()) {
			return new Trait();
		}
		
		if (annotations.stream().anyMatch(annotation -> annotationResolver.isMainAnnotationTag(annotation))) {
			return new AnnotationType();
		};
		
		com.feenk.pdt2famix.exporter.model.famix.Class clazz = new com.feenk.pdt2famix.exporter.model.famix.Class();
		clazz.setIsInterface(binding.isInterface());
		return clazz;
	}
	
	private ContainerEntity ensureContainerEntityForTypeBinding(ITypeBinding binding) {
		IMember declaringType;
		
		if (binding.getPHPElement() == null) {
			// If the PHP element is nill but we have a primitive type we use the system namespace as the container.
			if (binding.isPrimitive() || binding.isNullType() || binding.isArray()) {
				return systemNamespace();
			}
			throw new RuntimeException("Update the detection of the parent");
		}
		
		declaringType = ((IMember)binding.getPHPElement()).getDeclaringType();		
		if (declaringType != null) {
			boolean isNamespace = false;
			try {
				isNamespace = PHPFlags.isNamespace(declaringType.getFlags());
			} catch (ModelException e) {
				e.printStackTrace();
			}
			if (isNamespace) {
				return ensureNamespaceNamed(declaringType.getElementName());
			}
		} else {
			return ensureNamespaceNamed(DEFAULT_NAMESPACE_NAME);
		}
		
		throw new RuntimeException("Update the detection of the parent");
	}
	
	/**
	 * This is the type we used as a null object whenever we need to reference a type  
	 */
	public Type unknownType() {
		if (unknownType == null) {
			unknownType = ensureTypeNamedInUnknownNamespace(UNKNOWN_NAME);
		}
		return unknownType;
	}
	
	private Type ensureTypeNamedInUnknownNamespace(String name) {
		Type type = createTypeNamedInUnknownNamespace(name);
		String qualifiedName = Famix.qualifiedNameOf(type); //TODO: this uses the java qualified like name
		if (types.has(qualifiedName))
			return types.named(qualifiedName);
		else {
			types.add(Famix.qualifiedNameOf(type), type);
			return type;
		}
	}

	private Type createTypeNamedInUnknownNamespace(String name) {
		Type type = new Type();
		type.setName(name);
		type.setContainer(unknownNamespace());
		type.setIsStub(true);
		return type;
	}
	
	
	// INHERITANCE
	
	/**
	 * We use this one when we have the super type binding
	 */
	private Inheritance createInheritanceFromSubtypeToSuperTypeBinding(Type famixSubType, ITypeBinding superBinding) {
		return createInheritanceFromSubtypeToSuperType(famixSubType, protectedEnsureTypeFromTypeBinding(superBinding, "Resolve superclass or interface"));
	}

	/**
	 * We use this one when we have the super type
	 */
	private Inheritance createInheritanceFromSubtypeToSuperType(Type subType, Type superType) {
		Inheritance inheritance = new Inheritance();
		inheritance.setSuperclass(superType);
		inheritance.setSubclass(subType);
		repository.add(inheritance);
		return inheritance;
	}
	
	// TRAIT USAGE
	
	public void addTraitUsageToCurrentContainerForTraitBinding(ITypeBinding traitBinding) {
		TraitUsage traitUsage = new TraitUsage();
		Type parent = (Type) topOfContainerStack();
		Trait famixTrait = (Trait) ensureTypeFromTypeBinding(traitBinding);
		
		traitUsage.setTrait(famixTrait);
		traitUsage.setUser(parent);
		
		repository.add(traitUsage);
	}
	
	// METHOD
	
	public Method ensureMethodFromMethodBindingToCurrentContainer(IMethodBinding methodBinding) {
		return ensureMethodFromMethodBinding(methodBinding, (Type) topOfContainerStack());
	}
	
	public Method ensureMethodFromMethodBinding(IMethodBinding methodBinding, BindingResolver bindingResolver) {
		/*	JDT2FAMIX: binding.getDeclaringClass() might be null when you invoke a method from a class that is not in the path
			It looks like calling getMethodDeclaration is more robust. */
		/* In PDT  getMethodDeclaration() does not exist in the method binding /*/
		
		ITypeBinding classBinding = methodBinding.getDeclaringClass();
		Type famixClassType = null;
		if (classBinding == null || (classBinding.getPHPElement() != null && classBinding.getPHPElement() == this.currentSourceModel) ) {
			// Sometimes in the parser the type containing a library method 
			// does not resolve correcly: if we resolve the binding of the type we 
			// get the current source module.
			// If we encounter that then we just use the unknown type.
			
			IModelElement parentModelElement = methodBinding.getPHPElement().getParent();
			if (parentModelElement.getElementType() == IModelElement.TYPE) {
				ITypeBinding computedClassBinding = createTypeBinding((IType)parentModelElement, bindingResolver);
				famixClassType = ensureTypeFromTypeBinding(computedClassBinding);
			} 
			if (famixClassType == null) {
				logNullBinding("Method binding", methodBinding.getName());
				famixClassType = unknownType();
			}
		} else {
			famixClassType = ensureTypeFromTypeBinding(classBinding);
		}
		
		return ensureMethodFromMethodBinding(methodBinding, famixClassType);
	}
	
	public Method ensureConstructorForTypeBinding(ITypeBinding constructorClassTypeBinding, BindingResolver bindingResolver) {		
		Type classType = safeEnsureTypeFromTypeBinding(constructorClassTypeBinding, bindingResolver, "Constructor type");
		String constructorKey = formMethodKeyFrom(constructorClassTypeBinding.getKey(), CONSTRUCTOR_NAME);
		
		Method constructorMethod = ensureBasicMethod(
				CONSTRUCTOR_NAME, 
				constructorKey, 
				CONSTRUCTOR_NAME+"()", 
				classType, 
				famixMethod -> setUpImplicitConstructorMethod(famixMethod));
		return constructorMethod;
	}
	
	private void setUpImplicitConstructorMethod(Method famixMethod) {
		famixMethod.setKind(CONSTRUCTOR_KIND);
		famixMethod.addModifiers("public");
	}
	
	public Method ensureInitializerMethod() {
		String parentTypeKey = this.entitiesToKeys.get(topOfContainerStack());
		return ensureBasicMethod(
				INITIALIZER_NAME, 
				formMethodKeyFrom(parentTypeKey, INITIALIZER_NAME),
				null, 
				(Type) topOfContainerStack(),
				famixMethod -> setUpInitializerMethod(famixMethod));
	}
	
	private void setUpInitializerMethod(Method method) {
		method.setKind(INITIALIZER_KIND);
		method.setIsStub(false);
	}
	
	private String formMethodKeyFrom(String typeKey, String methodName) {
		return typeKey + "~" + methodName;
	}
	
	private Method ensureMethodFromMethodBinding(IMethodBinding methodBinding, Type parentType) {
		String methodName = methodBinding.getName();
		
//		// For library methods the only way to get the actual signature is to obtain the actual AST nodes.
//		// To do need to obtain the actual AST.
//		// Before doing this we should check if the method is actually a stub: is the path of the container file in
//		// a path that would be handled by the parser. If yes the AST will anyway be visited at a certain point so we do 
//		// not have to extract here the signature.
//		ASTNode astNode = null;
//		ASTParser parser = ASTParser.newParser(PHPVersion.PHP7_1, ((IMethod)methodBinding.getPHPElement()).getSourceModule());
//		try {
//			Program program = parser.createAST(null);
//			astNode = program.findDeclaringNode(methodBinding);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		return ensureBasicMethod(
				methodName,  
				methodBinding.getKey(),
				null,
				parentType,
				famixMethod -> setUpMethodFromMethodBinding(famixMethod, methodBinding));
	}
	
	private void appendTypeToMethodSignature(Type famixParameterType, StringJoiner signatureJoiner) {
		if (famixParameterType.getContainer().getName().equals(Importer.DEFAULT_NAMESPACE_NAME)) {
			signatureJoiner.add(famixParameterType.getName());
		} else {
			signatureJoiner.add(Famix.qualifiedNameOf(famixParameterType));
		}
	}
	
	private void setUpMethodFromMethodBinding(Method method, IMethodBinding binding) {
		if (binding.isConstructor()) 
			method.setKind(CONSTRUCTOR_KIND);
		
		ITypeBinding[] returnTypes = binding.getReturnType();
		
//		if ((returnType != null) && !(returnType.isPrimitive() && returnType.getName().equals("void")))
//			//we do not want to set void as a return type
//			method.setDeclaredType(ensureTypeFromTypeBinding(returnType));
		
		extractBasicModifiersFromBinding(binding.getModifiers(), method);
		if (PHPFlags.isStatic(binding.getModifiers()))
			method.setHasClassScope(true);
//		try {
//			IAnnotationBinding[] annotations = binding.getAnnotations();
//			createAnnotationInstancesToEntityFromAnnotationBinding(method, annotations);
//		} catch(NullPointerException e) {
//			/* This happens in some very strange circumstances, likely due to missing dependencies.
//			 * The only solution I found was to catch the exception and log it and provide people
//			 * with a way to solve it by adding the missing dependencies to the import.
//			 */
//			logNullBinding("annotation instances for method binding", Famix.qualifiedNameOf(method) , -1);
//		}
	}
	
	private Method ensureBasicMethod(String methodName, String identifierKey, String signature, Type parentType, Consumer<Method> ifAbsent) {
		String qualifiedName = identifierKey;
		if(methods.has(qualifiedName))
			return methods.named(qualifiedName);
		Method method = new Method();
		method.setName(methodName);
		methods.add(qualifiedName, method);
		method.setSignature(signature);
		method.setIsStub(true);
		method.setParentType(parentType);
		ifAbsent.accept(method);
		return method;
	}
	
	// PARAMETERS 
		
	public Parameter parameterFromFormalParameterDeclaration(FormalParameter formalParameter, Method famixMethod, String methodIdentifier) {
		String name = getNameFromExpression(formalParameter.getParameterName());
	
		// TODO: check to see how to properly define this.
		String qualifiedName = methodIdentifier + "~~~~" + name;
		
		if (parameters.has(qualifiedName)) 
			return parameters.named(qualifiedName);
		Parameter parameter = new Parameter();
		parameters.add(qualifiedName, parameter);
		
		parameter.setName(name);
		parameter.setParentBehaviouralEntity(famixMethod);
		
		List<Type> possibleTypes = new ArrayList<Type>();
		ITypeBinding inferredTypeBinding = formalParameter.getParameterNameIdentifier().resolveTypeBinding();
		Expression declatedType = formalParameter.getParameterType();
		Expression defaultValue = formalParameter.getDefaultValue();
		BindingResolver bindingResolver = formalParameter.getAST().getBindingResolver();
		
		if (inferredTypeBinding != null) {
			possibleTypes.add(safeEnsureTypeFromTypeBinding(inferredTypeBinding, bindingResolver, "Parameter type "+qualifiedName));
		}
		if (declatedType != null) {
			ITypeBinding resolvedTypeBinding = declatedType.resolveTypeBinding();
			if (resolvedTypeBinding != null) {
				possibleTypes.add(safeEnsureTypeFromTypeBinding(resolvedTypeBinding, bindingResolver, "Parameter resolved type "+qualifiedName));
			}	
		}
		if (defaultValue != null) {
			ITypeBinding resolvedTypeBinding = defaultValue.resolveTypeBinding();
			if (resolvedTypeBinding != null) {
				possibleTypes.add(safeEnsureTypeFromTypeBinding(resolvedTypeBinding, bindingResolver, "Parameter default type "+qualifiedName));
			}
		}
		
		Optional<Type> parameterType = possibleTypes.stream().sequential()
			.filter(aType -> aType != null && isUnknowFAMIXType(aType) == false )
			.findFirst();
		if (parameterType.isPresent()) {
			parameter.setDeclaredType(parameterType.get());
		}
		return parameter;
	}
	
	private Type safeEnsureTypeFromTypeBinding(ITypeBinding binding, BindingResolver bindingResolver, String debugData) {
		
		if (binding == null) {
			logNullBinding("type binding resolving: ", debugData);
			return unknownType();
		}
		
		if (isValidTypeBinding(binding) == false) {
			IEvaluatedType evaluatedType = binding.getEvaluatedType();
			if (evaluatedType instanceof UnknownType) {
				logInvalidBinding("type binding resolving [unknown]: ", debugData);
				return unknownType();
			}
			
			// Check if we have a primitive type.
			if (evaluatedType instanceof SimpleType || evaluatedType.getTypeName().equals("array") || evaluatedType.getTypeName().equals("null")) {
				String qualifiedName;
				Type famixType = null;

				if (binding.isArray() || evaluatedType.getTypeName().equals("array")) {
					qualifiedName = "array";
				} else {
					qualifiedName = binding.getEvaluatedType().getTypeName();
				}
				// Manually set the type here
				if (types.has(qualifiedName)) { 
					return types.named(qualifiedName); };
				
				famixType = new PrimitiveType();
				famixType.setName(qualifiedName);
				famixType.setIsStub(true);	
				famixType.setContainer(systemNamespace());
				
				types.add(qualifiedName, famixType);
				entitiesToKeys.put(famixType, qualifiedName);
				return famixType;
			} else if (evaluatedType instanceof ClassType) {
				ITypeBinding computedTypeBinding = createTypeBinding(evaluatedType.getTypeName(), bindingResolver);
				if (computedTypeBinding != null) {
					return ensureTypeFromTypeBinding(computedTypeBinding);
				}
			} 
			//TODO: handle MultiTypeType
			
			logInvalidBinding("type binding resolving [invalid]: ", debugData);
			return unknownType();
		}
		
		return ensureTypeFromTypeBinding(binding);
	}
	
	private boolean isUnknowFAMIXType(Type type) {
		return unknownType != null && type == unknownType;
	}
	
	public Parameter ensureParameterWithinCurrentMethodFromVariableBinding(IVariableBinding binding) {
		Method method = (Method) topOfContainerStack();
		Optional<Parameter> possibleParameter = method.getParameters()
			.stream()
			.filter(p -> p.getName().equals(binding.getName()))
			.findAny();
		if (possibleParameter.isPresent())
			return possibleParameter.get();
		return null;
	}
	
	// ATTRIBUTE - FIELD
	
	public Attribute ensureAttributeForFieldDeclaration(SingleFieldDeclaration fieldDeclaration) {
		IVariableBinding variableBinding = fieldDeclaration.getName().resolveVariableBinding();
		Attribute attribute;

		// If the variable binding obtained directly from the field declaration is null we attept 
		// to recover it by navigating to the type declaration node in the AST, and using the
		// type declaration to locate the actual field. A type declaration can be a class, trait
		// or interface.
		if (variableBinding == null) {
			String fieldName = getNameFromExpression(fieldDeclaration.getName());
			ASTNode typeDeclarationNode = fieldDeclaration
					.getParent()  // FieldsDeclaration node
					.getParent()  // Body node for the class
					.getParent(); // The type declaration node.
			if (typeDeclarationNode instanceof TraitDeclaration) {
				// We implement here a manual search as using findFieldInHierarchy() does not take into account interfaces.
				ITypeBinding traitBinding = ((TraitDeclaration)typeDeclarationNode).resolveTypeBinding();
				IVariableBinding[] traitFields = getDeclaredFieldsInTrait(traitBinding, fieldDeclaration.getAST().getBindingResolver());
				for (int i = 0; i < traitFields.length; i++) {
					IVariableBinding field = traitFields[i];
					if (field.getName().equals(fieldName)) {
						variableBinding = field;
						break;
					}
				}
			} else if (typeDeclarationNode instanceof ClassDeclaration || typeDeclarationNode instanceof InterfaceDeclaration) {
				ITypeBinding typeBinding = ((TypeDeclaration)typeDeclarationNode).resolveTypeBinding();
				variableBinding = Bindings.findFieldInHierarchy(typeBinding, fieldName);
			}
		}
		
		if (variableBinding == null) {
			// attribute = ensureAttributeFromFieldDeclarationIntoParentType(fieldDeclaration);
			throw new RuntimeException("Does it ever get here?");
		} else {
			attribute = ensureAttributeForVariableBinding(variableBinding, fieldDeclaration.getAST().getBindingResolver());
		}
		
		// When the type of an attribute is computed from a type binding, resolving the type of the attribute
		// seems not to work (getType()). Because of that in case the attribute has an unknow type or no type,
		// we resolve the type from the initial value assigned to the attribute.
		Type declaredType = null;
		if ( (attribute.getDeclaredType() == null || isUnknowFAMIXType(attribute.getDeclaredType())) && fieldDeclaration.getValue() != null) {
			ITypeBinding resolvedTypeBinding = fieldDeclaration.getValue().resolveTypeBinding();
			// Sometimes in the parser the code "public $list = [];" or "private $languageCode = 'EN';" 
			// does not resolve correcly the type of the value. If we resolve the binding of the type we 
			// get the current source module. To avoid errors we use the protected call.
			declaredType = safeEnsureTypeFromTypeBinding(resolvedTypeBinding, fieldDeclaration.getAST().getBindingResolver(), "Field declaration");
		}
		attribute.setDeclaredType(declaredType); 
		attribute.setIsStub(true);
		
		return attribute;
	}
	
	private IVariableBinding[] getDeclaredFieldsInTrait(ITypeBinding traitBinding, BindingResolver resolver) {
		List<IVariableBinding> variableBindings = new ArrayList<>();

		for (IModelElement element : traitBinding.getPHPElements()) {
			IType type = (IType) element;
			try {
				IField[] fields = type.getFields();
				for (int i = 0; i < fields.length; i++) {
					// TODO: open bug report.
					// The method getVariableBinding() is private in the resolver.
					// To overcome this we explicitly create a variable binding instead
					// of delegating to the resolver.
					IVariableBinding variableBinding = null;
					if (fields[i] != null) {
						variableBinding = new VariableBinding(resolver, fields[i]);
					}
					if (variableBinding != null) {
						variableBindings.add(variableBinding);
					}
				}
			} catch (ModelException e) {
				if (DLTKCore.DEBUG) {
					e.printStackTrace();
				}
			}
		}
		return variableBindings.toArray(new IVariableBinding[variableBindings.size()]);
	}
	
	public Attribute ensureAttributeFromFieldDeclarationIntoParentType(SingleFieldDeclaration fieldDeclaration) {
		Type parentType = this.topFromContainerStack(Type.class);
		String name = getNameFromExpression(fieldDeclaration.getName());
		String qualifiedName = entitiesToKeys.get(parentType)+"^"+name;
		
		if (attributes.has(qualifiedName)) {
			return attributes.named(qualifiedName);
		} else {
			return ensureBasicAttribute(parentType, name, qualifiedName, null);
		}		
	}
	
	// ATRRIBUTE - CONSTANTS
	
	/**
	 * Resolve the binding for the given constant. Right now the implementation of this method does not
	 * use a variable binding as there is no direct way to obtain the variable binding from the constant.
	 * 
	 * This means that if a constant will be accessed before its declaration is visited, it will be resolved
	 * using its variable binding. This implementation need to make sure it resolves the constant the same way.
	 * 
	 * A future solution can be to find the type declaration in the AST and from the type declaration get
	 * a type binding and use that type binding to obtain a variable binding for the constant.
	 */
	public Attribute ensureConstant(Identifier identifier, Expression expression, int modifier) {
		Type parentType = this.topFromContainerStack(Type.class);
		String qualifiedName = entitiesToKeys.get(parentType)+"^"+identifier.getName();
		
		if (attributes.has(qualifiedName)) {
			return attributes.named(qualifiedName);
		}
		
		Attribute attribute = ensureBasicAttribute(parentType, identifier.getName(), qualifiedName, null);
		extractBasicModifiersFromBinding(modifier, attribute);
		attribute.setHasClassScope(true);
		// The modifier returned by the constant declaration node seems to not contain a modifier for const.
		if (attribute.getModifiers().contains("const") == false) {
			attribute.addModifiers("const");
		}
		
		if (expression != null) {
			ITypeBinding resolvedTypeBinding = expression.resolveTypeBinding();
			attribute.setDeclaredType(safeEnsureTypeFromTypeBinding(resolvedTypeBinding, identifier.getAST().getBindingResolver(), "constant value")); 
		}
		
		return attribute;
	}
	
	/**
	 * 
	 * @param variableBinding Must not be null.
	 * 
	 * @return the Famix attribute associated to the given variable binding.
	 */
	private Attribute ensureAttributeForVariableBinding(IVariableBinding variableBinding, BindingResolver bindingResolver) {
		String qualifiedName = variableBinding.getKey();
		if (attributes.has(qualifiedName)) 
			return attributes.named(qualifiedName);
		
		String name = variableBinding.getName();
		ITypeBinding parentTypeBinding = variableBinding.getDeclaringClass();
		Type parentType;
		if (parentTypeBinding == null)
			parentType = unknownType();
		else 
			parentType = safeEnsureTypeFromTypeBinding(parentTypeBinding, bindingResolver, "Variable type binding");
		
		Type attributeType = null;
		if (variableBinding.getType() != null)  { 
			attributeType = ensureTypeFromTypeBinding(variableBinding.getType()) ;
		} 
		
		Attribute attribute = ensureBasicAttribute(parentType, name, qualifiedName, attributeType);
		extractBasicModifiersFromBinding(variableBinding.getModifiers(), attribute);
		if (PHPFlags.isStatic(variableBinding.getModifiers()) || PHPFlags.isConstant(variableBinding.getModifiers()))
			attribute.setHasClassScope(true);
		
		return attribute;
	}
	
	private Attribute ensureBasicAttribute(Type parentType, String name,
			String qualifiedName, Type declaredType) {
		Attribute attribute;
		if (parentType instanceof AnnotationType) {
			attribute = new AnnotationTypeAttribute();
		} else {
			attribute = new Attribute();
		}
		attribute.setName(name);
		attribute.setParentType(parentType);
		attribute.setDeclaredType(declaredType);
		attributes.add(qualifiedName, attribute);
		return attribute;
	}
	
	// INVOCATIONS
	
	public Invocation createInvocationFromMethodInvocation(MethodInvocation methodInvocation) {	
		IMethodBinding methodBinding = methodInvocation.resolveMethodBinding();
		Invocation invocation = createInvocationFromMethodBinding(methodBinding, methodInvocation);
				
		if (methodBinding != null) {
			Expression dispatcherExpression = methodInvocation.getDispatcher();
			if (dispatcherExpression != null) {
				invocation.setReceiver(ensureStructuralEntityFromExpression(dispatcherExpression));
			}
		}

		return invocation;
	}
	
	public Invocation createInvocationFromMethodInvocation(StaticMethodInvocation methodInvocation) {	
		IMethodBinding methodBinding = methodInvocation.resolveMethodBinding();
		Invocation invocation = createInvocationFromMethodBinding(methodBinding, methodInvocation);
		
		if (methodBinding != null) {
			Expression dispatcherExpression = methodInvocation.getClassName();
			if (dispatcherExpression != null) {
				invocation.setReceiver(ensureStructuralEntityFromExpression(dispatcherExpression));
			}
		}
							 
		repository.add(invocation);
		return invocation;
	}
	
	public Invocation createInvocationFromMethodBinding(IMethodBinding methodBinding, ASTNode invocationNode) {		
		Invocation invocation = new Invocation();
		invocation.setSender((Method) topOfContainerStack()); 
		if (methodBinding != null)
			invocation.addCandidates(ensureMethodFromMethodBinding(methodBinding, invocationNode.getAST().getBindingResolver()));  
		invocation.setSignature(computeSignatureFromInvocatioNode(invocationNode));
		repository.add(invocation);
		return invocation;
	}
	
	public Invocation createInvocationToFamixMethod(Method invokedMethod, ASTNode invocationNode) {
		Invocation invocation = new Invocation();
		invocation.setSender((Method) topOfContainerStack()); 
		invocation.addCandidates(invokedMethod); 
		invocation.setSignature(computeSignatureFromInvocatioNode(invocationNode));
		repository.add(invocation);
		return invocation;
	}
	
	private String computeSignatureFromInvocatioNode(ASTNode invocationNode) {
		try {
			return invocationNode.getProgramRoot().getSourceModule().getSource().substring(
					invocationNode.getStart(),
					invocationNode.getEnd());
		} catch (ModelException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public StructuralEntity ensureStructuralEntityFromExpression(Expression expression) {
		IVariableBinding variableBinding = null;
		if (expression.getClass().equals(Variable.class)) {
			variableBinding = ((Variable) expression).resolveVariableBinding();
		} else if (expression instanceof FieldAccess) {
			// For FieldAccess we need to make sure the type containing the field can be resolved.
			// If the type cannot be resolved then resolveFieldBinding() will throw an exception.
			ITypeBinding contaierTypeBinding = ((FieldAccess) expression).getDispatcher().resolveTypeBinding();
			if (contaierTypeBinding == null) {
				logNullBinding("Field access", this.getNameFromExpression(((FieldAccess) expression).getField()));
				return null;
			}
			// If the binding for the type containing the field is invalid use a custom solution for creating the variable binding.
			if (contaierTypeBinding.getPHPElement() == this.currentSourceModel) {
				variableBinding = computeVariableBindingFromInvalidParentBinding((FieldAccess) expression, contaierTypeBinding);
			} else {
				variableBinding = ((FieldAccess) expression).resolveFieldBinding();
			}
		}
		
		if (variableBinding != null) {
			if (variableBinding.isField())
				return ensureAttributeForVariableBinding(variableBinding, expression.getAST().getBindingResolver());
			if (variableBinding.isParameter() || (variableBinding.isLocal() && isVariableMethodParameter(variableBinding) ) )
				return ensureParameterWithinCurrentMethodFromVariableBinding(variableBinding);
//			if (variableBinding.isLocal()) 
//				return null;
		}
		return null;
	}
	
	/**
	 * For field bindings where the type binding containing the field is invalid attemp to compute a variable binding
	 * using the evaluated type associated with the type binding. 
	 */
	private IVariableBinding computeVariableBindingFromInvalidParentBinding(FieldAccess fieldAccess, ITypeBinding fieldParent) {
		ITypeBinding computedTypeBinding = createTypeBinding(
				fieldParent.getEvaluatedType().getTypeName(), 
				fieldAccess.getAST().getBindingResolver());
		if (computedTypeBinding != null) {
			// Use a custom resolveField method instead of callinf #resolveFieldBinding() on the FieldAccess.
			return resolveField((FieldAccess) fieldAccess, computedTypeBinding);
		}
		logInvalidBinding("Field access", getNameFromExpression(fieldAccess.getField()));
		return null;
	}
	
	/**
	 * I am a copy of the #resolveField() method from the BindingResolver, that instead of using `fieldAccess.getDispatcher().resolveTypeBinding()`
	 * to compute the type containing the field uses an already compute type. 
	 * 
	 * @see {@link FieldAccess#resolveFieldBinding()}
	 */
	private IVariableBinding resolveField(FieldAccess fieldAccess, ITypeBinding containerTypeBinding) {
		final VariableBase member = fieldAccess.getMember();
		if (member.getType() == ASTNode.VARIABLE) {
			Variable var = (Variable) member;
			if (!var.isDollared() && var.getName() instanceof Identifier) {
				Identifier id = (Identifier) var.getName();
				final String fieldName = "$" + id.getName(); //$NON-NLS-1$
				//final ITypeBinding type = fieldAccess.getDispatcher().resolveTypeBinding(); 
				final ITypeBinding type = containerTypeBinding;
				
				// There needs to be a check here to verify if the superclass has a valid binding or not.
				if (type.getSuperclass() == null) {
					return Bindings.findFieldInHierarchy(type, fieldName);
				} else if (isValidTypeBinding(type.getSuperclass())) {
					return Bindings.findFieldInHierarchy(type, fieldName);
				} else {
					logInvalidBinding("Field access [type binding superclass] ", getNameFromExpression(fieldAccess.getField()));
					return null;
				}
			}
		}
		return null;
	}

	
	// ACCESSES
	
	public Access createAccessFromFieldAccessNode(FieldAccess fieldAccess) {
		ITypeBinding typeBinding = fieldAccess.getDispatcher().resolveTypeBinding();
		IVariableBinding variableBinding;
		
		// If the type binding is not valid there will be a cast exception when resoving the variable binding.
		if (typeBinding == null) {
			logNullBinding("Field access", getNameFromExpression(fieldAccess.getField()));
			return new Access();
		}
		if (isValidTypeBinding(typeBinding) == false) {
			logInvalidBinding("Field access", getNameFromExpression(fieldAccess.getField()));
			return new Access();
		}
		variableBinding = fieldAccess.resolveFieldBinding();
		if (variableBinding != null) {
			return createAccessFromVariableBinding(variableBinding, fieldAccess.getAST().getBindingResolver());
		} else {
			// One reason why the access is not resolved is because it is an access to a variable defined in a trait.
			// fieldAccess.getDispatcher().resolveTypeBinding().getTraitList(false, getNameFromExpression(fieldAccess.getField()), true);
//			ITypeBinding fieldTypeBinding = fieldAccess.getDispatcher().resolveTypeBinding();
//			if (isValidTypeBinding(fieldTypeBinding) == false) {
//				return new Access();
//			}
			return new Access();
			// It happens! -> take into account
			// throw new RuntimeException("Let's see if this happens");
		}
	}
	
	public Access createAccessFromConstantAccessNode(StaticConstantAccess constantAccess) {		
		ITypeBinding typeBinding =  constantAccess.getClassName().resolveTypeBinding();
		IVariableBinding variableBinding;
		
		// If the type binding is not valid there will be a cast exception when resoving the variable binding.
		if (typeBinding == null) {
			logNullBinding("Constant access", constantAccess.getConstant().getName());
			return new Access();
		}
		if (isValidTypeBinding(typeBinding) == false) {
			logInvalidBinding("Constant access", constantAccess.getConstant().getName());
			return new Access();
		}
		
		variableBinding = constantAccess.resolveFieldBinding();
		if (variableBinding != null) {
			return createAccessFromVariableBinding(variableBinding, constantAccess.getAST().getBindingResolver());
		} else {
			return new Access();
		}
	}
	
	private Access createAccessFromVariableBinding(IVariableBinding variableBinding, BindingResolver bindingResolver) {
		Access access = new Access();
		StructuralEntity variable = unknownVariable();
		
		boolean isField = variableBinding.isField();
		boolean isParameter = isVariableMethodParameter(variableBinding);
		if (!isField && !isParameter)
			//we only consider fields and parameters ?
			return access;
		if (isField) 
			variable = ensureAttributeForVariableBinding(variableBinding, bindingResolver);
		else if (isParameter)
			variable = ensureParameterWithinCurrentMethodFromVariableBinding(variableBinding);
		
		access.setVariable(variable);
		access.setIsWrite(false);
	
		// Remove this after handling direct accesses outside of methods
		if (topOfContainerStack() instanceof Method)
			access.setAccessor((Method) topOfContainerStack());
	
		repository.add(access);
		
		return access;
	}
	
	public UnknownVariable unknownVariable() {
		if (unknownVariable == null) {
			unknownVariable = new UnknownVariable();
			repository.add(unknownVariable);
		}
		return unknownVariable;
	}
	
	
	/**
	 * I try to detect if the given variable binding is a method parameter.
	 * I am needed as org.eclipse.php.core.ast.node.VariableBinding#isParameter() just returns false.
	 */
	private boolean isVariableMethodParameter(IVariableBinding variableBinding) {
		if (variableBinding.getPHPElement()==null ) {
			return false;
		}
		if ( (variableBinding.getPHPElement().getElementType() == IModelElement.FIELD) == false ) {
			return false;
		}
		
		IModelElement fieldElement = variableBinding.getPHPElement();
		if (fieldElement.getParent() == null || (fieldElement.getParent().getElementType() == IModelElement.METHOD) == false) {
			return false;
		}
		
		IMethod methodElement = (IMethod)(fieldElement.getParent());
		try {
			return Arrays.asList(methodElement.getParameterNames()).contains(variableBinding.getName());
		} catch (ModelException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private void extractBasicModifiersFromBinding(int modifiers, NamedEntity entity) {
		if (PHPFlags.isPublic(modifiers) || PHPFlags.isDefault(modifiers)) {
			entity.addModifiers("public"); //$NON-NLS-1$
		}
		if (PHPFlags.isProtected(modifiers)) {
			entity.addModifiers("protected"); //$NON-NLS-1$
		}
		if (PHPFlags.isPrivate(modifiers)) {
			entity.addModifiers("private"); //$NON-NLS-1$
		}
		if (PHPFlags.isAbstract(modifiers)) {
			entity.addModifiers("abstract"); //$NON-NLS-1$
		}
		if (PHPFlags.isFinal(modifiers)) {
			entity.addModifiers("final"); //$NON-NLS-1$
		}
		if (PHPFlags.isConstant(modifiers)) {
			entity.addModifiers("const"); //$NON-NLS-1$
		}
	}
	
	public String getNameFromExpression(Expression expression) {
		switch (expression.getType()) {
		case ASTNode.REFERENCE:
			expression = ((Reference) expression).getExpression();
			if (expression.getType() != ASTNode.VARIABLE) {
				throw new IllegalStateException();
			}
		case ASTNode.VARIABLE:
			Variable variable = (Variable) expression;
			final Identifier variableName = (Identifier) variable.getName();
			return (variable.isDollared() ? "$" : "") + variableName.getName();
		
		case ASTNode.REFLECTION_VARIABLE:
			ReflectionVariable reflectionVariable = (ReflectionVariable)expression;
			if (reflectionVariable.getName().getType() == ASTNode.SCALAR) {
				return ((Scalar) reflectionVariable.getName()).getStringValue();
			}
		}	
		throw new IllegalStateException();
	}
	
	//SOURCE ANCHOR
	
	public void createSourceAnchor(SourcedEntity sourcedEntity, ASTNode node) {
		createSourceAnchor(sourcedEntity, node.getStart(), node.getEnd());
	}
	
	public void createSourceAnchor(SourcedEntity sourcedEntity, int startPosition, int endPosition) {
		IndexedFileAnchor fileAnchor = new IndexedFileAnchor();
		fileAnchor.setStartPos(startPosition);
		fileAnchor.setEndPos(endPosition);
		fileAnchor.setFileName(pathWithoutIgnoredRootPath(getCurrentFilePath()));
		sourcedEntity.setSourceAnchor(fileAnchor);
		repository.add(fileAnchor);
	}

	/**
	 * I return the qualified name of an entity as computed by PDT.
	 */
	public String getQualifiedNameForTypeBinding(ITypeBinding binding) {
		
		String qualifiedName = binding.getKey();
		if (qualifiedName == null) {
			// We handle the cases when PDT does not generate a key differenty.
			// For primitive types we just use the name of the type.
			if (binding.isPrimitive() || binding.isNullType()) {
				qualifiedName = binding.getName(); 
			} else if (binding.isArray()) {
				// This is a temporary solutions for arrays;
				// Normally we should use also the name of the type to
				// distinguish between different types of arrays.
				qualifiedName = "array";
			}
			if (qualifiedName == null) {
				return null;
				//throw new RuntimeException("Unable to compute qualified name");
			}
		} 
		return qualifiedName;
	}
	
	public String makeTypeQualifiedNameFrom(String containerQualifiedName, String entityName) {
		return containerQualifiedName.isEmpty() ? entityName : containerQualifiedName + NAME_SEPARATOR + entityName ;
	}
	
	public String entityBasenameFrom(String qualifiedEntityName) {
		int lastIndexOfSeparator = qualifiedEntityName.lastIndexOf(NAME_SEPARATOR);
		if (lastIndexOfSeparator < 0) {
			return qualifiedEntityName;
		} else {
			return qualifiedEntityName.substring(lastIndexOfSeparator+1);
		}
	}
	
	public String namepaceBasenameFrom(String qualifiedName) {
		int lastIndexOfSeparator = qualifiedName.lastIndexOf(NAMESPACE_SEPARATOR);
		if (lastIndexOfSeparator < 0) {
			return qualifiedName;
		} else {
			return qualifiedName.substring(lastIndexOfSeparator+1);
		}
	}
	
	// EXPORT

	public void exportMSE(String fileName) {
		
		try {
			repository.exportMSE(new FileWriter(fileName));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
		
	// LOGGING

	public void logNullBinding(String string, String extraData) {
		logger.error("binding null: " + string +
				" - " + extraData +
				" - " + getCurrentFilePath());
	}	
	
	public void logInvalidBinding(String bindingType, String extraData) {
		logger.error("binding invalid: " + bindingType +
				" - " + extraData +
				" - " + getCurrentFilePath());
	}	
		
	
	// OPENING
	
	public void run(IScriptProject projectPHP, List<String> allowedPaths, boolean exportAST) throws Exception{
		AstVisitor visitor = new AstVisitor(this);
		List<String> updatedPaths = updatePathForProject(projectPHP, allowedPaths);		
		for (IProjectFragment projectFragment : projectPHP.getProjectFragments()) {
			logger.trace("IProjectFragment: " + projectFragment.getPath());
			if (projectFragment.isExternal() == false) {
				processModelElement(projectFragment, visitor, updatedPaths, exportAST);
			}
		}
	}
	
	private List<String> updatePathForProject(IScriptProject projectPHP, List<String> allowedPaths) {
		String projectPath = projectPHP.getPath().toString();
		List<String> updatedPaths = allowedPaths.stream()
				.map(aPath -> {
					String updatedPath = aPath;
					if (updatedPath.startsWith("/") == false) {
						updatedPath = "/" + updatedPath;
					}
					if (updatedPath.endsWith("/") == false) {
						updatedPath = updatedPath + "/";
					}
					return projectPath + updatedPath; })
				.collect(Collectors.toList());
		return updatedPaths;
	}
	
	private void processModelElement(IModelElement modelElement, Visitor visitor, List<String> allowedPaths, boolean exportAST) throws Exception {
		if (modelElement instanceof ISourceModule) {
			//logger.trace("ISourceModule: "+modelElement.getElementName()+" "+modelElement.getPath().toString());
			boolean isPresent = allowedPaths.stream()
				.filter(aPath -> modelElement.getPath().toString().startsWith(aPath))
				.findAny()
				.isPresent();
			if (allowedPaths.isEmpty() || isPresent) {
				logger.trace("ISourceModule: "+modelElement.getElementName()+" - "+modelElement.getPath());
				setCurrentSourceModel((ISourceModule)modelElement);
				
				ASTParser parser = ASTParser.newParser(PHPVersion.PHP7_1, ((ISourceModule)modelElement));
				Program program = parser.createAST(null);
				
//				// Initialize the comment mapper.
//				IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(((ISourceModule)modelElement).getPath());
//				TextFileDocumentProvider documentProvider = new TextFileDocumentProvider();
//				documentProvider.connect(resource);
//				IDocument document = documentProvider.getDocument(resource);
//				program.initCommentMapper(document, program.getAST().lexer());
				
				if (exportAST) {
					exportAST(program);
				}
				program.accept(visitor);
			}
		} else if (modelElement instanceof IParent) {
			IModelElement[] children = ((IParent)modelElement).getChildren();
			for (IModelElement childElement: children) {
				processModelElement(childElement, visitor, allowedPaths, exportAST);	
			}
		}
		
	}
	
	private void exportAST(Program program) {
		IPath relativeFilePath = getCurrentSourceModel().getPath().removeFileExtension().addFileExtension("ast");	
		IPath absoluteProjectPath = ResourcesPlugin.getWorkspace().getRoot().findMember(scriptProject.getPath()).getLocation();
		IPath absoluteFilePath = absoluteProjectPath.append(relativeFilePath.removeFirstSegments(1));		
		
		try (BufferedWriter output = new BufferedWriter(new FileWriter(absoluteFilePath.toFile()))) {
			output.write(program.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Typically holds the prefix of the path of the root folder in which the importer was triggered.
	 * It is useful for creating relative paths for the source anchors  
	 */
	protected String ignoredRootPath;
	public String pathWithoutIgnoredRootPath(String originalPath) { 
		return originalPath.replaceAll("\\\\", "/").replaceFirst("^"+ignoredRootPath+"/", "");
	}
	
	
	// COMMENT
	
	public com.feenk.pdt2famix.exporter.model.famix.Comment extractCommentFromASTNode(ASTNode astNode) {
		Comment commentNode = extractMainCommentNodeForASTNode(astNode);
		// Nothing to do if the comment not present or cannot be detected. 
		if (commentNode == null) {
			return null;
		}
		com.feenk.pdt2famix.exporter.model.famix.Comment famixComment = new com.feenk.pdt2famix.exporter.model.famix.Comment();
		createSourceAnchor(famixComment, commentNode);
		return famixComment;
	}
		
	public void extractCommentAndAnnotationsFromASTNode(NamedEntity namedEntity, ASTNode astNode) {
		com.feenk.pdt2famix.exporter.model.famix.Comment famixComment = extractCommentFromASTNode(astNode);
		if (famixComment == null) {
			return ;
		}
		namedEntity.addComments(famixComment);
		repository.add(famixComment);
		
		String commentSource = getSourceFromFamixComment(famixComment, astNode.getProgramRoot());
		List<AnnotationInstance> annotationInstances = annotationResolver.extractAnnotationInstancesFromComment(commentSource, astNode);
		namedEntity.setAnnotationInstances(annotationInstances);
	}
	
	
	private String getSourceFromFamixComment(com.feenk.pdt2famix.exporter.model.famix.Comment famixComment, Program programRoot) {
		String commentSource = "";
		if (famixComment == null ) {
			return commentSource;
		}
		if (famixComment.getSourceAnchor() instanceof IndexedFileAnchor) {
			IndexedFileAnchor fileAnchor = (IndexedFileAnchor)famixComment.getSourceAnchor();
			try {
				commentSource = programRoot.getSourceModule().getSource().substring(fileAnchor.getStartPos().intValue(), fileAnchor.getEndPos().intValue());
			} catch (ModelException e) {
				e.printStackTrace();
			}
		} else {
			throw new RuntimeException("The importer should always generate IndexedFileAnchor. "
					+ "If this exception appears this methods needs to be updated");
		}
		
		return commentSource;
	}
	
	private Comment extractMainCommentNodeForASTNode(ASTNode astNode) {
		//TODO: Experiment more with the following:
		//fieldDeclaration.getProgramRoot().firstLeadingCommentIndex(fieldDeclaration);
		//fieldDeclaration.getProgramRoot().comments();
		
		Program programRoot = astNode.getProgramRoot();
		int entityStartLine = programRoot.getLineNumber(astNode.getStart());
		return programRoot.comments().stream()
				.filter( comment -> {
					int commentEndLine = programRoot.getLineNumber(comment.getEnd());
					return 
							commentEndLine     == entityStartLine ||    // The comment ends on the line on which the entity starts
							commentEndLine + 1 == entityStartLine ||    // The entity starts on the line following the comment
							comment.getEnd() + 1 == astNode.getStart(); // The entity starts right after the comment. 
					})
				.findFirst()
				.orElse(null);
	}
	

	public ITypeBinding createTypeBinding(final IType type, final BindingResolver bindingResolver) {
		return new TypeBinding(bindingResolver, PHPClassType.fromIType(type), type);
	}
	
	public ITypeBinding createTypeBinding(final String typeName, final BindingResolver bindingResolver) {
		String relativeTypeName = typeName.startsWith("\\") ? typeName.replaceFirst("\\\\", "") : typeName;
		IDLTKSearchScope searchScope = SearchEngine.createSearchScope(this.getCurrentSourceModel().getScriptProject());
		IType[] types = PHPModelAccess.getDefault().findTypes(relativeTypeName, MatchRule.EXACT, 0, 0, searchScope, new NullProgressMonitor());
		if (types.length == 1) {
			return createTypeBinding(types[0], bindingResolver);
		}
		return null;
	}
}
