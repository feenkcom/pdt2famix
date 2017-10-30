package com.feenk.pdt2famix.test.support;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Arrays;
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

public abstract class InPhpTestCase {
	private static final String PROJECT_NAME = "pdt2famix-exporter-samples";
	private static final String SAMPLES_LOCATION = "/" + PROJECT_NAME + "/samples/";
	
	protected Importer importer;
	
	
	// ASSERTIONS
	
	public void assertEmptyNamespace(String qualifiedName) {
		assertEmptyNamespace(qualifiedName, null);
	}
	
	public void assertEmptyNamespace(String namespaceQualifiedName, String parentNamespaceQualifiedName) {
		Namespace obtainedNamespace = importer.namespaces().named(namespaceQualifiedName);
		Namespace parentNamespace = null;
		
		assertEquals(namespaceNameFrom(namespaceQualifiedName), obtainedNamespace.getName());
		assertEquals(true, obtainedNamespace.getIsStub());
		assertEquals(0, obtainedNamespace.getGlobalVariables().size());
		assertEquals(0, obtainedNamespace.getTypes().size());
		assertEquals(0, obtainedNamespace.getFunctions().size());
		assertEquals(0, obtainedNamespace.getReceivingInvocations().size());
		assertEquals(0, obtainedNamespace.getDefinedAnnotationTypes().size());
		assertEquals(0, obtainedNamespace.getComments().size());
		assertEquals(0, obtainedNamespace.getAnnotationInstances().size());
		
		if (parentNamespaceQualifiedName != null) {
			parentNamespace = importer.namespaces().named(parentNamespaceQualifiedName);
		}
		assertEquals(parentNamespace, obtainedNamespace.getParentScope());
	}
	
	
	private String namespaceNameFrom(String namespaceQualifiedName) {
		int lastIndexOfBackslash = namespaceQualifiedName.lastIndexOf("\\");
		Namespace namespace = new Namespace();
		namespace.setIsStub(true);
		if (lastIndexOfBackslash <= 0) {
			return namespaceQualifiedName;
		} else {
			return namespaceQualifiedName.substring(lastIndexOfBackslash+1);
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
