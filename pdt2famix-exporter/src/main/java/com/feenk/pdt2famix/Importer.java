package com.feenk.pdt2famix;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.eclipse.dltk.core.IMember;
import org.eclipse.dltk.core.IMethod;
import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IParent;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.php.core.PHPToolkitUtil;
import org.eclipse.php.core.PHPVersion;
import org.eclipse.php.core.ast.nodes.ASTNode;
import org.eclipse.php.core.ast.nodes.ASTParser;
import org.eclipse.php.core.ast.nodes.ArrayCreation;
import org.eclipse.php.core.ast.nodes.Expression;
import org.eclipse.php.core.ast.nodes.FieldAccess;
import org.eclipse.php.core.ast.nodes.FormalParameter;
import org.eclipse.php.core.ast.nodes.IMethodBinding;
import org.eclipse.php.core.ast.nodes.ITypeBinding;
import org.eclipse.php.core.ast.nodes.IVariableBinding;
import org.eclipse.php.core.ast.nodes.Identifier;
import org.eclipse.php.core.ast.nodes.NamespaceDeclaration;
import org.eclipse.php.core.ast.nodes.Program;
import org.eclipse.php.core.ast.nodes.Reference;
import org.eclipse.php.core.ast.nodes.SingleFieldDeclaration;
import org.eclipse.php.core.ast.nodes.Variable;
import org.eclipse.php.core.ast.visitor.Visitor;
import org.eclipse.php.core.compiler.PHPFlags;
import org.eclipse.php.core.compiler.ast.nodes.NamespaceReference;

import com.feenk.pdt2famix.inphp.AstVisitor;
import com.feenk.pdt2famix.model.famix.Access;
import com.feenk.pdt2famix.model.famix.Attribute;
import com.feenk.pdt2famix.model.famix.ContainerEntity;
import com.feenk.pdt2famix.model.famix.Entity;
import com.feenk.pdt2famix.model.famix.FAMIXModel;
import com.feenk.pdt2famix.model.famix.FileAnchor;
import com.feenk.pdt2famix.model.famix.IndexedFileAnchor;
import com.feenk.pdt2famix.model.famix.Inheritance;
import com.feenk.pdt2famix.model.famix.Invocation;
import com.feenk.pdt2famix.model.famix.JavaSourceLanguage;
import com.feenk.pdt2famix.model.famix.Method;
import com.feenk.pdt2famix.model.famix.NamedEntity;
import com.feenk.pdt2famix.model.famix.Namespace;
import com.feenk.pdt2famix.model.famix.Parameter;
import com.feenk.pdt2famix.model.famix.PrimitiveType;
import com.feenk.pdt2famix.model.famix.ScopingEntity;
import com.feenk.pdt2famix.model.famix.SourcedEntity;
import com.feenk.pdt2famix.model.famix.StructuralEntity;
import com.feenk.pdt2famix.model.famix.Trait;
import com.feenk.pdt2famix.model.famix.Type;
import com.feenk.pdt2famix.model.java.JavaModel;

import ch.akuhn.fame.MetaRepository;
import ch.akuhn.fame.Repository;
import pdt2famix_exporter.Activator;

public class Importer {	
	private static final Activator logger = Activator.getDefault();
	 
	private static final char NAME_SEPARATOR = '$';
	private static final char NAMESPACE_SEPARATOR = NamespaceReference.NAMESPACE_SEPARATOR;
	public static final String CONSTRUCTOR_KIND = "constructor";
	public static final String DEFAULT_NAMESPACE_NAME = "";
	public static final String SYSTEM_NAMESPACE_NAME = "__SYSTEM__";
	public static final String UNKNOWN_NAME = "__UNKNOWN__";
	
	private Map<Entity,String> entitiesToKeys = new HashMap<>();
	
	private Namespace systemNamespace;
	private Namespace unknownNamespace;
	private Type unknownType;
	
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
	
	private ISourceModule currentSourceModel;
	public ISourceModule getCurrentSourceModel() {return currentSourceModel;}
	public void setCurrentSourceModel(ISourceModule currentSourceModel) {this.currentSourceModel = currentSourceModel;}
	
	public String getCurrentFilePath() {return getCurrentSourceModel().getPath().makeAbsolute().toString(); }
	
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
	
	public Importer(IScriptProject projectPHP) {
		MetaRepository metaRepository = new MetaRepository();
		FAMIXModel.importInto(metaRepository);
		JavaModel.importInto(metaRepository);
		repository = new Repository(metaRepository);
		repository.add(new JavaSourceLanguage());
		
		namespaces = new NamedEntityAccumulator<Namespace>(repository);
		types = new NamedEntityAccumulator<Type>(repository);
		methods = new NamedEntityAccumulator<Method>(repository);
		attributes = new NamedEntityAccumulator<Attribute>(repository);
		
		scriptProject = projectPHP; 
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
	
	// TYPE
	
	public Type ensureTypeFromTypeBinding(ITypeBinding binding) {
		String qualifiedName = getQualifiedNameForBinding(binding); 
		binding.getKey();
		binding.getPHPElement();
		if (qualifiedName == null) {
			return unknownType();
			//throw new RuntimeException("Handle the case of unknown types");
		} 
		if (types.has(qualifiedName)) { 
			return types.named(qualifiedName); };
			
		Type famixType = createTypeFromTypeBinding(binding);
		if (binding.getPHPElement() != null) {
			// For classes that are in a namespace the name returned by the binding also includes the namespace.
			famixType.setName(binding.getPHPElement().getElementName());
		} else {
			famixType.setName(binding.getName());
		}
		
		types.add(qualifiedName, famixType);
		entitiesToKeys.put(famixType, qualifiedName);
		famixType.setIsStub(true);		
		binding.getInterfaces();
		//extractBasicModifiersFromBinding(binding.getModifiers(), type);
		famixType.setContainer(ensureContainerEntityForTypeBinding(binding));
		if (binding.getSuperclass() != null) 
			createInheritanceFromSubtypeToSuperTypeBinding(famixType, binding.getSuperclass());
		for (ITypeBinding interfaceBinding : binding.getInterfaces()) {
			createInheritanceFromSubtypeToSuperTypeBinding(famixType, interfaceBinding);
		}
		return famixType;
	}
	
	private Type createTypeFromTypeBinding(ITypeBinding binding) {
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
		
		com.feenk.pdt2famix.model.famix.Class clazz = new com.feenk.pdt2famix.model.famix.Class();
		clazz.setIsInterface(binding.isInterface());
		return clazz;
	}
	
	// INHERITANCE
	
	/**
	 * We use this one when we have the super type binding
	 */
	private Inheritance createInheritanceFromSubtypeToSuperTypeBinding(Type famixSubType, ITypeBinding superBinding) {
		return createInheritanceFromSubtypeToSuperType(famixSubType, ensureTypeFromTypeBinding(superBinding));
	}

	/**
	 * When we cannot resolve the binding of the superclass of a class declaration,
	 * we still want to create a {@link Type} with the best available information
	 * from {@link org.eclipse.jdt.core.dom.Type}
	 */
//	public Inheritance createInheritanceFromSubtypeToSuperDomType(Type famixSubType, org.eclipse.jdt.core.dom.Type type) {
//		return createInheritanceFromSubtypeToSuperType(famixSubType, ensureTypeFromDomType(type));
//	}

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
	
	// METHOD
	
	public Method ensureMethodFromMethodBindingToCurrentContainer(IMethodBinding methodBinding) {
		return ensureMethodFromMethodBinding(methodBinding, (Type) topOfContainerStack());
	}
	
	public Method ensureMethodFromMethodBinding(IMethodBinding binding) {
		/*	JDT2FAMIX: binding.getDeclaringClass() might be null when you invoke a method from a class that is not in the path
			It looks like calling getMethodDeclaration is more robust. */
		/* In PDT  getMethodDeclaration() does not exist in the method binding /*/
		
		ITypeBinding classBinding = binding.getDeclaringClass();
		Type famixClassType;
		if (classBinding == null || (classBinding.getPHPElement() != null && classBinding.getPHPElement() == this.currentSourceModel)) {
			// Sometimes in the parser the type containing a library method 
			// does not resolve correcly: if we resolve the binding of the type we 
			// get the current source module.
			// If we encounter that then we just use the unknown type.
			famixClassType = unknownType();
		} else {
			famixClassType = ensureTypeFromTypeBinding(classBinding);
		}
		
		return ensureMethodFromMethodBinding(binding, famixClassType);
	}
	
	public Method ensureMethodFromMethodBinding(IMethodBinding methodBinding, Type parentType) {
		String methodName = methodBinding.getName();
		return ensureBasicMethod(
				methodName,  
				parentType,
				famixMethod -> setUpMethodFromMethodBinding(famixMethod, methodBinding));
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
	
	public Method ensureBasicMethod(String methodName, Type parentType, Consumer<Method> ifAbsent) {
		String qualifiedName = makeQualifiedNameFrom(qualifiedFAMIXNameOf(parentType), methodName);
		if(methods.has(qualifiedName))
			return methods.named(qualifiedName);
		Method method = new Method();
		method.setName(methodName);
		methods.add(qualifiedName, method);
//		method.setSignature(signature);
		method.setIsStub(true);
		method.setParentType(parentType);
		ifAbsent.accept(method);
		return method;
	}
	
	// PARAMETERS 
	
	//PARAMETER
	
	public Parameter parameterFromFormalParameterDeclaration(FormalParameter formalParameter, Method famixMethod) {
		String name = getNameFromExpression(formalParameter.getParameterName());
		
		Parameter parameter = new Parameter();
		parameter.setName(name);
		parameter.setParentBehaviouralEntity(famixMethod);
		
		Type parameterType = null;
		Expression declatedType = formalParameter.getParameterType();
		Expression defaultValue = formalParameter.getDefaultValue();
		
		if (declatedType != null) {
			ITypeBinding resolvedTypeBinding = declatedType.resolveTypeBinding();
			if (resolvedTypeBinding != null) {
				parameterType = ensureTypeFromTypeBinding(resolvedTypeBinding);
			}
		}
		if (defaultValue != null) {
			ITypeBinding resolvedTypeBinding = defaultValue.resolveTypeBinding();
			Type defaultValueType = null;
			if (resolvedTypeBinding != null) {
				defaultValueType = ensureTypeFromTypeBinding(resolvedTypeBinding);
			}
			if (parameterType == null) {
				parameterType = defaultValueType;
			}
		}
		
		parameter.setDeclaredType(parameterType);

		return parameter;
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
	
	// ATTRIBUTE
	
	public Attribute ensureAttributeForFieldDeclaration(SingleFieldDeclaration fieldDeclaration) {
		IVariableBinding variableBinding = fieldDeclaration.getName().resolveVariableBinding();
		Attribute attribute;
		
		if (variableBinding == null) {
			attribute = ensureAttributeFromFieldDeclarationIntoParentType(fieldDeclaration);
		}
		else {
			attribute = ensureAttributeForVariableBinding(variableBinding);
			extractBasicModifiersFromBinding(variableBinding.getModifiers(), attribute);
			if (PHPFlags.isStatic(variableBinding.getModifiers()))
				attribute.setHasClassScope(true);
			throw new RuntimeException("Somehow we got a binging");
		}
		attribute.setIsStub(true);
		return attribute;
	}
	
	public Attribute ensureAttributeForVariableBinding(IVariableBinding variableBinding) {
		String name = variableBinding.getName();
		ITypeBinding parentTypeBinding = variableBinding.getDeclaringClass();
		Type parentType;
		if (parentTypeBinding == null)
			parentType = unknownType();
		else 
			parentType = ensureTypeFromTypeBinding(parentTypeBinding);
		
		String qualifiedName = variableBinding.getKey();
		if (attributes.has(qualifiedName)) 
			return attributes.named(qualifiedName);
		Type attributeType;
		if (variableBinding.getType() != null)  { 
			attributeType = ensureTypeFromTypeBinding(variableBinding.getType()) ;
		} else  {
			attributeType = unknownType();
		}
		Attribute attribute = ensureBasicAttribute(parentType, name, qualifiedName, attributeType);
		
//		Andrei: I moved this here from the visitor method.
//		extractBasicModifiersFromBinding(variableBinding.getModifiers(), attribute);
//		if (PHPFlags.isStatic(variableBinding.getModifiers()))
//			attribute.setHasClassScope(true);
		
		//createAnnotationInstancesToEntityFromAnnotationBinding(attribute, variableBinding.getAnnotations());
		return attribute;
	}
	
	public Attribute ensureAttributeFromFieldDeclarationIntoParentType(SingleFieldDeclaration fieldDeclaration) {
		Type parentType = this.topFromContainerStack(Type.class);
		String name = getNameFromExpression(fieldDeclaration.getName());
		String qualifiedName = entitiesToKeys.get(parentType)+"^"+name;
		if (attributes.has(qualifiedName)) 
			return attributes.named(qualifiedName);
		Type declaredType = null;
		if (fieldDeclaration.getValue() != null) {
			Expression value = fieldDeclaration.getValue();
			ITypeBinding resolvedTypeBinding = value.resolveTypeBinding();
			if (resolvedTypeBinding == null || (resolvedTypeBinding.getPHPElement() != null && resolvedTypeBinding.getPHPElement() == this.currentSourceModel)) {
				// Sometimes in the parser the code "public $list = [];" or "private $languageCode = 'EN';" 
				// does not resolve correcly the type of the value. If we resolve the binding of the type we 
				// get the current source module.
				// If we encounter that then we just use the unknown type.
				declaredType = unknownType();
			} else {
				declaredType = ensureTypeFromTypeBinding(fieldDeclaration.getValue().resolveTypeBinding());
			}
		}
		Attribute attribute = ensureBasicAttribute(parentType, name, qualifiedName, declaredType);
		return attribute;
	}
	
	private Attribute ensureBasicAttribute(Type parentType, String name,
			String qualifiedName, Type declaredType) {
		Attribute attribute = new Attribute();
		attribute.setName(name);
		attribute.setParentType(parentType);
		attribute.setDeclaredType(declaredType);
		attributes.add(qualifiedName, attribute);
		return attribute;
	}
	
	// INVOCATIONS
	
	public Invocation createInvocationFromMethodBinding(IMethodBinding binding) {		
		Invocation invocation = new Invocation();
		invocation.setSender((Method) topOfContainerStack()); 
		if (binding != null)
			//TODO: If the binding is null we can still get the name if the method
			invocation.addCandidates(ensureMethodFromMethodBinding(binding));  
		//invocation.setSignature(signature);
		repository.add(invocation);
		return invocation;
	}
	
	public StructuralEntity ensureStructuralEntityFromExpression(Expression expression) {
		IVariableBinding variableBinding = null;
		if (expression.getClass().equals(Variable.class)) {
			variableBinding = ((Variable) expression).resolveVariableBinding();
		} else if (expression instanceof FieldAccess) {
			ITypeBinding contaierTypeBinding = ((FieldAccess) expression).getDispatcher().resolveTypeBinding();
			if ( contaierTypeBinding == null || (contaierTypeBinding.getPHPElement() == this.currentSourceModel)) {
				// Like in a few other cases the binding here gets resolved to the wrong module.
				System.out.println();
			} else {
				variableBinding = ((FieldAccess) expression).resolveFieldBinding();
			}
		}
		
		if (variableBinding != null) {
			if (variableBinding.isField())
				return ensureAttributeForVariableBinding(variableBinding);
			if (variableBinding.isParameter() || (variableBinding.isLocal() && isVariableMethodParameter(variableBinding) ) )
				return ensureParameterWithinCurrentMethodFromVariableBinding(variableBinding);
//			if (variableBinding.isLocal()) 
//				return null;
		}
		return null;
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
	
	/**
	 * All types should be ensured first via this method.
	 * We first check to see if the binding is resolvable (not null)
	 * If it is not null we ensure the type from the binding (the happy case)
	 * If the type is null we recover what we know (for example, the name of a simple type)
	 * In the worst case we return the {@link #unknownType()} 
	 */
//	private Type ensureTypeFromDomType(org.eclipse.jdt.core.dom.Type domType) {
//		ITypeBinding binding = domType.resolveBinding();
//		if (binding != null)
//			return ensureTypeFromTypeBinding(binding);
//		if (domType.isSimpleType())
//			return ensureTypeNamedInUnknownNamespace(((SimpleType) domType).getName().toString());
//		if (domType.isParameterizedType())
//			return ensureTypeNamedInUnknownNamespace(((org.eclipse.jdt.core.dom.ParameterizedType) domType).getType().toString());
//		return unknownType();
//	}
	
	/**
	 * This is the type we used as a null object whenever we need to reference a type  
	 */
	public Type unknownType() {
		if (unknownType == null) {
			unknownType = ensureTypeNamedInUnknownNamespace(UNKNOWN_NAME);
		}
		return unknownType;
	}
	
	
	public Type ensureTypeNamedInUnknownNamespace(String name) {
		Type type = createTypeNamedInUnknownNamespace(name);
		String qualifiedName = Famix.qualifiedNameOf(type); //TODO: this uses the java qualified like name
		if (types.has(qualifiedName))
			return types.named(qualifiedName);
		else {
			types.add(Famix.qualifiedNameOf(type), type);
			return type;
		}
	}

	public Type createTypeNamedInUnknownNamespace(String name) {
		Type type = new Type();
		type.setName(name);
		type.setContainer(unknownNamespace());
		type.setIsStub(true);
		return type;
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
		
//		Boolean publicModifier = Modifier.isPublic(modifiers);
//		Boolean protectedModifier = Modifier.isProtected(modifiers);
//		Boolean privateModifier = Modifier.isPrivate(modifiers);
//		if (publicModifier )
//			entity.addModifiers("public");
//		if (protectedModifier)
//			entity.addModifiers("protected");
//		if (privateModifier)
//			entity.addModifiers("private");
//		if (!(publicModifier || protectedModifier || privateModifier))
//			entity.addModifiers("package");
//		if (Modifier.isFinal(modifiers))
//			entity.addModifiers("final");
//		if (Modifier.isAbstract(modifiers))
//			entity.addModifiers("abstract");
//		if (Modifier.isNative(modifiers))
//			entity.addModifiers("native");
//		if (Modifier.isSynchronized(modifiers))
//			entity.addModifiers("synchronized");
//		if (Modifier.isTransient(modifiers))
//			entity.addModifiers("transient");
//		if (Modifier.isVolatile(modifiers))
//			entity.addModifiers("volatile");
		/*	We do not extract the static modifier here because we want to set the hasClassScope property
			and we do that specifically only for attributes and methods */  
	}
	
	private String getNameFromExpression(Expression expression) {
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
		}
		throw new IllegalStateException();
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
		
		binding.getEvaluatedType().getTypeName();
		binding.getPHPElement().getParent().getHandleIdentifier();
		
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
	
	//SOURCE ANCHOR
	
	public void createSourceAnchor(SourcedEntity sourcedEntity, ASTNode node) {
		IndexedFileAnchor fileAnchor = new IndexedFileAnchor();
		fileAnchor.setStartPos(node.getStart());
		fileAnchor.setEndPos(node.getEnd());
		fileAnchor.setFileName(pathWithoutIgnoredRootPath(getCurrentFilePath()));
		sourcedEntity.setSourceAnchor(fileAnchor);
		repository.add(fileAnchor);
	}

	/**
	 * I return the qualified name of an entity as computed by PDT.
	 */
	public String getQualifiedNameForBinding(ITypeBinding binding) {
		String qualifiedName = binding.getKey();
		binding.isAmbiguous();
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
	
	/**
	 * This is a method that manually tries to compute the qualified name for a binding 
	 * based on the current stack of containers. This is used to ensure that the qualified
	 * names generated by IType#getFullyQualifiedName() are as expected.
	 */
	public String computeQualifiedNameForBinding(ITypeBinding binding) {
		String qualifiedContainerName = qualifiedFAMIXNameOf(topOfContainerStackOrDefaultNamespace());
		return makeQualifiedNameFrom(qualifiedContainerName, binding.getName());
	}
	
	public String makeQualifiedNameFrom(String containerQualifiedName, String entityName) {
		return containerQualifiedName.isEmpty() ? entityName : containerQualifiedName + NAME_SEPARATOR + entityName ;
	}
	
	public String qualifiedFAMIXNameOf(ContainerEntity containerEntity) {	
		if (containerEntity instanceof Type)
			return qualifiedFAMIXNameOf((Type) containerEntity);
		if (containerEntity instanceof ScopingEntity)
			return qualifiedFAMIXNameOf((ScopingEntity) containerEntity);
		if (containerEntity instanceof Method)
			return qualifiedFAMIXNameOf((Method) containerEntity);
	
		throw new RuntimeException("TODO: "+containerEntity.getName());
	}
	
	public String qualifiedNameOf(Method method) {
		return makeQualifiedNameFrom(qualifiedFAMIXNameOf(method.getParentType()), method.getName());
	}
	
	public String qualifiedFAMIXNameOf(Type type) {	
		if (type.getContainer() instanceof Type)
			return qualifiedFAMIXNameOf((Type) type.getContainer()) + NAME_SEPARATOR + type.getName();
		return makeQualifiedNameFrom(qualifiedFAMIXNameOf((ScopingEntity) type.getContainer()), type.getName());
	}
	
	public String qualifiedFAMIXNameOf(ScopingEntity container) {
		if (container.getParentScope() != null)
			return qualifiedFAMIXNameOf(container.getParentScope()) + NAMESPACE_SEPARATOR + container.getName();
		return container.getName();
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

	public void logNullBinding(String string, Object extraData, int lineNumber) {
		logger.error("unresolved " + string +
				" - " + extraData +
				" - " + getCurrentFilePath() +
				" - line " + lineNumber);
	}			
		
	
	// OPENING

	
	public void run(IScriptProject projectPHP, List<String> allowedPaths) throws Exception{
		AstVisitor visitor = new AstVisitor(this);
		for (IProjectFragment projectFragment : projectPHP.getProjectFragments()) {
			logger.trace("IProjectFragment: "+projectFragment.getPath());
			if (projectFragment.isExternal() == false) {
				processModelElement(projectFragment, visitor, allowedPaths);
			}
		}
	}
	
	private void processModelElement(IModelElement modelElement, Visitor visitor, List<String> allowedPaths) throws Exception {
		if (modelElement instanceof ISourceModule) {
			logger.trace("ISourceModule: "+modelElement.getElementName()+" "+modelElement.getPath().toString());
			boolean isPresent = allowedPaths.stream()
				.filter(aPath -> modelElement.getPath().toString().startsWith(aPath))
				.findAny()
				.isPresent();
			if (allowedPaths.isEmpty() || isPresent) {
				logger.trace("ISourceModule: "+modelElement.getElementName()+" "+modelElement.getPath());
				setCurrentSourceModel((ISourceModule)modelElement);
				
				ASTParser parser = ASTParser.newParser(PHPVersion.PHP7_1, ((ISourceModule)modelElement));
				Program program = parser.createAST(null);
				program.accept(visitor);
			}
		} else if (modelElement instanceof IParent) {
			IModelElement[] children = ((IParent)modelElement).getChildren();
			for (IModelElement childElement: children) {
				processModelElement(childElement, visitor, allowedPaths);	
			}
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
	

}
