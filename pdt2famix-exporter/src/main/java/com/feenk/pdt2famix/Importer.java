package com.feenk.pdt2famix;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;

import org.eclipse.dltk.core.IModelElement;
import org.eclipse.dltk.core.IParent;
import org.eclipse.dltk.core.IProjectFragment;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.dltk.core.ISourceModule;
import org.eclipse.php.core.PHPVersion;
import org.eclipse.php.core.ast.nodes.ASTNode;
import org.eclipse.php.core.ast.nodes.ASTParser;
import org.eclipse.php.core.ast.nodes.ITypeBinding;
import org.eclipse.php.core.ast.nodes.NamespaceDeclaration;
import org.eclipse.php.core.ast.nodes.Program;
import org.eclipse.php.core.ast.visitor.Visitor;

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
import com.feenk.pdt2famix.model.famix.SourcedEntity;
import com.feenk.pdt2famix.model.famix.Type;
import com.feenk.pdt2famix.model.java.JavaModel;

import ch.akuhn.fame.MetaRepository;
import ch.akuhn.fame.Repository;
import pdt2famix_exporter.Activator;

public class Importer {	
	private static final Activator logger = Activator.getDefault();
	 
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
	
	public Importer() {
		MetaRepository metaRepository = new MetaRepository();
		FAMIXModel.importInto(metaRepository);
		JavaModel.importInto(metaRepository);
		repository = new Repository(metaRepository);
		repository.add(new JavaSourceLanguage());
		
		namespaces = new NamedEntityAccumulator<Namespace>(repository);
		types = new NamedEntityAccumulator<Type>(repository);
		methods = new NamedEntityAccumulator<Method>(repository);
		attributes = new NamedEntityAccumulator<Attribute>(repository);
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
		int lastIndexOfBackslash = qualifiedName.lastIndexOf("\\");
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
	
	// TYPES
	
	public Type ensureTypeFromTypeBinding(ITypeBinding binding) {
		String qualifiedName =  getQualifiedName(binding);
		if (types.has(qualifiedName)) { 
			return types.named(qualifiedName); };
			
		Type type = createTypeFromTypeBinding(binding);
		type.setName(binding.getName());
		types.add(qualifiedName, type);
		type.setIsStub(true);
		if (binding.isAmbiguous()) {
			System.out.println();
		}
		IModelElement modelElement = binding.getPHPElement();
	
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
	
	private void extractBasicModifiersFromBinding(int modifiers, NamedEntity entity) {
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

	public String getQualifiedName(ITypeBinding binding) {
		StringBuilder qualifiedName = new StringBuilder();
		containerStackForEach(container -> 
			 qualifiedName.append("\\" + container.getName()));
		return qualifiedName + "\\" + binding.getName();
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
