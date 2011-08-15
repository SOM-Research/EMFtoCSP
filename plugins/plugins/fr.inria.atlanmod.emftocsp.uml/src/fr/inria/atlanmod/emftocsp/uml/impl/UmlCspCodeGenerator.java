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
package fr.inria.atlanmod.emftocsp.uml.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.resource.UMLResource;

import fr.inria.atlanmod.emftocsp.ICspCodeGenerator;
import fr.inria.atlanmod.emftocsp.IModelProperty;
import fr.inria.atlanmod.emftocsp.IModelReader;
import fr.inria.atlanmod.emftocsp.IOclParser;

/**
 * @author <a href="mailto:carlos.gonzalez@inria.fr">Carlos A. González</a>
 *
 */
public abstract class UmlCspCodeGenerator implements ICspCodeGenerator<UMLResource, Package, Class, Association, Property, Operation, Constraint> {
  IModelReader<UMLResource, Package, Class, Association, Property, Operation> modelReader;
  IOclParser<Constraint, UMLResource> oclParser;
  List <IModelProperty> properties;
  HashMap <String, String> elementsDomain;
  IFile oclDocument;
  UMLResource modelResource;
  
  @Override
  public void setModelReader(IModelReader<UMLResource, Package, Class, Association, Property, Operation> modelReader) {
    this.modelReader = modelReader;
  }
  
  protected IModelReader<UMLResource, Package, Class, Association, Property, Operation> GetModelReader() {
    return modelReader;
  }
  
  @Override
  public void setProperties(List<IModelProperty> properties) {
    this.properties = properties;
  }

  protected List<IModelProperty> GetProperties() {
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
  public void setOclParser(IOclParser<Constraint, UMLResource> oclParser) {
    this.oclParser = oclParser;
  }

  protected IOclParser<Constraint, UMLResource> GetOclParser() {
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
  public void setModel(UMLResource modelResource) {
    this.modelResource = modelResource;
  }
  
  protected UMLResource GetModel() {
    return modelResource;
  } 
    
  @Override
  public abstract String getCspCode();
  
  @Override
  public abstract String getCspCodeFileExtension();
}
