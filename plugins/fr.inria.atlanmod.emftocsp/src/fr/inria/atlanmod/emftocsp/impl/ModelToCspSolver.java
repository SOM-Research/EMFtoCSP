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
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

import fr.inria.atlanmod.emftocsp.ICspCodeGenerator;
import fr.inria.atlanmod.emftocsp.ICspSolver;
import fr.inria.atlanmod.emftocsp.ILogger;
import fr.inria.atlanmod.emftocsp.IModelBuilder;
import fr.inria.atlanmod.emftocsp.IModelProperty;
import fr.inria.atlanmod.emftocsp.IModelReader;
import fr.inria.atlanmod.emftocsp.IModelToCspSolver;
import fr.inria.atlanmod.emftocsp.IOclParser;
import fr.inria.atlanmod.emftocsp.ProcessingException;

/**
 * @author <a href="mailto:carlos.gonzalez@inria.fr">Carlos A. González</a>
 *
 */
public abstract class ModelToCspSolver<R,ST> implements IModelToCspSolver<R,ST> {
  IFile constraintsDocument;
  IFolder resultLocation;
  HashMap<String, String> modelElementsDomain;
  List<IModelProperty> modelProperties;
  ICspSolver<ST> solver;
  String modelFilename;
  ILogger logger;
  protected IModelBuilder<R, ?,?,?,?,?,ST> builder;

@Override  
  public abstract 	IModelBuilder<R, ?, ?, ?, ?, ?, ST> getBuilder();
  
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
  public void setConstraintsDocumentToNull() {
    this.constraintsDocument = null;
  }
  @Override 
  public void setConstraintsDocument(URI fileURI) throws CoreException{
	  IWorkspace ws = ResourcesPlugin.getWorkspace();
	IProject project = ws.getRoot().getProject("external.files");
	if (project.exists())
		project.delete(true, null);
	    project.create(null);
	if (!project.isOpen())
	    project.open(null);
	project.setHidden(false);
	IPath location = new Path(fileURI.getPath());
	IFile file = project.getFile(location.lastSegment());
	file.createLink(location, IResource.HIDDEN, null);
	setConstraintsDocument(file);
	addShutDownhook(project);
  }
  

  private void addShutDownhook(final IProject project) {
	  
	  Runtime.getRuntime().addShutdownHook( 
	    		new Thread(
	    			new Runnable() {
	    				public void run() {
	    					try {
								project.delete(true, null);
							} catch (CoreException e) {
								e.printStackTrace();
							}
	    				}	
	    			}
	    		)
	    	);
	
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
    boolean solved=false;
    try {
	    PrintWriter out = new PrintWriter(cspCodeFile);
	    out.println(cspCode);
	    out.flush();    
	    solved = solver.solveCSP(cspCodeFile, importLibs);
	    if(solver.getSolution()!=null){
	    builder.setSolution(solver.getSolution());
	    builder.decorticateResult();
	    String path ="\\" + modelFilename;
	    path=resultLocation.getFullPath().toOSString()+path;
	    //System.out.println("PATH IS "+path);
	    String outputModelLocation= getUnusedLocation(cspCodeFile.getAbsolutePath());
		builder.generateInstance("file://"+outputModelLocation);
		if (getConstraintsDocument() != null)
		 if (getConstraintsDocument().getParent() instanceof IProject)
			 if (((IProject)getConstraintsDocument().getParent()).getFullPath().segment(0).equals("external.files") )
				 ((IProject)getConstraintsDocument().getParent()).delete(true,null);
	    }

	    return solved;
    } catch (IOException e) {
    	throw new ProcessingException(e);
    }catch (CoreException e){
    	throw new ProcessingException(e);
    }
  }   
  
  private String getUnusedLocation(String absolutePath) {
	String cutPath = absolutePath.substring(0, absolutePath.lastIndexOf('.'));
	String resultLocation = "";
	int counter = 0;
	boolean halt = false;
	while (!halt){
		resultLocation = cutPath+".i"+counter+".xmi";
		File file = new File(resultLocation);
		if (file.exists()){
			counter++;
		}
		else {
			halt = true;
		}
	}
		return resultLocation.substring(0, absolutePath.lastIndexOf('.'));
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
