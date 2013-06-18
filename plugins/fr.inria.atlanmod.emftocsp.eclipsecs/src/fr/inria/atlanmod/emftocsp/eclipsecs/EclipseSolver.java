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
package fr.inria.atlanmod.emftocsp.eclipsecs;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.parctechnologies.eclipse.CompoundTerm;
import com.parctechnologies.eclipse.EclipseEngine;
import com.parctechnologies.eclipse.EclipseEngineOptions;
import com.parctechnologies.eclipse.EclipseException;
import com.parctechnologies.eclipse.Fail;
import com.parctechnologies.eclipse.OutOfProcessEclipse;
import com.parctechnologies.eclipse.Throw;

import fr.inria.atlanmod.emftocsp.ICspSolver;
import fr.inria.atlanmod.emftocsp.ProcessingException;


/**
 * @author <a href="mailto:carlos.gonzalez@inria.fr">Carlos A. González</a>
 *
 */
public class EclipseSolver implements ICspSolver <CompoundTerm>{
  private String imgGeneratorPath = ""; //$NON-NLS-1$
  private File imgResult;
  private EclipseEngineOptions engineOptions = null;
  private EclipseEngine engine = null; 
  private CompoundTerm solution = null;
  
  @Override
  public CompoundTerm getSolution(){
	  return solution;
  }

  public EclipseSolver(String solverPath, String imgGeneratorPath) {
    this.imgGeneratorPath = imgGeneratorPath;
    engineOptions = new EclipseEngineOptions(new File(solverPath));
    engineOptions.setUseQueues(false);    
  }

  @Override
  public boolean solveCSP(File srcFile) throws ProcessingException {
    return solveCSP(srcFile, null);
  }  
  
  @Override
  public boolean solveCSP(File srcFile, List<File> libs) throws ProcessingException  {
    try {
      createEngineProcess();
      compile(srcFile, libs);
      File dotFile = File.createTempFile(srcFile.getName(), ".dot"); //$NON-NLS-1$
      dotFile.deleteOnExit();
      StringBuilder query = new StringBuilder();
      query.append("findSolutions(I),gviz_draw_object_diagram(\""); //$NON-NLS-1$
      query.append(dotFile.getAbsolutePath().replaceAll("\\\\", "\\\\\\\\")); //$NON-NLS-1$ //$NON-NLS-2$
      query.append("\",I)"); //$NON-NLS-1$
      solution = engine.rpc(query.toString());    
      System.out.println();
      disposeEngineProcess();
      String imgFilePath = srcFile.getAbsolutePath() + ".png"; //$NON-NLS-1$
      setResult(generateImage(imgFilePath, dotFile));
      return true;
    }
    catch (Throw th) {
      disposeEngineProcess(); 
      throw new ProcessingException("Unexpected problem. Please consult the executed generated CLP code (.ecl file)",th);
    }
    catch (Fail fail) {
        disposeEngineProcess(); 
        return false;
    } catch (EclipseException e) {
        disposeEngineProcess(); 
        throw new ProcessingException("Unexpected problem. Please consult the executed generated CLP code (.ecl file)",e);
	} catch (IOException e) {
		disposeEngineProcess();
        throw new ProcessingException("Unexpected problem. Please consult the executed generated CLP code (.ecl file)",e);
	}
  }
  

@Override
  public Object getResult() {
    return imgResult;
  }
  
  private void setResult(File result) {
    imgResult = result;
  }
    
  private void compile(File srcFile, List<File> libs) throws EclipseException, IOException {   
    engine.compile(srcFile);
    if (libs != null)
      for (File importFile : libs)
        engine.compile(importFile);    
  }
  
  private File generateImage(String imgFilePath, File dotFile) throws ProcessingException {
    StringBuilder cmd = new StringBuilder();
    
    if (dotFile == null)
      return null;
    File imgFile = new File(imgFilePath);
    
    cmd.append(imgGeneratorPath);
    cmd.append(File.separator);
    cmd.append("dot -Tpng -o "); //$NON-NLS-1$
    cmd.append(imgFile.getAbsolutePath());
    cmd.append(" "); //$NON-NLS-1$
    cmd.append(dotFile.getAbsolutePath());    
    Process proc;
	try {
		proc = Runtime.getRuntime().exec(cmd.toString());
	    proc.waitFor();
	} catch (IOException e) {
		throw new ProcessingException(e);
	} catch (InterruptedException e) {
		throw new ProcessingException(e);
	}
    return imgFile;
  }

  private void disposeEngineProcess() {
    try {
      ((OutOfProcessEclipse)engine).destroy();
    } 
    catch (Exception e) {
      e.printStackTrace();
    } 
    finally {
      engine = null;
    }
  }
  
  private void createEngineProcess() {
    try {
      engine = new OutOfProcessEclipse(engineOptions);
    } 
    catch (IOException e) {
      e.printStackTrace();
    } 
    catch (EclipseException e) {
      e.printStackTrace();
    }
  }
  
}
