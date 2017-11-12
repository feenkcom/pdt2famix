package com.feenk.pdt2famix;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayDeque;
import java.util.ArrayList;
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

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
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
import org.eclipse.php.core.PHPToolkitUtil;
import org.eclipse.php.core.PHPVersion;
import org.eclipse.php.core.ast.nodes.ASTNode;
import org.eclipse.php.core.ast.nodes.ASTParser;
import org.eclipse.php.core.ast.nodes.ArrayCreation;
import org.eclipse.php.core.ast.nodes.BindingResolver;
import org.eclipse.php.core.ast.nodes.Bindings;
import org.eclipse.php.core.ast.nodes.ClassDeclaration;
import org.eclipse.php.core.ast.nodes.Expression;
import org.eclipse.php.core.ast.nodes.FieldAccess;
import org.eclipse.php.core.ast.nodes.FormalParameter;
import org.eclipse.php.core.ast.nodes.IMethodBinding;
import org.eclipse.php.core.ast.nodes.ITypeBinding;
import org.eclipse.php.core.ast.nodes.IVariableBinding;
import org.eclipse.php.core.ast.nodes.Identifier;
import org.eclipse.php.core.ast.nodes.InterfaceDeclaration;
import org.eclipse.php.core.ast.nodes.NamespaceDeclaration;
import org.eclipse.php.core.ast.nodes.Program;
import org.eclipse.php.core.ast.nodes.Reference;
import org.eclipse.php.core.ast.nodes.SingleFieldDeclaration;
import org.eclipse.php.core.ast.nodes.StaticConstantAccess;
import org.eclipse.php.core.ast.nodes.TraitDeclaration;
import org.eclipse.php.core.ast.nodes.TypeDeclaration;
import org.eclipse.php.core.ast.nodes.Variable;
import org.eclipse.php.core.ast.nodes.VariableBinding;
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
import com.feenk.pdt2famix.model.famix.TraitUsage;
import com.feenk.pdt2famix.model.famix.Type;
import com.feenk.pdt2famix.model.famix.UnknownVariable;
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
	
	public String getCurrentFilePath() {return getCurrentSourceModel().getPath().makeRelative().toString(); }
	
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
		parameters = new NamedEntityAccumulator<Parameter>(repository);
		
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
			logNullBinding("type binding resolving", debugData, 0);
			return unknownType();
		}
		if (isValidTypeBinding(binding) == false) {
			logInvalidBinding("type binding resolving", debugData);
			return unknownType();
		}
		return ensureTypeFromTypeBinding(binding);
	}
	
	public boolean isValidTypeBinding(ITypeBinding binding) {
		if (binding == null) {
			logNullBinding("type binding resolving", "", 0);
			return false;
		}
		if ((binding.getPHPElement() != null && binding.getPHPElement() == currentSourceModel)) {
			return false;
		}
		return true;
	}
	
	public Type ensureTypeFromTypeBinding(ITypeBinding binding) {
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
			logInvalidBinding("Method binding", binding);
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
				methodBinding.getKey(),
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
	
	public Method ensureBasicMethod(String methodName, String identifierKey, Type parentType, Consumer<Method> ifAbsent) {
		String qualifiedName = identifierKey;
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
		
		if (inferredTypeBinding != null) {
			possibleTypes.add(protectedEnsureTypeFromTypeBinding(inferredTypeBinding, "Parameter type "+qualifiedName));
		}
		if (declatedType != null) {
			ITypeBinding resolvedTypeBinding = declatedType.resolveTypeBinding();
			if (resolvedTypeBinding != null) {
				possibleTypes.add(protectedEnsureTypeFromTypeBinding(resolvedTypeBinding, "Parameter resolved type "+qualifiedName));
			}	
		}
		if (defaultValue != null) {
			ITypeBinding resolvedTypeBinding = defaultValue.resolveTypeBinding();
			if (resolvedTypeBinding != null) {
				possibleTypes.add(protectedEnsureTypeFromTypeBinding(resolvedTypeBinding, "Parameter default type "+qualifiedName));
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
			attribute = ensureAttributeForVariableBinding(variableBinding);
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
			declaredType = protectedEnsureTypeFromTypeBinding(resolvedTypeBinding, "Field declaration");
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
			attribute.setDeclaredType(protectedEnsureTypeFromTypeBinding(resolvedTypeBinding, "constant value")); 
		}
		
		return attribute;
	}
	
	/**
	 * 
	 * @param variableBinding Must not be null.
	 * 
	 * @return the Famix attribute associated to the given variable binding.
	 */
	private Attribute ensureAttributeForVariableBinding(IVariableBinding variableBinding) {
		String qualifiedName = variableBinding.getKey();
		if (attributes.has(qualifiedName)) 
			return attributes.named(qualifiedName);
		
		String name = variableBinding.getName();
		ITypeBinding parentTypeBinding = variableBinding.getDeclaringClass();
		Type parentType;
		if (parentTypeBinding == null)
			parentType = unknownType();
		else 
			parentType = ensureTypeFromTypeBinding(parentTypeBinding);
		
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
				logInvalidBinding("Expression declaration", expression);
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
	
	// ACCESSES
	
	public Access createAccessFromFieldAccessNode(FieldAccess fieldAccess) {
		ITypeBinding typeBinding = fieldAccess.getDispatcher().resolveTypeBinding();
		IVariableBinding variableBinding;
		
		// If the type binding is not valid there will be a cast exception when resoving the variable binding.
		if (isValidTypeBinding(typeBinding) == false) {
			return new Access();
		}
		variableBinding = fieldAccess.resolveFieldBinding();
		if (variableBinding != null) {
			return createAccessFromVariableBinding(variableBinding);
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
		if (isValidTypeBinding(typeBinding) == false) {
			return new Access();
		}
		
		variableBinding = constantAccess.resolveFieldBinding();
		if (variableBinding != null) {
			return createAccessFromVariableBinding(variableBinding);
		} else {
			return new Access();
		}
	}
	
	private Access createAccessFromVariableBinding(IVariableBinding variableBinding) {
		Access access = new Access();
		StructuralEntity variable = unknownVariable();
		
		boolean isField = variableBinding.isField();
		boolean isParameter = isVariableMethodParameter(variableBinding);
		if (!isField && !isParameter)
			//we only consider fields and parameters ?
			return access;
		if (isField) 
			variable = ensureAttributeForVariableBinding(variableBinding);
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

	public void logNullBinding(String string, Object extraData, int lineNumber) {
		logger.error("unresolved " + string +
				" - " + extraData +
				" - " + getCurrentFilePath() +
				" - line " + lineNumber);
	}	
	
	public void logInvalidBinding(String bindingType, Object extraData) {
		logger.error("unresolved " + bindingType +
				" - " + extraData +
				" - " + getCurrentFilePath());
	}	
		
	
	// OPENING

	
	public void run(IScriptProject projectPHP, List<String> allowedPaths, boolean exportAST) throws Exception{
		AstVisitor visitor = new AstVisitor(this);
		for (IProjectFragment projectFragment : projectPHP.getProjectFragments()) {
			logger.trace("IProjectFragment: "+projectFragment.getPath());
			if (projectFragment.isExternal() == false) {
				processModelElement(projectFragment, visitor, allowedPaths, exportAST);
			}
		}
	}
	
	private void processModelElement(IModelElement modelElement, Visitor visitor, List<String> allowedPaths, boolean exportAST) throws Exception {
		if (modelElement instanceof ISourceModule) {
			//logger.trace("ISourceModule: "+modelElement.getElementName()+" "+modelElement.getPath().toString());
			boolean isPresent = allowedPaths.stream()
				.filter(aPath -> modelElement.getPath().toString().startsWith(aPath))
				.findAny()
				.isPresent();
			if (allowedPaths.isEmpty() || isPresent) {
				//logger.trace("ISourceModule: "+modelElement.getElementName()+" "+modelElement.getPath());
				setCurrentSourceModel((ISourceModule)modelElement);
				
				ASTParser parser = ASTParser.newParser(PHPVersion.PHP7_1, ((ISourceModule)modelElement));
				Program program = parser.createAST(null);
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
		IPath absoluteFilePath = ResourcesPlugin.getWorkspace().getRoot().getLocation().append(relativeFilePath);
		
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

}
