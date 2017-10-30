package pdt2famix_exporter;

import org.eclipse.core.internal.runtime.InternalPlatform;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

	// The plug-in ID
	public static final String PLUGIN_ID = "pdt2famix-exporter"; //$NON-NLS-1$
		
	// The shared instance
	private static Activator plugin;
	
	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
		plugin = null;
	}
	
	public final ILog getLog() {
		return InternalPlatform.getDefault().getLog(getContext().getBundle());
	}
	
	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}
	
	
	public void trace(String message) {
		this.getLog().log(new Status(IStatus.INFO, Activator.PLUGIN_ID, message));
	}

	public void error(String message) {
		this.getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, message));
	}

}
