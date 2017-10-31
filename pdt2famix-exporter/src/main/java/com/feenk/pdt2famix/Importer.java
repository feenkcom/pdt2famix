package com.feenk.pdt2famix;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Consumer;

import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.INamespace;
import org.eclipse.dltk.core.IParent;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.dltk.core.IType;
import org.eclipse.dltk.core.ModelException;
import org.eclipse.dltk.core.index2.search.ISearchEngine.MatchRule;
import org.eclipse.dltk.core.search.IDLTKSearchScope;
import org.eclipse.dltk.core.search.SearchEngine;
import org.eclipse.php.core.PHPVersion;
import org.eclipse.php.core.ast.nodes.ASTNode;
import org.eclipse.php.core.ast.nodes.ASTParser;
import org.eclipse.php.core.ast.nodes.IMethodBinding;
import org.eclipse.php.core.ast.nodes.ITypeBinding;
import org.eclipse.php.core.ast.nodes.NamespaceDeclaration;
import org.eclipse.php.core.ast.nodes.Program;
import org.eclipse.php.core.ast.visitor.Visitor;
import org.eclipse.php.core.compiler.PHPFlags;
import org.eclipse.php.core.compiler.ast.nodes.NamespaceReference;
import org.eclipse.php.internal.core.model.PHPModelAccess;

import com.feenk.pdt2famix.inphp.AstVisitor;
import com.feenk.pdt2famix.model.famix.Attribute;
import com.feenk.pdt2famix.model.famix.ContainerEntity;
import com.feenk.pdt2famix.model.famix.FAMIXModel;
import com.feenk.pdt2famix.model.famix.FileAnchor;
import com.feenk.pdt2famix.model.famix.JavaSourceLanguage;
import com.feenk.pdt2famix.model.famix.Method;
import com.feenk.pdt2famix.model.famix.NamedEntity;
import com.feenk.pdt2famix.model.famix.Namespace;
import com.feenk.pdt2famix.model.famix.PrimitiveType;
import com.feenk.pdt2famix.model.famix.ScopingEntity;
import com.feenk.pdt2famix.model.famix.SourcedEntity;
import com.feenk.pdt2famix.model.famix.Type;
import com.feenk.pdt2famix.model.java.JavaModel;

import ch.akuhn.fame.MetaRepository;
import ch.akuhn.fame.Repository;
import pdt2famix_exporter.Activator;

public class Importer {	
	private static final Activator logger = Activator.getDefault();
	 
	private static final char NAME_SEPARATOR = '$';
	private static final char NAMESPACE_SEPARATOR = NamespaceReference.NAMESPACE_SEPARATOR;
	private static final String CONSTRUCTOR_KIND = "constructor";
	public static final String DEFAULT_NAMESPACE_NAME = "";
	
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
	
	private String currentFilePath;
	public String getCurrentFilePath() {return currentFilePath;}
	public void setCurrentFilePath(String currentFilePath) {this.currentFilePath = currentFilePath;}
	
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
		return ensureNamespaceNamed(declaration.getName() == null ? "" : declaration.getName().getName());
	}
	
	public Namespace ensureNamespaceNamed(String qualifiedName) {
		if (namespaces.has(qualifiedName)) 
			return namespaces.named(qualifiedName);
		else
			return namespaces.add(qualifiedName, createNamespaceNamed(qualifiedName));
	}
	
	private Namespace createNamespaceNamed(String qualifiedName) {
		int lastIndexOfBackslash = qualifiedName.lastIndexOf(NAMESPACE_SEPARATOR);
		Namespace namespace = new Namespace();
		namespace.setIsStub(true);
		if (lastIndexOfBackslash <= 0)
			namespace.setName(qualifiedName);
		else {
			/* Namespaces in PHP can be nested.
			 * In Famix, namespaces are also nested. So we create nesting based on the \ separator.
			 */
			namespace.setName(qualifiedName.substring(lastIndexOfBackslash+1));
			Namespace parentNamespace = ensureNamespaceNamed(qualifiedName.substring(0, lastIndexOfBackslash));
			namespace.setParentScope(parentNamespace);
		}
		return namespace;
	}
	
	// TYPE
	
	public Type ensureTypeFromTypeBinding(ITypeBinding binding) {
		String qualifiedName = getQualifiedNameForBinding(binding);
		if (types.has(qualifiedName)) { 
			return types.named(qualifiedName); };
			
		Type type = createTypeFromTypeBinding(binding);
		type.setName(binding.getName());
		types.add(qualifiedName, type);
		type.setIsStub(true);
		if (binding.isAmbiguous()) {
			System.out.println();
		}
		
//		Experiments
//		IModelElement modelElement = binding.getPHPElement();
//		String delimiter = NamespaceReference.NAMESPACE_DELIMITER;
//		String fullName = ((IType)modelElement).getFullyQualifiedName();
//		INamespace namespace = null;
//		try {
//			namespace = ((IType)modelElement).getNamespace();
//		} catch (ModelException e) {
//			e.printStackTrace();
//		}
//		((IType)modelElement.getParent()).getFullyQualifiedName();
//		binding.getKey();
//		binding.getEvaluatedType();
//		IDLTKSearchScope scope = SearchEngine.createSearchScope(getScriptProject());
//		Arrays.asList(PHPModelAccess.getDefault().
//				findTypes("", binding.getName(), MatchRule.EXACT, 0, 0, scope, null));
		
		
		//extractBasicModifiersFromBinding(binding.getModifiers(), type);
		type.setContainer(ensureContainerEntityForTypeBinding(binding));
//		if (binding.getSuperclass() != null) 
//			createInheritanceFromSubtypeToSuperTypeBinding(type, binding.getSuperclass());
//		for (ITypeBinding interfaceBinding : binding.getInterfaces()) {
//			createInheritanceFromSubtypeToSuperTypeBinding(type, interfaceBinding);
//		}
		return type;
	}
	
	private Type createTypeFromTypeBinding(ITypeBinding binding) {
		//TODO: binding.isAmbiguous()
		//TODO: binding.isTrait()
		//TODO: binding.isClass()
		//TODO: binding.isNullType()
		
		if (binding.isPrimitive())
			return new PrimitiveType();
		
		if (binding.isArray())
			return createTypeFromTypeBinding(binding.getElementType());
		
		com.feenk.pdt2famix.model.famix.Class clazz = new com.feenk.pdt2famix.model.famix.Class();
		clazz.setIsInterface(binding.isInterface());
		
		return clazz;
	}
	
	
	// METHOD
	
	public Method ensureMethodFromMethodBindingToCurrentContainer(IMethodBinding methodBinding) {
		return ensureMethodFromMethodBinding(methodBinding, (Type) topOfContainerStack());
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
		String qualifiedName = qualifiedFAMIXNameOf(parentType) + NAME_SEPARATOR + methodName;
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
	
	private ContainerEntity ensureContainerEntityForTypeBinding(ITypeBinding binding) {
		if (this.containerStack.isEmpty()) {
			return ensureNamespaceNamed("");
		} else {
			return this.topOfContainerStack();
		}
	}
	
	//SOURCE ANCHOR
	
	public void createSourceAnchor(SourcedEntity sourcedEntity, ASTNode node) {
		FileAnchor fileAnchor = new FileAnchor();
//		fileAnchor.setStartLine(compilationUnit.getLineNumber(node.getStartPosition()));
//		fileAnchor.setEndLine(compilationUnit.getLineNumber(node.getStartPosition() + node.getLength() - 1));
		fileAnchor.setFileName(pathWithoutIgnoredRootPath(currentFilePath));
		sourcedEntity.setSourceAnchor(fileAnchor);
		repository.add(fileAnchor);
	}

	/**
	 * I return the qualified name of an entity as computed by PDT.
	 */
	public String getQualifiedNameForBinding(ITypeBinding binding) {
		IModelElement modelElement = binding.getPHPElement();
		if (modelElement instanceof IType) {
			return ((IType)modelElement).getFullyQualifiedName();
		}
		throw new RuntimeException("No qualified name can be computed");
		//return qualifiedFAMIXNameOf(topOfContainerStackOrDefaultNamespace()) + NAME_SEPARATOR + binding.getName();
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
	
	public static String makeQualifiedNameFrom(String containerQualifiedName, String entityName) {
		return containerQualifiedName.isEmpty() ? entityName : containerQualifiedName + NAME_SEPARATOR + entityName ;
	}
	
	public static String qualifiedFAMIXNameOf(ContainerEntity containerEntity) {	
		if (containerEntity instanceof Type)
			return qualifiedFAMIXNameOf((Type) containerEntity);
		if (containerEntity instanceof ScopingEntity)
			return qualifiedFAMIXNameOf((ScopingEntity) containerEntity);
		if (containerEntity instanceof Method)
			return qualifiedFAMIXNameOf((Method) containerEntity);
	
		throw new RuntimeException("TODO: "+containerEntity.getName());
	}
	
	public static String qualifiedNameOf(Method method) {
		return makeQualifiedNameFrom(qualifiedFAMIXNameOf(method.getParentType()), method.getName());
	}
	
	public static String qualifiedFAMIXNameOf(Type type) {	
		if (type.getContainer() instanceof Type)
			return qualifiedFAMIXNameOf((Type) type.getContainer()) + NAME_SEPARATOR + type.getName();
		return makeQualifiedNameFrom(qualifiedFAMIXNameOf((ScopingEntity) type.getContainer()), type.getName());
	}
	
	public static String qualifiedFAMIXNameOf(ScopingEntity container) {
		if (container.getParentScope() != null)
			return qualifiedFAMIXNameOf(container.getParentScope()) + NAMESPACE_SEPARATOR + container.getName();
		return container.getName();
	}
	
	public String entityBasenameFrom(String qualifiedName) {
		int lastIndexOfSeparator = qualifiedName.lastIndexOf(NAME_SEPARATOR);
		if (lastIndexOfSeparator < 0) {
			return qualifiedName;
		} else {
			return qualifiedName.substring(lastIndexOfSeparator+1);
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
				" - " + currentFilePath +
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
				setCurrentFilePath(modelElement.getPath().makeAbsolute().toString());
				
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
