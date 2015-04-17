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
package fr.inria.atlanmod.emftocsp.tests.test;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Properties;

import org.junit.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Platform;
import org.eclipse.emf.common.util.URI;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.osgi.framework.Bundle;

import fr.inria.atlanmod.emftocsp.tests.EMFtoCSPTestsPlugin;
import fr.inria.atlanmod.emftocsp.tests.test.unit.TestNonRegression;

/**
 * @author <a href="mailto:hamza.eddouibi@inria.fr">Hamza Ed-Douibi</a>
 */
@RunWith(Parameterized.class)
public class TestModelGeneration {

	public static Properties CONFIG;
	private static Bundle BUNDLE = Platform
			.getBundle(EMFtoCSPTestsPlugin.PLUGIN_ID);

	static {
		CONFIG = new Properties();
		try {
			CONFIG.load(FileLocator.resolve(
					BUNDLE.getEntry("data/config.properties")).openStream());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Parameters
	public static Collection<Object[]> data() throws IOException {
		return Arrays
				.asList(new Object[][] {
						{
								URI.createFileURI("./data/models/ecore/eShopOCLSAT.ecore"),
								BUNDLE.getEntry("data/properties/ecore/eShopOCLSAT.properties"),
								true },
						{
								URI.createFileURI("./data/models/ecore/eShop.ecore"),
								BUNDLE.getEntry("data/properties/ecore/eShop.ecore.SAT.properties"),
								true },
						{
								URI.createFileURI("./data/models/ecore/abstractClassSample.ecore"),
								BUNDLE.getEntry("data/properties/ecore/abstractClassSample.ecore.properties"),
								true },
						{
								URI.createFileURI("./data/models/ecore/abstractClassSampleNoSpec.ecore"),
								BUNDLE.getEntry("data/properties/ecore/abstractClassSampleNoSpec.ecore.properties"),
								true },
						{
								URI.createFileURI("./data/models/ecore/ER.ecore"),
								BUNDLE.getEntry("data/properties/ecore/ER.ecore.properties"),
								true },
						{
								URI.createFileURI("./data/models/ecore/ERStrings.ecore"),
								BUNDLE.getEntry("data/properties/ecore/ERStrings.ecore.properties"),
								true },
						{
								URI.createFileURI("./data/models/ecore/let.ecore"),
								BUNDLE.getEntry("data/properties/ecore/let.ecore.properties"),
								true },
						{
								URI.createFileURI("./data/models/ecore/MultiLevelSpec.ecore"),
								BUNDLE.getEntry("data/properties/ecore/MultiLevelSpec.ecore.properties"),
								true },
						{
								URI.createFileURI("./data/models/ecore/nestedPackage.ecore"),
								BUNDLE.getEntry("data/properties/ecore/nestedPackage.ecore.properties"),
								true },
						{
								URI.createFileURI("./data/models/ecore/oclAt.ecore"),
								BUNDLE.getEntry("data/properties/ecore/oclAt.ecore.properties"),
								true },
						{
								URI.createFileURI("./data/models/ecore/oclIsTypeOf.ecore"),
								BUNDLE.getEntry("data/properties/ecore/oclIsTypeOf.ecore.properties"),
								true },
						{
								URI.createFileURI("./data/models/ecore/Papers-ResearchersOCL.ecore"),
								BUNDLE.getEntry("data/properties/ecore/Papers-ResearchersOCL.ecore.properties"),
								false },
						{
								URI.createFileURI("./data/models/ecore/Papers-Researchers.ecore"),
								BUNDLE.getEntry("data/properties/ecore/Papers-Researchers.ecore.properties"),
								false },
						{
								URI.createFileURI("./data/models/uml/ER.uml"),
								BUNDLE.getEntry("data/properties/uml/ER.uml.properties"),
								true },
						{
								URI.createFileURI("./data/models/uml/eShop.uml"),
								BUNDLE.getEntry("data/properties/uml/eShop.uml.properties"),
								true },
						{
								URI.createFileURI("./data/models/uml/eShopOCL.uml"),
								BUNDLE.getEntry("data/properties/uml/eShopOCL.uml.properties"),
								true },
						{
								URI.createFileURI("./data/models/uml/eShopOCLGold.uml"),
								BUNDLE.getEntry("data/properties/uml/eShopOCLGold.uml.properties"),
								true },
						{
								URI.createFileURI("./data/models/uml/eShopOCLmincStock.uml"),
								BUNDLE.getEntry("data/properties/uml/eShopOCLmincStock.uml.properties"),
								true },
						{
								URI.createFileURI("./data/models/uml/Papers-ResearchersOCL.uml"),
								BUNDLE.getEntry("data/properties/uml/Papers-ResearchersOCL.uml.properties"),
								false },
						{
								URI.createFileURI("./data/models/uml/Pet_Abstract.uml"),
								BUNDLE.getEntry("data/properties/uml/Pet_Abstract.uml.properties"),
								true } });

	}

	private URI modelURI;

	private URL propertiesURL;

	private Boolean flag;
	private TestNonRegression testNonRegression;

	public TestModelGeneration(URI uri, URL url, boolean flag) {
		this.modelURI = uri;
		this.propertiesURL = url;
		this.flag = flag;
	}

	@Before
	public void inittialize() {
		testNonRegression = new TestNonRegression(modelURI, propertiesURL,
				CONFIG);
	}

	@org.junit.Test
	public void test() throws IOException, CoreException {
		System.out.println(modelURI);
		System.out.println(propertiesURL);
		Assert.assertTrue("test", flag == testNonRegression.runValidation());
	}

}
