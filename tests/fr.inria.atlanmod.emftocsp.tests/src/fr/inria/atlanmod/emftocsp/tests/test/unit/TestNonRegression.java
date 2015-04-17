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
package fr.inria.atlanmod.emftocsp.tests.test.unit;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.uml2.uml.resources.util.UMLResourcesUtil;
import org.osgi.framework.FrameworkUtil;

import com.parctechnologies.eclipse.CompoundTerm;

import fr.inria.atlanmod.emftocsp.ICspSolver;
import fr.inria.atlanmod.emftocsp.IModelProperty;
import fr.inria.atlanmod.emftocsp.IModelReader;
import fr.inria.atlanmod.emftocsp.IModelToCspSolver;
import fr.inria.atlanmod.emftocsp.IModelToCspSolverFactory;
import fr.inria.atlanmod.emftocsp.ProcessingException;
import fr.inria.atlanmod.emftocsp.adapters.umlImpl.EResourceUMLAdapter;
import fr.inria.atlanmod.emftocsp.eclipsecs.EclipseSolver;
import fr.inria.atlanmod.emftocsp.emf.impl.EAssociation;
import fr.inria.atlanmod.emftocsp.emf.impl.EmfModelToCspSolverFactory;
import fr.inria.atlanmod.emftocsp.emftoecl.EmfToEclCodeGenerator;
import fr.inria.atlanmod.emftocsp.impl.FileLogger;
import fr.inria.atlanmod.emftocsp.impl.LackOfConstraintsRedundanciesModelProperty;
import fr.inria.atlanmod.emftocsp.impl.LackOfConstraintsSubsumptionsModelProperty;
import fr.inria.atlanmod.emftocsp.impl.LivelinessModelProperty;
import fr.inria.atlanmod.emftocsp.impl.StrongSatisfiabilityModelProperty;
import fr.inria.atlanmod.emftocsp.impl.WeakSatisfiabilityModelProperty;
import fr.inria.atlanmod.emftocsp.tests.EMFtoCSPTestsPlugin;

/**
 * @author <a href="mailto:hamza.eddouibi@inria.fr">Hamza Ed-Douibi</a>
 */
public class TestNonRegression {

	private URI modelURI;
	private Properties properties;
	private Properties config;
	String modelName;
	private String extension;
	private String logFileName;
	private Map<String, String> modelElementsDomain;
	private IModelToCspSolver<Resource, CompoundTerm> modelSolver;

	public TestNonRegression(URI modelURI, URL propertiesURL, Properties config) {
		super();
		this.modelURI = modelURI;
		this.config = config;
		extension = modelURI.fileExtension();
		modelName = modelURI.lastSegment();
		properties = new Properties();
		try {
			properties.load(FileLocator.resolve(propertiesURL).openStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean runValidation() throws IOException, CoreException {
		if (canHandle(extension) > -1) {

			Resource resource = loadResource(modelURI, canHandle(extension));
			return execute(modelName, config.getProperty("eclipsePath"),
					config.getProperty("graphvizPath"), resource);
		}
		return false;
	}

	static class StringAttributeContainer {
		public enum Kind {
			LENGTH, DOMAIN
		};

		public EAttribute attribute;
		public Kind kind;

		public StringAttributeContainer(EAttribute a, Kind k) {
			attribute = a;
			kind = k;
		}

		public String getKey() {
			String base = attribute.getEContainingClass().getName() + "."
					+ attribute.getName();
			if (kind == Kind.LENGTH) {
				return base + ".length";
			}
			if (kind == Kind.DOMAIN) {
				return base + ".domain";
			}
			throw new RuntimeException();
		}
	}

	private void initializeModelElementsDomain() {
		modelElementsDomain = modelSolver.getModelElementsDomain();
		if (modelElementsDomain != null)
			return;
		modelElementsDomain = new HashMap<String, String>();

		@SuppressWarnings("unchecked")
		IModelReader<Resource, EPackage, EClass, EAssociation, EAttribute, EOperation> modelReader = (IModelReader<Resource, EPackage, EClass, EAssociation, EAttribute, EOperation>) modelSolver
				.getModelReader();
		List<EClass> cList = modelReader.getClasses();
		for (EClass c : cList) {
			modelElementsDomain
					.put(c.getEPackage().getName() + "." + c.getName(), properties.getProperty(c.getEPackage().getName() + "." + c.getName(), "0..5")); //$NON-NLS-1$ //$NON-NLS-2$
			List<EAttribute> atList = modelReader.getClassAttributes(c);
			for (EAttribute at : atList) {
				String attName = at.getEAttributeType().getName();
				if (attName.equalsIgnoreCase("boolean") || attName.equalsIgnoreCase("boolean")) //$NON-NLS-1$
					modelElementsDomain.put(
							at.getEContainingClass().getName() + "."
									+ at.getName(),
							properties.getProperty(at.getEContainingClass()
									.getName() + "." + at.getName(), "0..1")); //$NON-NLS-1$ //$NON-NLS-2$
				else if (attName.equalsIgnoreCase("string")
						|| attName.equalsIgnoreCase("estring")) {
					modelElementsDomain.put(at.getEContainingClass().getName()
							+ "." + at.getName() + ".length", properties
							.getProperty(at.getEContainingClass().getName()
									+ "." + at.getName() + ".length", "0..10")); //$NON-NLS-1$ //$NON-NLS-2$
					modelElementsDomain.put(at.getEContainingClass().getName()
							+ "." + at.getName() + ".domain", properties
							.getProperty(at.getEContainingClass().getName()
									+ "." + at.getName() + ".domain", "")); //$NON-NLS-1$ //$NON-NLS-2$
				} else
					modelElementsDomain.put(at.getEContainingClass().getName()
							+ "." + at.getName(), properties.getProperty(
							at.getEContainingClass().getName()
									+ "." + at.getName(), "[1,10,20]")); //$NON-NLS-1$ //$NON-NLS-2$
			}
			List<String> asNames = modelReader.getAssociationsNames();
			for (String asName : asNames)
				modelElementsDomain.put(asName,
						properties.getProperty(asName, "0..10")); //$NON-NLS-1$
		}
		modelSolver.setModelElementsDomain(modelElementsDomain);
	}

	protected int canHandle(String extension) {
		return extension.equals("uml") ? 1 : extension.equals("ecore") ? 0 : -1;
	}

	private Resource loadEmfResource(URI modelFileURI) {
		ResourceSet rSet = new ResourceSetImpl();
		rSet.setPackageRegistry(EPackage.Registry.INSTANCE);
		return rSet.getResource(modelFileURI, true);
	}

	private Resource loadUmlResource(URI modelUri) {
		ResourceSet rSet = new ResourceSetImpl();
		UMLResourcesUtil.init(rSet);
		rSet.getPackageRegistry().put(UMLPackage.eNS_URI, UMLPackage.eINSTANCE);
		rSet.getResourceFactoryRegistry().getExtensionToFactoryMap()
				.put(UMLResource.FILE_EXTENSION, UMLResource.Factory.INSTANCE);
		UMLResource r = (UMLResource) rSet.getResource(modelUri, true);
		EcoreUtil.resolveAll(r);
		UMLResourcesUtil.init(rSet);
		return new EResourceUMLAdapter(r);
	}

	/**
	 * @author <a href="mailto:amine.benelallam@inria.fr">Amine Benelallam</a>
	 *
	 */
	protected static void registerPathmaps(URI baseUri) {
		System.out.println("baseUri = " + baseUri);
		URIConverter.URI_MAP.put(URI.createURI(UMLResource.LIBRARIES_PATHMAP),
				baseUri.appendSegment("libraries").appendSegment(""));
		URIConverter.URI_MAP.put(URI.createURI(UMLResource.METAMODELS_PATHMAP),
				baseUri.appendSegment("metamodels").appendSegment(""));
		URIConverter.URI_MAP.put(URI.createURI(UMLResource.PROFILES_PATHMAP),
				baseUri.appendSegment("profiles").appendSegment(""));
	}

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

	public boolean execute(String modelFileName, String eclipsePath,
			String graphvizPath, Resource resource) throws CoreException {
		boolean result = false;
		// initiate the solver
		@SuppressWarnings("rawtypes")
		ICspSolver solver = new EclipseSolver(eclipsePath, graphvizPath);
		IModelToCspSolverFactory<Resource, CompoundTerm> modelSolverFactory = new EmfModelToCspSolverFactory();
		modelSolver = modelSolverFactory.getModelToCspSolver();
		modelSolver.setModelFileName(modelFileName);
		modelSolver.setModel(resource);
		modelSolver.setSolver(solver);
		modelSolver.setCspCodeGenerator(new EmfToEclCodeGenerator(modelSolver));
		modelSolver.setLogger(new FileLogger());
		modelSolver.getLogger().writeInfoMessage(this.getClass().toString(),
				"Starting EMFtoCSP GUI");
		modelSolver.getBuilder();

		// set OCL file
		try {
			if (properties.getProperty("oclFile") != null
					&& !properties.getProperty("oclFile").trim().equals(""))
				modelSolver
						.setConstraintsDocument(EMFtoCSPTestsPlugin
								.getDefault()
								.getFile(
										FileLocator
												.resolve(
														Platform.getBundle(
																EMFtoCSPTestsPlugin.PLUGIN_ID)
																.getEntry(
																		"data/models/"
																				+ extension
																				+ "/"
																				+ properties
																						.getProperty("oclFile")))
												.toURI()));
		} catch (URISyntaxException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		// propertoes
		List<IModelProperty> modelProperties = new ArrayList<IModelProperty>();
		if (Boolean.valueOf(properties.getProperty("strongSatisfiability",
				"false")))
			modelProperties.add(new StrongSatisfiabilityModelProperty());
		if (Boolean.valueOf(properties.getProperty("weakSatisfiability",
				"false")))
			modelProperties.add(new WeakSatisfiabilityModelProperty());
		if (Boolean.valueOf(properties.getProperty("liveliness", "false"))) {
			ArrayList<String> modelElementsNames = new ArrayList<String>();
			modelElementsNames.add(properties.getProperty("livelinessClass"));
			modelProperties
					.add(new LivelinessModelProperty(modelElementsNames));
		}
		if (Boolean.valueOf(properties.getProperty(
				"lackOfConstraintsSubsumptions", "false"))) {
			ArrayList<String> modelElementsNames = new ArrayList<String>();
			modelElementsNames
					.add(properties
							.getProperty("lackOfConstraintsSubsumptions1") + "," + properties.getProperty("lackOfConstraintsSubsumptions2")); //$NON-NLS-1$
			modelProperties.add(new LackOfConstraintsSubsumptionsModelProperty(
					modelElementsNames));
		}
		if (Boolean.valueOf(properties.getProperty(
				"lackOfConstraintsRedundancies", "false"))) {
			ArrayList<String> modelElementsNames = new ArrayList<String>();
			modelElementsNames
					.add(properties
							.getProperty("lackOfConstraintsRedundancies1") + "," + properties.getProperty("lackOfConstraintsRedundancies2")); //$NON-NLS-1$
			modelProperties.add(new LackOfConstraintsRedundanciesModelProperty(
					modelElementsNames));
		}
		modelSolver.setModelProperties(modelProperties);
		modelSolver.setResultLocation(EMFtoCSPTestsPlugin.getDefault()
				.getResultFolder());
		logFileName = modelSolver.getResultLocation().getRawLocation()
				.append(modelSolver.getModelFileName() + ".log").toOSString();
		initializeModelElementsDomain();
		try {
			File importsFolder;
			try {
				importsFolder = new File(
						FileLocator
								.toFileURL(
										FrameworkUtil
												.getBundle(
														fr.inria.atlanmod.emftocsp.eclipsecs.EclipseSolver.class)
												.getEntry("/libs")).toURI());
			} catch (URISyntaxException e) {
				throw new ProcessingException(e);
			} catch (IOException e) {
				throw new ProcessingException(e);
			}
			File[] libs = importsFolder.listFiles(new FilenameFilter() {
				public boolean accept(File dir, String name) {
					return name.matches(".*\\.ecl$"); //$NON-NLS-1$
				}
			});
			ArrayList<File> libList = new ArrayList<File>();
			for (int i = 0; i < libs.length; i++)
				libList.add(libs[i]);
			result = modelSolver.solveModel(libList);

			modelSolver.getLogger().writeInfoMessage(
					this.getClass().toString(), "Closing EMFtoCSP");
			modelSolver.getLogger().close(logFileName);

		} catch (Exception e) {
			e.printStackTrace();
			modelSolver.getLogger().writeInfoMessage(
					this.getClass().toString(), "Closing EMFtoCSP");

		}
		return result;
	}

}
