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
package fr.inria.atlanmod.emftocsp.emf.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.ocl.ecore.Constraint;

import fr.inria.atlanmod.emftocsp.ICspCodeGenerator;
import fr.inria.atlanmod.emftocsp.IModelProperty;
import fr.inria.atlanmod.emftocsp.IModelReader;
import fr.inria.atlanmod.emftocsp.IOclParser;
import fr.inria.atlanmod.emftocsp.ProcessingException;

/**
 * @author <a href="mailto:carlos.gonzalez@inria.fr">Carlos A. González</a>
 *
 */
public abstract class EmfCspCodeGenerator implements ICspCodeGenerator<Resource, EPackage, EClass, EAssociation, EAttribute, EOperation, Constraint> {
  IModelReader<Resource, EPackage, EClass, EAssociation, EAttribute, EOperation> modelReader;
  IOclParser<Constraint, Resource> oclParser;
  List <IModelProperty> properties;
  HashMap <String, String> elementsDomain;
  IFile oclDocument;
  Resource modelResource;
  
  @Override
  public void setModelReader(IModelReader<Resource, EPackage, EClass, EAssociation, EAttribute, EOperation> modelReader) {
    this.modelReader = modelReader;
  }
  
  protected IModelReader<Resource, EPackage, EClass, EAssociation, EAttribute, EOperation> getModelReader() {
    return modelReader;
  }  
  
  @Override
  public void setProperties(List<IModelProperty> properties) {
    this.properties = properties;
  }
  
  protected List<IModelProperty> getProperties() {
    return properties;
  }  
  
  @Override
  public void setModelElementsDomains(Map<String, String> elementsDomain) {
    this.elementsDomain = (HashMap <String, String>)elementsDomain;
  }

  protected Map<String, String> getModelElementsDomain() {
    return elementsDomain;
  }
    
  @Override
  public void setOclParser(IOclParser<Constraint, Resource> oclParser) {
    this.oclParser = oclParser;
  }
  
  protected IOclParser<Constraint, Resource> getOclParser() {
    return oclParser;
  }
  
  @Override
  public void setOclDocument(IFile oclDocument) {
    this.oclDocument = oclDocument;
  }
  
  protected IFile getOclDocument() {
    return oclDocument;
  } 
  
  @Override
  public void setModel(Resource modelResource) {
    this.modelResource = modelResource;
  }
  
  protected Resource getModel() {
    return modelResource;
  } 

  @Override
  public abstract String getCspCode() throws ProcessingException;  

  @Override
  public abstract String getCspCodeFileExtension();  
}
