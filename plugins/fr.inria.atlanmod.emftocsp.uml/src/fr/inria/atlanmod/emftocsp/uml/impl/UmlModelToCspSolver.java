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


import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.resource.UMLResource;

import fr.inria.atlanmod.emftocsp.ICspCodeGenerator;
import fr.inria.atlanmod.emftocsp.IModelReader;
import fr.inria.atlanmod.emftocsp.IOclParser;
import fr.inria.atlanmod.emftocsp.impl.ModelToCspSolver;

/**
 * @author <a href="mailto:carlos.gonzalez@inria.fr">Carlos A. González</a>
 *
 */
public class UmlModelToCspSolver extends ModelToCspSolver<UMLResource> {
  UMLResource umlModelResource;
  UmlModelReader modelReader;
  UmlOclParser oclParser;
  ICspCodeGenerator<UMLResource, ?, ?, ?, ?, ?, ?> cspCodeGenerator;
  
	@Override
	public void setModel(UMLResource modelResource) {
	  umlModelResource = modelResource;
	}

	@Override
	public UMLResource getModel() {
	  return umlModelResource;
	}
	
  @Override
  public String getModelLocation() {
    return umlModelResource.getURI().path();
  }	

  @Override
  public IModelReader<UMLResource, Package, Class, Association, Property, Operation> getModelReader() {
    if (umlModelResource == null)
      return null;
    if (modelReader == null)
      modelReader = new UmlModelReader(umlModelResource);
    if (modelReader.getModelResource() != umlModelResource)
      modelReader = new UmlModelReader(umlModelResource);
    return modelReader;
  }	
  
  @Override
  public IOclParser<Constraint, UMLResource> getOclParser() {
    if (oclParser == null)
      oclParser = new UmlOclParser();
    return oclParser;
  }    
  
  @Override
  public void setCspCodeGenerator(ICspCodeGenerator<UMLResource, ?, ?, ?, ?, ?, ?> cspCodeGenerator) {
    this.cspCodeGenerator = cspCodeGenerator;
  }  

  @SuppressWarnings("unchecked")
  @Override
  public ICspCodeGenerator<UMLResource, Package, Class, Association, Property, Operation, Constraint> getCspCodeGenerator() {
    return (ICspCodeGenerator<UMLResource, Package, Class, Association, Property, Operation, Constraint>)cspCodeGenerator;
  }  
}
