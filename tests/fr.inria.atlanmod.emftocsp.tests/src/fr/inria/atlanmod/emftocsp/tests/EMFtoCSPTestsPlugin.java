/*******************************************************************************
 * Copyright (c) 2015 INRIA Rennes Bretagne-Atlantique.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     INRIA Rennes Bretagne-Atlantique - initial API and implementation
 *******************************************************************************/
package fr.inria.atlanmod.emftocsp.tests;

import java.net.URI;
import java.util.UUID;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 * @author <a href="mailto:hamza.eddouibi@inria.fr">Hamza Ed-Douibi</a>
 */
public class EMFtoCSPTestsPlugin extends AbstractUIPlugin {

	// The plug-in ID
	public static final String PLUGIN_ID = "fr.inria.atlanmod.emftocsp.tests"; //$NON-NLS-1$

	// The shared instance
	private static EMFtoCSPTestsPlugin plugin;

	private IProject emfToCspProject;

	/**
	 * The constructor
	 */
	public EMFtoCSPTestsPlugin() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		emfToCspProject = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(UUID.randomUUID().toString());
		emfToCspProject.create(new NullProgressMonitor());
		if (!emfToCspProject.isOpen()) {
			emfToCspProject.open(new NullProgressMonitor());
//			emfToCspProject.setHidden(true);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static EMFtoCSPTestsPlugin getDefault() {
		return plugin;
	}

	/**
	 * Return an IFile given the URI of a file in the filesystem
	 * @param uri
	 * @return an IFile given the URI of a file in the filesystem 
	 * @throws CoreException
	 */
	public IFile getFile(URI uri) throws CoreException {
		IFile file = emfToCspProject.getFile("linkedFile"+UUID.randomUUID().toString());
		file.createLink(uri, IResource.NONE, new NullProgressMonitor());
		return file;
	}
	/**
	 * Return an IFolder given the URI of a folder in the filesystem
	 * @param uri
	 * @return an IFolder given the URI of a folder in the filesystem 
	 * @throws CoreException
	 */
	public IFolder getFolder(URI uri) throws CoreException {
		IFolder folder = emfToCspProject.getFolder("folder"+UUID.randomUUID().toString());
		folder.createLink(uri, IResource.NONE, new NullProgressMonitor());
		return folder;
	}
	/**
	 * Return a temporary IFolder to store the results
	 * @param uri
	 * @return a temporary IFolder to store the results 
	 * @throws CoreException
	 */
	public IFolder getResultFolder() throws CoreException {
		IFolder folder = emfToCspProject.getFolder("result"+UUID.randomUUID().toString());
		folder.create(IResource.NONE, true, new NullProgressMonitor());
		return folder;
	}
}
