package com.feenk.pdt2famix.test.support;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.IScriptProject;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.LibraryLocation;
import org.junit.Before;
import org.junit.BeforeClass;

import com.feenk.pdt2famix.AnnotationResolver;
import com.feenk.pdt2famix.Importer;
import com.feenk.pdt2famix.model.famix.Access;
import com.feenk.pdt2famix.model.famix.AnnotationInstance;
import com.feenk.pdt2famix.model.famix.AnnotationType;
import com.feenk.pdt2famix.model.famix.Attribute;
import com.feenk.pdt2famix.model.famix.BehaviouralEntity;
import com.feenk.pdt2famix.model.famix.Inheritance;
import com.feenk.pdt2famix.model.famix.Invocation;
import com.feenk.pdt2famix.model.famix.Method;
import com.feenk.pdt2famix.model.famix.NamedEntity;
import com.feenk.pdt2famix.model.famix.Namespace;
import com.feenk.pdt2famix.model.famix.Parameter;
import com.feenk.pdt2famix.model.famix.StructuralEntity;
import com.feenk.pdt2famix.model.famix.Trait;
import com.feenk.pdt2famix.model.famix.TraitUsage;
import com.feenk.pdt2famix.model.famix.Type;

public abstract class InPhpTestCase {
	public static final String PROJECT_NAME = "pdt2famix-exporter-samples";
	public static final String SAMPLES_NAME = "samples";
		
	protected Importer importer;
	
	
	// ASSERTIONS NAMESPACES
	
	protected void assertNamespacePresent(String namespaceQualifiedName) {
		Namespace obtainedNamespace = null;
		
		assertTrue(importer.namespaces().has(namespaceQualifiedName));
		
		obtainedNamespace = importer.namespaces().named(namespaceQualifiedName);
		assertEquals(importer.namepaceBasenameFrom(namespaceQualifiedName), obtainedNamespace.getName());
		assertEquals(true, obtainedNamespace.getIsStub());
	}
	
	protected void assertNamespaceStructure(String namespaceQualifiedName, String parentNamespaceQualifiedName) {
		Namespace obtainedNamespace = null;
		Namespace parentNamespace = null;
		
		assertNamespacePresent(namespaceQualifiedName);
		 obtainedNamespace = importer.namespaces().named(namespaceQualifiedName);
		if (parentNamespaceQualifiedName != null) {
			assertTrue(importer.namespaces().has(parentNamespaceQualifiedName));
			parentNamespace = importer.namespaces().named(parentNamespaceQualifiedName);
		}
		assertEquals(parentNamespace, obtainedNamespace.getParentScope());
	}
	
	protected void assertEmptyNamespace(String qualifiedName) {
		assertEmptyNamespace(qualifiedName, null);
	}
	
	protected void assertEmptyNamespace(String namespaceQualifiedName, String parentNamespaceQualifiedName) {
		Namespace obtainedNamespace;
		
		assertNamespaceStructure(namespaceQualifiedName, parentNamespaceQualifiedName);
		
		obtainedNamespace = importer.namespaces().named(namespaceQualifiedName);
		assertEquals(0, obtainedNamespace.getGlobalVariables().size());
		assertEquals(0, obtainedNamespace.getTypes().size());
		assertEquals(0, obtainedNamespace.getFunctions().size());
		assertEquals(0, obtainedNamespace.getReceivingInvocations().size());
		assertEquals(0, obtainedNamespace.getDefinedAnnotationTypes().size());
		assertEquals(0, obtainedNamespace.getComments().size());
		assertEquals(0, obtainedNamespace.getAnnotationInstances().size());
	}
	
	protected void assertNamespacesPresent(String... namespacesQualifiedNames) {
		assertEquals(namespacesQualifiedNames.length, importer.namespaces().size());
		Arrays.asList(namespacesQualifiedNames).stream().forEach(
			qualifiedNamespaceName -> assertNamespacePresent(qualifiedNamespaceName));
		
	}
	
	protected void assertNamespaceTypes(String namespaceQualifiedName, String ...typesIdentifiers) {
		ArrayList<Type> expectedTypes = new ArrayList<>();
		
		Arrays.asList(typesIdentifiers).stream().forEach(
			typeQualifiedName -> expectedTypes.add(importer.types().named(typeQualifiedName)));
		assertNamespaceTypes(namespaceQualifiedName, expectedTypes.toArray(new Type[] {}));
	}
	
	protected void assertNamespaceTypes(String namespaceQualifiedName, Type ...expectedTypes) {
		Namespace namespace = importer.namespaces().named(namespaceQualifiedName);
		Collection<Type> obtainedTypes = namespace.getTypes();
		
		assertEquals(new HashSet<Type>(Arrays.asList(expectedTypes)), new HashSet<Type>(obtainedTypes));
		obtainedTypes.stream().forEach(
			aType -> assertEquals(namespace, aType.getContainer()));
	}
	
	// ASSERTIONS ANNOTATIONS
	
	protected void assertAnnotationTypePresent(String annotationTypeIdentifier) {
		Type annotationType;
		
		assertTrue(importer.types().has(annotationTypeIdentifier));
		annotationType = importer.types().named(annotationTypeIdentifier);
		
		assertEquals(false, annotationType.getIsStub());
		assertTrue(annotationType instanceof com.feenk.pdt2famix.model.famix.AnnotationType);
	}
	
	protected void assertAnnotationTagType() {
		String annotationTagTypeName = annotationTagTypeName();
		String annotationTypeIdentifier = importer.makeTypeQualifiedNameFrom(Importer.SYSTEM_NAMESPACE_NAME, annotationTagTypeName);
		
		assertTrue(importer.types().has(annotationTypeIdentifier));
		Type annotationType = importer.types().named(annotationTypeIdentifier);
		
		assertEquals(true, annotationType.getIsStub());
		assertEquals(importer.systemNamespace(), annotationType.getContainer());
		assertEquals(annotationTagTypeName, annotationType.getName());
		assertTrue(annotationType instanceof com.feenk.pdt2famix.model.famix.AnnotationType);	
	}
	
	protected AnnotationInstance locateAnnotationInstanceInType(Type targetClass, AnnotationType annotationType) {
		List<AnnotationInstance> possibleInstances = targetClass.getAnnotationInstances().stream()
			.filter(annotationInstance -> annotationInstance.getAnnotationType().equals(annotationType))
			.collect(Collectors.toList());
		
		assertEquals(1, possibleInstances.size());
		return possibleInstances.get(0);
	}

	protected void assertAnnotationInstancesForType(Type annotatedType, AnnotationType[] annotationTypes) {
		assertEquals(annotationTypes.length, annotatedType.getAnnotationInstances().size());
		 List<AnnotationInstance> extractedAnnotationInstances = Arrays.asList(annotationTypes).stream()
			.map(annotationType -> annotationType.getInstances().stream()
					.filter(annotationInstance -> annotationInstance.getAnnotatedEntity().equals(annotatedType))
					.findFirst())
			.filter(annotationInstanceOptional -> annotationInstanceOptional.isPresent())
			.map(annotationInstanceOptional -> annotationInstanceOptional.get())
			.collect(Collectors.toList());
		 assertEquals(new HashSet<>(annotatedType.getAnnotationInstances()), new HashSet<>(extractedAnnotationInstances));
	}
	
	protected void assertInstancesForAnnotationType(AnnotationType annotationType, Type[] annotatedTypes) {
		assertEquals(annotatedTypes.length, annotationType.getInstances().size());
		 List<AnnotationInstance> extractedAnnotationInstances = Arrays.asList(annotatedTypes).stream()
			.map(annotatedType -> annotatedType.getAnnotationInstances().stream()
					.filter(annotationInstance -> annotationInstance.getAnnotationType().equals(annotationType))
					.findFirst())
			.filter(annotationInstanceOptional -> annotationInstanceOptional.isPresent())
			.map(annotationInstanceOptional -> annotationInstanceOptional.get())
			.collect(Collectors.toList());
		assertEquals(new HashSet<>(annotationType.getInstances()), new HashSet<>(extractedAnnotationInstances));
	}
	
	
	// ASSERTIONS CLASSES & TRAITS
	
	
	protected void assertClassPresent(String classIdentifier) {
		Type classType;
		
		assertTrue(importer.types().has(classIdentifier));
		classType = importer.types().named(classIdentifier);
		
		assertEquals(false, classType.getIsStub());
		assertTrue(classType instanceof com.feenk.pdt2famix.model.famix.Class);
		assertEquals(false, ((com.feenk.pdt2famix.model.famix.Class)(classType)).getIsInterface());
	}
	
	protected void assertInterfacePresent(String interfaceIdentifier) {
		Type interfaceType;
		
		assertTrue(importer.types().has(interfaceIdentifier));
		interfaceType = importer.types().named(interfaceIdentifier);
		
		assertEquals(false, interfaceType.getIsStub());
		assertTrue(interfaceType instanceof com.feenk.pdt2famix.model.famix.Class);
		assertEquals(true, ((com.feenk.pdt2famix.model.famix.Class)(interfaceType)).getIsInterface());
	}
	
	protected void assertClassMethods(Type famixClass, String ...expectedMethodNames) {
		Collection<Method> obtainedMethods = famixClass.getMethods();
		ArrayList<Method>  expectedMethods = new ArrayList<>();
		
		assertEquals(obtainedMethods.size(), expectedMethodNames.length);
		Arrays.asList(expectedMethodNames).stream().forEach(
				localName -> expectedMethods.add(methodInType(famixClass, localName)));
		
		assertEquals(new HashSet<>(expectedMethods), new HashSet<>(obtainedMethods));
		obtainedMethods.stream().forEach(
				aType -> assertEquals(famixClass, aType.getParentType()));
	}
	
	protected void assertClassType(Type classType, String typeName) {
		assertEquals(typeName, classType.getName());
		assertEquals(false, classType.getIsStub());
		assertTrue(classType instanceof com.feenk.pdt2famix.model.famix.Class);
		assertEquals(false, ((com.feenk.pdt2famix.model.famix.Class)(classType)).getIsInterface());
	}
	
	protected void assertTraitType(Type traitType, String typeName) {
		assertEquals(typeName, traitType.getName());
		assertEquals(false, traitType.getIsStub());
		assertTrue(traitType instanceof Trait);
	}
	
	protected void assertInterfaceType(Type interfaceType, String typeName) {
		assertEquals(typeName, interfaceType.getName());
		assertEquals(false, interfaceType.getIsStub());
		assertTrue(interfaceType instanceof com.feenk.pdt2famix.model.famix.Class);
		assertEquals(true, ((com.feenk.pdt2famix.model.famix.Class)(interfaceType)).getIsInterface());
	}
	
	// ASSERTIONS INHERITANCE
	
	protected void assertInheritance(Type subclass, Type superclass) {
		Inheritance superclassSubclassInheritance = superclass.getSubInheritances().stream()
				.filter( inheritance -> inheritance.getSubclass().equals(subclass) )
				.findAny()
				.get();
		assertEquals(superclass, superclassSubclassInheritance.getSuperclass());
		assertEquals(subclass, superclassSubclassInheritance.getSubclass());
		
		Inheritance subclassSuperclassInheritance = subclass.getSuperInheritances().stream()
				.filter( inheritance -> inheritance.getSuperclass().equals(superclass) )
				.findAny()
				.get();
		assertEquals(superclassSubclassInheritance, subclassSuperclassInheritance);
	}
	
	// ASSERTIONS TRAIT USAGE
	
	protected void assertTraitUsage(Type typeUsingTrait, Trait usedTrait) {
		TraitUsage traitUsageInTrait = usedTrait.getIncomingTraitUsages().stream()
				.filter( traitUsage -> traitUsage.getUser().equals(typeUsingTrait) )
				.findAny()
				.get();
		assertEquals(typeUsingTrait, traitUsageInTrait.getUser());
		assertEquals(usedTrait, traitUsageInTrait.getTrait());
		
		TraitUsage traitUsageInType = typeUsingTrait.getOutgoingTraitUsages().stream()
				.filter( traitUsage -> traitUsage.getTrait().equals(usedTrait) )
				.findAny()
				.get();
		assertEquals(traitUsageInType, traitUsageInTrait);
	}
	
	// ASSERTIONS METHODS
	
	protected void assertMethod(Method method, String[] modifiers) {
		assertMethod(method, modifiers, null);
	}
	
	protected void assertMethod(Method method, String[] modifiers, String kind) {
		assertEquals(false, method.getHasClassScope() == null ? false : method.getHasClassScope() );
		assertEquals(kind, method.getKind());
		assertEquals(method.getModifiers(), new HashSet<>(Arrays.asList(modifiers)));
	}
	
	protected void assertStaticMethod(Method method, String[] modifiers, String kind) {
		assertEquals(true, method.getHasClassScope() == null ? false : method.getHasClassScope() );
		assertEquals(kind, method.getKind());
		assertEquals(method.getModifiers(), new HashSet<>(Arrays.asList(modifiers)));
	}
	
	// ASSERTIONS ATTRIBUTES & PARAMETERS
	
	protected void assertAttribute(String attributeName, Type parentType, Type declaredType) {
		Attribute attribute = attributeInType(parentType, attributeName);
		
		assertEquals(false, attribute.getHasClassScope() == null ? false : attribute.getHasClassScope() );
		assertEquals(parentType, attribute.getParentType());
		assertEquals(declaredType, attribute.getDeclaredType());
		//assertEquals(attribute.getModifiers(), new HashSet<>(Arrays.asList(modifiers)));
	}
	
	protected void assertAttribute(String attributeName, Type parentType, Type declaredType, boolean hasClassScope,
			String[] modifiers) {
		Attribute attribute = attributeInType(parentType, attributeName);
		
		assertEquals(hasClassScope, attribute.getHasClassScope() == null ? false : attribute.getHasClassScope() );
		assertEquals(parentType, attribute.getParentType());
		assertEquals(declaredType, attribute.getDeclaredType());
		assertEquals(new HashSet<>(Arrays.asList(modifiers)), new HashSet<>(attribute.getModifiers()));
	}
	
	protected void assertMethodParameter(String parameterName, BehaviouralEntity parentBehaviour, Type declaredType) {
		List<Parameter> possibleParameters = parentBehaviour.getParameters().stream()
				.filter( aParameter -> aParameter.getName().equals(parameterName))
				.collect(Collectors.toList());
		assertEquals(1, possibleParameters.size());
		
		Parameter targetParameter = possibleParameters.get(0);
		assertEquals(parentBehaviour, targetParameter.getParentBehaviouralEntity());
		assertEquals(declaredType, targetParameter.getDeclaredType());
	}

	
	
	// ASSERTIONS INVOCATIONS
	
	protected void assertInvocationsBetweenMethodsMatching(BehaviouralEntity sender, BehaviouralEntity candidate, int numberOfInvocations, 
			Predicate<Invocation> predicate) {
		List<Invocation> outgoingInvocations =  sender.getOutgoingInvocations().stream()
			.filter(invocation -> invocation.getCandidates().contains(candidate))
			.filter(predicate)
			.collect(Collectors.toList());
		assertEquals(numberOfInvocations, outgoingInvocations.size());
		List<Invocation> incomingInvocations = candidate.getIncomingInvocations().stream()
			.filter(invocation -> invocation.getSender().equals(sender))
			.filter(predicate)
			.collect(Collectors.toList());
		assertEquals(new HashSet<>(Arrays.asList(outgoingInvocations)), new HashSet<>(Arrays.asList(incomingInvocations)));
		outgoingInvocations.stream().forEach(
			invocation -> assertInvocationProperties(invocation, sender, candidate));
	}
	
	protected void assertInvocationsBetweenMethods(BehaviouralEntity sender, BehaviouralEntity candidate, int numberOfInvocations, 
			NamedEntity receiver) {
		assertInvocationsBetweenMethodsMatching(sender, candidate, numberOfInvocations, 
				invocation -> receiver == null ? invocation.getReceiver() == null :  invocation.getReceiver().equals(receiver) );
	}

	protected void assertInvocationProperties(Invocation invocation, BehaviouralEntity sender, BehaviouralEntity candidate, NamedEntity receiver, String signature) {
		assertInvocationProperties(invocation, sender, candidate, receiver);	
		assertEquals(signature, invocation.getSignature());
	}
	
	protected void assertInvocationProperties(Invocation invocation, BehaviouralEntity sender,  BehaviouralEntity[] candidates, NamedEntity receiver, String signature) {
		assertInvocationProperties(invocation, sender, candidates, receiver);	
		assertEquals(signature, invocation.getSignature());
	}
	
	protected void assertInvocationProperties(Invocation invocation, BehaviouralEntity sender, BehaviouralEntity candidate, NamedEntity receiver) {
		assertInvocationProperties(invocation, sender, new BehaviouralEntity[] {candidate}, receiver);	
	}
	
	private void assertInvocationProperties(Invocation invocation, BehaviouralEntity sender, BehaviouralEntity candidate) {
		assertEquals(sender, invocation.getSender());
		assertEquals(new HashSet<>(Arrays.asList(new BehaviouralEntity[] {candidate})), new HashSet<>(invocation.getCandidates()));
	}

	private void assertInvocationProperties(Invocation invocation, BehaviouralEntity sender, BehaviouralEntity[] candidates, NamedEntity receiver) {
		assertEquals(sender, invocation.getSender());
		assertEquals(candidates.length, invocation.getCandidates().size());
		assertEquals(receiver, invocation.getReceiver());
		assertEquals(new HashSet<>(Arrays.asList(candidates)), new HashSet<>(invocation.getCandidates()));
	}

	protected void assertOneInvocation(Method callerMethod, Method calledMethod) {
		assertEquals(0, callerMethod.getIncomingInvocations().size());
		assertEquals(1, callerMethod.getOutgoingInvocations().size());
		assertEquals(1, calledMethod.getIncomingInvocations().size());
		assertEquals(0, calledMethod.getOutgoingInvocations().size());
	}

	// ASSERTIONS ACCESSES
	
	protected void assertAccess(BehaviouralEntity accessorEntity, StructuralEntity accessedAttribute, boolean isWrite) {
		assertAccess(accessorEntity, accessedAttribute, 1, Optional.of(isWrite));
	}
	
	protected void assertAccess(BehaviouralEntity accessorEntity, StructuralEntity accessedAttribute, int accessCount) {
		assertAccess(accessorEntity, accessedAttribute, accessCount, Optional.empty());
	}
	
	protected void assertAccess(BehaviouralEntity accessorEntity, StructuralEntity accessedVariable, int accessCount, Optional<Boolean> isWrite) {
		List<Access> outgoingAccesses = accessorEntity.getAccesses().stream()
				.filter(access -> access.getVariable().equals(accessedVariable)).collect(Collectors.toList());
		assertEquals(accessCount, outgoingAccesses.size());
		List<Access> incomingAccesses = accessedVariable.getIncomingAccesses().stream()
				.filter(access -> access.getAccessor().equals(accessorEntity)).collect(Collectors.toList());
		assertEquals(new HashSet<>(Arrays.asList(outgoingAccesses)), new HashSet<>(Arrays.asList(incomingAccesses)));
		outgoingAccesses.stream().forEach( targetAccess ->
				assertAccessProperties(targetAccess, accessorEntity, accessedVariable, isWrite));
		
	}
	
	protected void assertAccessMatching(BehaviouralEntity accessorEntity, StructuralEntity accessedAttribute, Predicate<Access> predicate) {
		List<Access> possibleAccesses = accessorEntity.getAccesses().stream()
				.filter(access -> access.getVariable().equals(accessedAttribute))
				.filter(predicate)
				.collect(Collectors.toList());
		assertEquals(1, possibleAccesses.size());
		assertAccessProperties(possibleAccesses.get(0), accessorEntity, accessedAttribute, Optional.empty());
	}

	protected void assertAccessProperties(Access access, BehaviouralEntity accessorEntity, StructuralEntity accessedVariable, Optional<Boolean> isWrite) {
		assertEquals(access.getAccessor(), accessorEntity);
		assertEquals(access.getVariable(), accessedVariable);
		if (isWrite.isPresent()) {
			assertEquals(isWrite.get(), access.getIsWrite());
		}
		
	}
	
	// UTILS MODEL ACCESSING
	
	protected Method methodInType(Type type, String methodName) {
		return type.getMethods()
			.stream()
	        .filter(m -> m.getName().equals(methodName))
	        .findAny()
	        .get();
	}
	
	protected Attribute attributeInType(Type type, String attributeName) {
		return type.getAttributes()
			.stream()
	        .filter(attribute -> attribute.getName().equals(attributeName))
	        .findAny()
	        .get();
	}
	
	protected Parameter parameterInBehaviour(BehaviouralEntity parentBehaviour, String parameterName) {
		List<Parameter> possibleParameters = parentBehaviour.getParameters().stream()
				.filter( aParameter -> aParameter.getName().equals(parameterName))
				.collect(Collectors.toList());
		assertEquals(1, possibleParameters.size());
		return possibleParameters.get(0);
	}
	
	protected Type typeNamed(String typeName) {
		return importer.types()
				.stream()
	            .filter(type -> type.getName().equals(typeName))
	            .findAny()
	            .get();
	}
	
	protected AnnotationType annotationTypeNamed(String annotationTypeName) {
		return (AnnotationType) importer.types()
				.stream()
	            .filter(type -> 
	            		type.getName().equals(annotationTypeName) &&
	            		type instanceof AnnotationType)
	            .findAny()
	            .get();
	}
	
	protected Method methodNamed(String name) {
		return importer.methods()
				.stream()
	            .filter(m -> m.getName().equals(name))
	            .findAny()
	            .get();
	}

	protected Attribute attributeNamed(String name) {
		return importer.attributes()
				.stream()
				.filter(a -> a.getName().equals(name))
				.findAny()
				.get();
	}
	
	protected Type typeWithIndentifier(String typeIdentifier) {
		return importer.types().named(typeIdentifier);
	}
	
	protected Type numberType() {
		return typeWithIndentifier("number");
	}
	
	protected Type booleanType() {
		return typeWithIndentifier("boolean");
	}
	
	protected Type nullType() {
		return typeWithIndentifier("NULL");
	}
	
	protected Type stringType() {
		return typeWithIndentifier("string");
	}
	
	protected Type arrayType() {
		return typeWithIndentifier("array");
	}
	
	
	
	// UTILS IDENTIFIERS
	
	protected String identifierFor(String folderName, String fileName, String typeQualifiedName) {
		//"=pdt2famix-exporter-samples/<samples\\/class_with_simple_methods{class_with_simple_methods.php[ClassWithSimpleMethods";
		return 
			"="  + PROJECT_NAME +
			"/<" + SAMPLES_NAME +
			"\\/"+ folderName +
			"{"  + fileName+ ".php" +
			"[" + typeQualifiedName.replace('$', '[').replace("\\", "\\\\");
	}
	
	protected String typeIdentifier(String typeQualifiedName) {
		return typeIdentifier(Importer.DEFAULT_NAMESPACE_NAME, typeQualifiedName);
	}
	
	protected String typeIdentifier(String namespaceName, String typeName) {
		String sampleDirectoryName = sampleDirectory();
		return identifierFor(sampleDirectoryName, sampleDirectoryName, importer.makeTypeQualifiedNameFrom(namespaceName, typeName));
	}
	
	/**
	 * Ensure that there is a test project.
	 */
	@BeforeClass
	public static void setUpClass() throws CoreException {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		Optional<IProject> testProject = Arrays.asList(root.getProjects())
			.stream()
			.filter(aProject -> aProject.getName().equals(PROJECT_NAME))
			.findFirst();	
		if (testProject.isPresent() == false) {
			throw new RuntimeException("No available project");
			//this.createTestProject(root);
		}
	}
	
	private IProject createTestProject(IWorkspaceRoot root) throws CoreException {
		IProject project = createEmtyProject(root);
		setProjectNature(project);
		
		IJavaProject javaProject = JavaCore.create(project);
		setOutputLocation(project, javaProject);
		setProjectClasspath(javaProject);
		setSourceLocation(project, javaProject);
		
		return project;
	}
	
	private IProject createEmtyProject(IWorkspaceRoot root) throws CoreException {
		IProject project = root.getProject(PROJECT_NAME);
		project.create(null);
		project.open(null);
		return project;
	}
	
	private void setProjectNature(IProject project) throws CoreException {
		IProjectDescription description = project.getDescription();
		description.setNatureIds(new String[] { JavaCore.NATURE_ID });
		project.setDescription(description, null);
	}
	
	private void setOutputLocation(IProject project, IJavaProject javaProject) throws CoreException {
		IFolder binFolder = project.getFolder("bin");
		binFolder.create(false, true, null);
		javaProject.setOutputLocation(binFolder.getFullPath(), null);
	}
	
	private void setProjectClasspath(IJavaProject javaProject) throws JavaModelException {
		List<IClasspathEntry> entries = new ArrayList<IClasspathEntry>();
		IVMInstall vmInstall = JavaRuntime.getDefaultVMInstall();
		LibraryLocation[] locations = JavaRuntime.getLibraryLocations(vmInstall);
		for (LibraryLocation element : locations) {
		 entries.add(JavaCore.newLibraryEntry(element.getSystemLibraryPath(), null, null));
		}
		//Add libs to project class path
		javaProject.setRawClasspath(entries.toArray(new IClasspathEntry[entries.size()]), null);
	}
	
	private void setSourceLocation(IProject project, IJavaProject javaProject) throws CoreException {
		IFolder sourceFolder = project.getFolder("src");
		sourceFolder.create(false, true, null);
		IPackageFragmentRoot root = javaProject.getPackageFragmentRoot(sourceFolder);
		IClasspathEntry[] oldEntries = javaProject.getRawClasspath();
		IClasspathEntry[] newEntries = new IClasspathEntry[oldEntries.length + 1];
		System.arraycopy(oldEntries, 0, newEntries, 0, oldEntries.length);
		newEntries[oldEntries.length] = JavaCore.newSourceEntry(root.getPath());
		javaProject.setRawClasspath(newEntries, null);
	}
	
	
	protected abstract String sampleDirectory();

	protected String annotationTagName() {
		return "Annotation";
	}
	
	protected String defaultAnnotationsNamespaceName() {
		return "annotations";
	}
	
	protected String annotationTagTypeName() {
		return annotationTagName() + AnnotationResolver.ANNOTATION_TAG_PREFIX;
	}
	
	@Before
	public void setUp() throws Exception {
		IScriptProject projectPHP = ProjectHolder.getProject(PROJECT_NAME);
		AnnotationResolver annotationResolver = new AnnotationResolver(annotationTagName(), defaultAnnotationsNamespaceName()); 
		importer = new Importer(projectPHP, annotationResolver);
		importer.run(projectPHP, Arrays.asList(new String[] {"/" + PROJECT_NAME + "/"+ SAMPLES_NAME +"/" + sampleDirectory()}), false);
	}
	
//	@After
//	public void tearDown() throws Exception {
//		projectPHP.close();
//		project.close(null);
//	}
}
