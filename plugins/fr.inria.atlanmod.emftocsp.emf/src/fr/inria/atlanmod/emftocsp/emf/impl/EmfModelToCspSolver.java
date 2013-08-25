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

import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.ocl.ecore.Constraint;

import com.parctechnologies.eclipse.CompoundTerm;

import fr.inria.atlanmod.emftocsp.ICspCodeGenerator;
import fr.inria.atlanmod.emftocsp.IModelBuilder;
import fr.inria.atlanmod.emftocsp.IModelReader;
import fr.inria.atlanmod.emftocsp.IOclParser;
import fr.inria.atlanmod.emftocsp.adapters.umlImpl.EResourceUMLAdapter;
import fr.inria.atlanmod.emftocsp.impl.ModelToCspSolver;
import fr.inria.atlanmod.emftocsp.modelbuilder.Point;

/**
 * @author <a href="mailto:carlos.gonzalez@inria.fr">Carlos A. González</a>
 *
 */
public class EmfModelToCspSolver extends ModelToCspSolver<Resource,CompoundTerm> {
	Resource emfModelResource;
	EmfModelReader modelReader;
	IOclParser<Constraint, Resource> oclParser;
	ICspCodeGenerator<Resource, EPackage, EClass, EAssociation, EAttribute, EOperation,Constraint> cspCodeGenerator;
	
	@Override
	public void setModel(Resource modelResource) {
		emfModelResource = modelResource;		
	}

	@Override
	public Resource getModel() {
		return emfModelResource;
	}
	
	@Override
	public String getModelLocation() {
	  return emfModelResource.getURI().path();
	}

  @Override
  public IModelReader<Resource, EPackage, EClass, EAssociation, EAttribute, EOperation> getModelReader() {
    if (emfModelResource == null)
      return null;
    if (modelReader == null)
      modelReader = new EmfModelReader(emfModelResource);
    if (modelReader.getModelResource() != emfModelResource)
      modelReader = new EmfModelReader(emfModelResource);
    return modelReader;
  }
  
  @Override
  public IModelBuilder<Resource, EPackage, EClass, EAssociation, EAttribute, EOperation, CompoundTerm> getBuilder() {
	  if (emfModelResource == null)
	      return null;
	    if (builder == null)
	    	if (emfModelResource instanceof EResourceUMLAdapter)
	    		builder = new UmlModelBuilder(getModelReader());
	    	else	
	      builder = new EmfModelBuilder(getModelReader());
  	return (IModelBuilder<Resource, EPackage, EClass, EAssociation, EAttribute, EOperation, CompoundTerm>) builder;
  }
  
  @Override
  public IOclParser<Constraint, Resource> getOclParser() {
    if (oclParser == null)
      oclParser = new EmfOclParser();
    return oclParser;
  }  
  
  @Override
  public void setCspCodeGenerator(ICspCodeGenerator<Resource, ?, ?, ?, ?, ?, ?> cspCodeGenerator) {
    this.cspCodeGenerator = (ICspCodeGenerator<Resource, EPackage, EClass, EAssociation, EAttribute, EOperation, Constraint>) cspCodeGenerator;
  }  

  @SuppressWarnings("unchecked")
  @Override
  public ICspCodeGenerator<Resource, EPackage, EClass, EAssociation, EAttribute, EOperation, Constraint> getCspCodeGenerator() {
    return (ICspCodeGenerator<Resource, EPackage, EClass, EAssociation, EAttribute, EOperation, Constraint>)cspCodeGenerator;
  }

@Override
public void setBuilder(
		IModelBuilder<Resource, ?, ?, ?, ?, ?, CompoundTerm> builder) {
	this.builder = builder;
	
}








}
