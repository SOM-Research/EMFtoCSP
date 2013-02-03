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
package fr.inria.atlanmod.emftocsp;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;

/**
 * @author <a href="mailto:carlos.gonzalez@inria.fr">Carlos A. González</a>
 *
 */
public interface IModelToCspSolver<R> {
	
	public void setModel(R modelResource);
	
	public R getModel();
	
  public void setModelFileName(String filename);
  
	public String getModelFileName();
	
	public String getModelLocation();
	
	public IModelReader<R, ?, ?, ?, ?, ?> getModelReader();

  public IOclParser<?, R> getOclParser();
	
  public void setCspCodeGenerator(ICspCodeGenerator<R, ?, ?, ?, ?, ?, ?> cspCodeGenerator);
  
	public ICspCodeGenerator<R, ?, ?, ?, ?, ?, ?> getCspCodeGenerator();

	public void setConstraintsDocument(IFile constraintsDocument);
		
	public IFile getConstraintsDocument();
	
  public void setModelElementsDomain(Map<String, String> modelDomain);
  
	public Map<String, String> getModelElementsDomain();
	
	public void setResultLocation(IFolder resultLocation);
	
	public IFolder getResultLocation();
	
  public void setModelProperties(List<IModelProperty> modelProperties);
  
	public List<IModelProperty> getModelProperties();
	
	public void setSolver(ICspSolver solver);
	
	public ICspSolver getSolver();
	
	public boolean solveModel() throws ProcessingException ;

  public boolean solveModel(List<File> importLibs) throws ProcessingException ;	
  
  public Object getSolverEvaluationResult();
  
  public void setLogger(ILogger logger);
  
  public ILogger getLogger();
}
