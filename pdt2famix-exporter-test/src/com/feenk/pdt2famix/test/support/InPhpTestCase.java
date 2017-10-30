package com.feenk.pdt2famix.test.support;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.dltk.core.DLTKCore;
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

import com.feenk.pdt2famix.Importer;
import com.feenk.pdt2famix.model.famix.Namespace;
import com.feenk.pdt2famix.model.famix.Type;

public abstract class InPhpTestCase {
	private static final String PROJECT_NAME = "pdt2famix-exporter-samples";
	private static final String SAMPLES_LOCATION = "/" + PROJECT_NAME + "/samples/";
	
	protected static final String EMPTY_NAMESPACE_NAME = "";
	
	protected Importer importer;
	
	
	// ASSERTIONS NAMESPACES
	
	protected void assertNamespacePresent(String namespaceQualifiedName) {
		Namespace obtainedNamespace = null;
		
		assertTrue(importer.namespaces().has(namespaceQualifiedName));
		
		obtainedNamespace = importer.namespaces().named(namespaceQualifiedName);
		assertEquals(basenameNameFrom(namespaceQualifiedName), obtainedNamespace.getName());
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
	
	protected void assertNamespacesPresent(String[] namespacesQualifiedNames) {
		assertEquals(namespacesQualifiedNames.length, importer.namespaces().size());
		Arrays.asList(namespacesQualifiedNames).stream().forEach(
			qualifiedNamespaceName -> assertNamespacePresent(qualifiedNamespaceName));
		
	}
	
	protected void assertNamespaceTypes(String namespaceQualifiedName, String[] typesQualifiedNames) {
		Namespace namespace = importer.namespaces().named(namespaceQualifiedName);
		Collection<Type> obtainedTypes = namespace.getTypes();
		ArrayList<Type>  expectedTypes = new ArrayList<>();
		
		Arrays.asList(typesQualifiedNames).stream().forEach(
			typeQualifiedName -> assertEquals(namespace, importer.types().named(typeQualifiedName).getContainer()));
		
		Arrays.asList(typesQualifiedNames).stream().forEach(
			typeQualifiedName -> expectedTypes.add(importer.types().named(typeQualifiedName)));
		assertArrayEquals(expectedTypes.toArray(), obtainedTypes.toArray());
	}
	
	// ASSERTIONS CLASSES
	
	protected void assertClassPresent(String classQualifiedname) {
		Type classType = importer.types().named(classQualifiedname);
		
		assertEquals(basenameNameFrom(classQualifiedname), classType.getName());
		assertEquals(false, classType.getIsStub());
		assertTrue(classType instanceof com.feenk.pdt2famix.model.famix.Class);
		assertEquals(false, ((com.feenk.pdt2famix.model.famix.Class)(classType)).getIsInterface());
	}
	
	private String basenameNameFrom(String qualifiedName) {
		int lastIndexOfBackslash = qualifiedName.lastIndexOf("\\");
		Namespace namespace = new Namespace();
		if (lastIndexOfBackslash < 0) {
			return qualifiedName;
		} else {
			return qualifiedName.substring(lastIndexOfBackslash+1);
		}
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
	
	@Before
	public void setUp() throws Exception {
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IProject project = root.getProject(PROJECT_NAME);
		project.open(null /* IProgressMonitor */);
		IScriptProject projectPHP = DLTKCore.create(project);

		importer = new Importer();
		importer.run(projectPHP, Arrays.asList(new String[] { SAMPLES_LOCATION + 	sampleDirectory() }));
	}
	
	protected abstract String sampleDirectory();

}
