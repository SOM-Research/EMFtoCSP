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
package fr.inria.atlanmod.emftocsp.ui.commands;

import org.eclipse.core.commands.AbstractHandler;


import com.parctechnologies.eclipse.CompoundTerm;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.uml2.uml.resource.UMLResource;

import fr.inria.atlanmod.emftocsp.ICspSolver;
import fr.inria.atlanmod.emftocsp.IModelToCspSolver;
import fr.inria.atlanmod.emftocsp.IModelToCspSolverFactory;
import fr.inria.atlanmod.emftocsp.emf.impl.EmfModelToCspSolverFactory;
import fr.inria.atlanmod.emftocsp.ui.Activator;
import fr.inria.atlanmod.emftocsp.ui.wizards.impl.ModelSelectedWizard;
import fr.inria.atlanmod.emftocsp.ui.wizards.impl.ValidationWizard;
import fr.inria.atlanmod.emftocsp.uml.impl.UmlModelToCspSolverFactory;
import fr.inria.atlanmod.emftocsp.umltoecl.UmlToEclCodeGenerator;
import fr.inria.atlanmod.emftocsp.emftoecl.EmfToEclCodeGenerator;

import fr.inria.atlanmod.emftocsp.eclipsecs.EclipseSolver;
import fr.inria.atlanmod.emftocsp.impl.FileLogger;

/**
 * @author <a href="mailto:carlos.gonzalez@inria.fr">Carlos A. González</a>
 *
 */
public class WizardHandler extends AbstractHandler implements IHandler {
	
  public Object execute(ExecutionEvent event) throws ExecutionException {
    IStructuredSelection selection = (IStructuredSelection)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
    IFile modelFile = (IFile)selection.getFirstElement();
    String eclipsePath = Activator.getDefault().getPreferenceStore().getString("EclipsePath"); //$NON-NLS-1$
    String graphvizPath = Activator.getDefault().getPreferenceStore().getString("GraphvizPath");       //$NON-NLS-1$
    
    if (modelFile.getFileExtension().equals("uml")) { //$NON-NLS-1$
      launchUmlWizard(modelFile, eclipsePath, graphvizPath);
      return null;
    }
    if (modelFile.getFileExtension().equals("ecore")) { //$NON-NLS-1$
      launchEmfWizard(modelFile, eclipsePath, graphvizPath);
      return null;
    }
    return null;
  }   
  
  public void launchEmfWizard(IFile modelFile, String eclipsePath, String graphvizPath) {
    URI modelFileURI = URI.createFileURI(modelFile.getRawLocation().toOSString());    
    ICspSolver solver = new EclipseSolver(eclipsePath, graphvizPath);
    
    ResourceSet rSet = new ResourceSetImpl();
    rSet.setPackageRegistry(EPackage.Registry.INSTANCE);
    Resource r = rSet.getResource(modelFileURI, true);
    IModelToCspSolverFactory<Resource,CompoundTerm> modelSolverFactory = new EmfModelToCspSolverFactory();
    
    IModelToCspSolver<Resource,CompoundTerm> modelSolver = modelSolverFactory.getModelToCspSolver();
    modelSolver.setModelFileName(modelFile.getName());
    modelSolver.setModel(r);
    modelSolver.setSolver(solver);
    modelSolver.setCspCodeGenerator(new EmfToEclCodeGenerator(modelSolver));
    modelSolver.setLogger(new FileLogger());
//    modelSolver.setLogger(new FileLogger(modelFile.getRawLocation().toOSString().concat(".emftocsp.log")));
    modelSolver.getLogger().writeInfoMessage(this.getClass().toString(), "Starting EMFtoCSP GUI");
    modelSolver.getBuilder();   
    ValidationWizard wizard = new ModelSelectedWizard(modelSolver);
    WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
    dialog.open();
  }
  
  public void launchUmlWizard(IFile modelFile, String eclipsePath, String graphvizPath) {
    URI modelFileURI = URI.createFileURI(modelFile.getRawLocation().toOSString());    
    ICspSolver solver = new EclipseSolver(eclipsePath, graphvizPath);

    ResourceSet rSet = new ResourceSetImpl();
    UMLResource r = (UMLResource)rSet.getResource(modelFileURI, true);
    IModelToCspSolverFactory<UMLResource,CompoundTerm> modelSolverFactory = new UmlModelToCspSolverFactory();
    
    IModelToCspSolver<UMLResource,CompoundTerm> modelSolver = modelSolverFactory.getModelToCspSolver(); 
    modelSolver.setModelFileName(modelFile.getName());
    modelSolver.setModel(r);
    modelSolver.setSolver(solver);
    modelSolver.setCspCodeGenerator(new UmlToEclCodeGenerator(modelSolver));

    modelSolver.setLogger(new FileLogger());
    //  modelSolver.setLogger(new FileLogger(modelFile.getRawLocation().toOSString().concat(".emftocsp.log")));
    modelSolver.getLogger().writeInfoMessage(this.getClass().toString(), "Starting EMFtoCSP GUI");

  ValidationWizard wizard = new ModelSelectedWizard(modelSolver);
    WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
    dialog.open();
  }
}
