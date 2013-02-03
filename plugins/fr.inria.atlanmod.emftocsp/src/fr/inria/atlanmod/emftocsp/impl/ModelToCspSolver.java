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
package fr.inria.atlanmod.emftocsp.impl;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IPath;

import fr.inria.atlanmod.emftocsp.ICspCodeGenerator;
import fr.inria.atlanmod.emftocsp.ICspSolver;
import fr.inria.atlanmod.emftocsp.ILogger;
import fr.inria.atlanmod.emftocsp.IModelProperty;
import fr.inria.atlanmod.emftocsp.IModelReader;
import fr.inria.atlanmod.emftocsp.IModelToCspSolver;
import fr.inria.atlanmod.emftocsp.IOclParser;
import fr.inria.atlanmod.emftocsp.ProcessingException;
import fr.inria.atlanmod.emftocsp.ProcessingException;

/**
 * @author <a href="mailto:carlos.gonzalez@inria.fr">Carlos A. González</a>
 *
 */
public abstract class ModelToCspSolver<R> implements IModelToCspSolver<R> {
  IFile constraintsDocument;
  IFolder resultLocation;
  HashMap<String, String> modelElementsDomain;
  List<IModelProperty> modelProperties;
  ICspSolver solver;
  String modelFilename;
  ILogger logger;

  @Override
  public abstract void setModel(R modelResource);
  
  @Override
  public abstract R getModel();
  
  @Override
  public void setModelFileName(String filename) {
    modelFilename = filename;
  }
  
  @Override
  public String getModelFileName() {
    return modelFilename;
  }
  
  @Override
  public abstract String getModelLocation();
  
  @Override
  public abstract IModelReader<R, ?, ?, ?, ?, ?> getModelReader();

  @Override
  public abstract IOclParser<?, R> getOclParser();

  @Override
  public abstract void setCspCodeGenerator(ICspCodeGenerator<R, ?, ?, ?, ?, ?, ?> cspCodeGenerator);

  @Override
  public abstract ICspCodeGenerator<R, ?, ?, ?, ?, ?, ?> getCspCodeGenerator();
  
  @Override
  public void setConstraintsDocument(IFile constraintsDocument) {
    this.constraintsDocument = constraintsDocument;
  }

  @Override
  public IFile getConstraintsDocument() {
    return constraintsDocument;
  }
  
  @Override
  public void setModelElementsDomain(Map<String, String> modelElementsDomain) {
    this.modelElementsDomain = (HashMap<String, String>) modelElementsDomain;
  }
  
  @Override
  public Map<String, String> getModelElementsDomain() {
    return modelElementsDomain;
  }

  @Override
  public void setResultLocation(IFolder resultLocation) {
    this.resultLocation = resultLocation;    
  }

  @Override
  public IFolder getResultLocation() {
    return resultLocation;
  }
  
  @Override
  public void setModelProperties(List<IModelProperty> modelProperties) {
    this.modelProperties = (List<IModelProperty>) modelProperties;
  }
  
  @Override
  public List<IModelProperty> getModelProperties() {
    return modelProperties;
  }
  
  @Override
  public void setSolver(ICspSolver solver) {
    this.solver = solver;
  }  
  
  @Override
  public ICspSolver getSolver() {
    return solver;
  }
  
  @Override
  public boolean solveModel() throws ProcessingException {
    return solveModel(null);
  }
  
  @Override
  public boolean solveModel(List<File> importLibs) throws ProcessingException {
    String cspCodeFileExtension = getCspCodeGenerator().getCspCodeFileExtension();
    String cspCodeFileName = getModelFileName() + "." + cspCodeFileExtension; //$NON-NLS-1$
    IPath cspCodeFilePath = getResultLocation().getRawLocation().append(cspCodeFileName);
    File cspCodeFile = new File(cspCodeFilePath.toOSString());
    String cspCode = getCspCodeGenerator().getCspCode();
    try {
	    PrintWriter out = new PrintWriter(cspCodeFile);
	    out.println(cspCode);
	    out.flush();    
	    return solver.solveCSP(cspCodeFile, importLibs);
    } catch (IOException e) {
    	throw new ProcessingException(e);
    }
  }   
  
  @Override
  public Object getSolverEvaluationResult()  {
    return solver.getResult();
  }
  
  @Override
  public void setLogger(ILogger logger) {
    this.logger = logger;
  }

  @Override
  public ILogger getLogger() {
    return logger;
  }
  
  
}
