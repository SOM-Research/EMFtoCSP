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

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.ocl.ecore.Constraint;

import fr.inria.atlanmod.emftocsp.ICspCodeGenerator;
import fr.inria.atlanmod.emftocsp.IModelReader;
import fr.inria.atlanmod.emftocsp.IOclParser;
import fr.inria.atlanmod.emftocsp.impl.ModelToCspSolver;

/**
 * @author <a href="mailto:carlos.gonzalez@inria.fr">Carlos A. González</a>
 *
 */
public class EmfModelToCspSolver extends ModelToCspSolver<Resource> {
	Resource emfModelResource;
	EmfModelReader modelReader;
	EmfOclParser oclParser;
	ICspCodeGenerator<Resource, ?, ?, ?, ?, ?, ?> cspCodeGenerator;
	
	@Override
	public void setModel(Resource modelResource) {
		emfModelResource = modelResource;		
	}

	@Override
	public Resource getModel() {
		return emfModelResource;
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
  public IOclParser<Constraint, Resource> getOclParser() {
    if (oclParser == null)
      oclParser = new EmfOclParser();
    return oclParser;
  }  
  
  @Override
  public void setCspCodeGenerator(ICspCodeGenerator<Resource, ?, ?, ?, ?, ?, ?> cspCodeGenerator) {
    this.cspCodeGenerator = cspCodeGenerator;
  }  

  @SuppressWarnings("unchecked")
  @Override
  public ICspCodeGenerator<Resource, EPackage, EClass, EAssociation, EAttribute, EOperation, Constraint> getCspCodeGenerator() {
    return (ICspCodeGenerator<Resource, EPackage, EClass, EAssociation, EAttribute, EOperation, Constraint>)cspCodeGenerator;
  }
}
