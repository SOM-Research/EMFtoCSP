/*******************************************************************************
 * Copyright (c) 2011 INRIA Rennes Bretagne-Atlantique.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     INRIA Rennes Bretagne-Atlantique - initial API and implementation
 *******************************************************************************/
package fr.inria.atlanmod.emftocsp.ui.main.commands;

import java.net.URL;
import java.util.Map;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.uml2.common.util.UML2Util;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.uml2.uml.resources.util.UMLResourcesUtil;

import fr.inria.atlanmod.emftocsp.adapters.umlImpl.EResourceUMLAdapter;
import fr.inria.atlanmod.emftocsp.ui.commands.AbstractWizardHandler;
import fr.inria.atlanmod.emftocsp.ui.main.Activator;

/**
 * @author <a href="mailto:carlos.gonzalez@inria.fr">Carlos A. González</a>
 *
 */
public class WizardHandler extends AbstractWizardHandler {

	protected String getGVizPathFromPreferenceStore() {

		return Activator.getDefault().getPreferenceStore().getString("GraphvizPath"); //$NON-NLS-1$
	}

	protected String getEclipsePathFromPreferenceStore() {
		return Activator.getDefault().getPreferenceStore().getString("EclipsePath");       //$NON-NLS-1$
	}

	protected int canHandle(String extension) {
		return extension.equals("uml")? 1 : extension.equals("ecore")? 0 : -1 ;	
	}

	private Resource loadEmfResource(URI modelFileURI) {
		ResourceSet rSet = new ResourceSetImpl();
		rSet.setPackageRegistry(EPackage.Registry.INSTANCE);
		return  rSet.getResource(modelFileURI, true);
	}
	@SuppressWarnings("unchecked")
	private Resource loadUmlResource (URI modelUri){
		ResourceSet rSet = new ResourceSetImpl();
		UMLResourcesUtil.init(rSet);
		rSet.getPackageRegistry().put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
		rSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
//		@SuppressWarnings("rawtypes")
		//Map uriMap = rSet.getURIConverter().getURIMap();
		//URI uri = URI.createURI("jar:plugin:/C:/eclipse/plugins/fr.inria.atlanmod.emftocsp.eclipsecs/libs/umlResource.jar!/"); // for example
//		uriMap.put(URI.createURI(UMLResource.LIBRARIES_PATHMAP), modelUri.appendSegment("libraries").appendSegment(""));
//		uriMap.put(URI.createURI(UMLResource.METAMODELS_PATHMAP), modelUri.appendSegment("metamodels").appendSegment(""));
//		uriMap.put(URI.createURI(UMLResource.PROFILES_PATHMAP), modelUri.appendSegment("profiles").appendSegment(""));
		UMLResource r = (UMLResource)rSet.getResource(modelUri, true);
		EcoreUtil.resolveAll(r);	
		UMLResourcesUtil.init(rSet);
		return new EResourceUMLAdapter(r);
	}

	  protected static void registerPathmaps( URI baseUri )
	  {
	    System.out.println( "baseUri = " + baseUri );
	    URIConverter.URI_MAP.put(URI.createURI( UMLResource.LIBRARIES_PATHMAP ), baseUri.appendSegment( "libraries" ).appendSegment( "" ));
	    URIConverter.URI_MAP.put(URI.createURI( UMLResource.METAMODELS_PATHMAP ), baseUri.appendSegment( "metamodels" ).appendSegment( "" ));
	    URIConverter.URI_MAP.put(URI.createURI( UMLResource.PROFILES_PATHMAP ), baseUri.appendSegment( "profiles" ).appendSegment( "" ));
	  }
	

	@Override
	protected Resource loadResource(URI modelFileURI, int i) {
		switch (i) {
		case 0:
			return loadEmfResource(modelFileURI);
		case 1:
			return loadUmlResource(modelFileURI);
		default:
			return null;
		}
	}

}
