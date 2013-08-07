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
package fr.inria.atlanmod.emftocsp.ui.wizards.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.MessageBox;
import org.osgi.framework.FrameworkUtil;

import fr.inria.atlanmod.emftocsp.IModelToCspSolver;
import fr.inria.atlanmod.emftocsp.ProcessingException;
import fr.inria.atlanmod.emftocsp.ui.messages.Messages;
import fr.inria.atlanmod.emftocsp.ui.wizards.IWizardNavigation;

/**
 * @author <a href="mailto:carlos.gonzalez@inria.fr">Carlos A. González</a>
 *
 */
public abstract class ValidationWizard extends Wizard  {
	IWizardNavigation wizardNavigation;
	IModelToCspSolver<?,?> modelSolver;
	String logFileName;
	
	public ValidationWizard(IWizardNavigation wizardNavigation, IModelToCspSolver<?,?> modelSolver) {
		this.wizardNavigation = wizardNavigation;
		this.modelSolver = modelSolver;		
		logFileName = modelSolver.getModelLocation() + modelSolver.getModelFileName() + ".log";
	}

	@Override
	public void addPages() {
	  IWizardPage[] wizardPages = wizardNavigation.getWizardPages();
	  for (int i = 0; i < wizardPages.length; i++)
	    addPage(wizardPages[i]);
	}
	
	@Override
	public boolean performCancel() {
	  if (modelSolver.getLogger() != null) {
	    modelSolver.getLogger().writeInfoMessage(this.getClass().toString(), "Wizard cancelled by the user");	    
	    if (modelSolver.getResultLocation() != null) {
        logFileName = modelSolver.getResultLocation().getRawLocation().append(modelSolver.getModelFileName() + ".log").toOSString();
        modelSolver.getLogger().writeInfoMessage(this.getClass().toString(), "Closing EMFtoCSP");
	      modelSolver.getLogger().close(logFileName);
  	    try {
          modelSolver.getResultLocation().refreshLocal(IResource.DEPTH_INFINITE, null);
	      }
	      catch (Exception e) {
	        e.printStackTrace();
	      }
	    }
	  }
	  return true;
	}
	
	@Override
	public boolean performFinish() {  
		try {
			logFileName = modelSolver.getResultLocation().getRawLocation().append(modelSolver.getModelFileName() + ".log").toOSString();
			File importsFolder;
			try {
				importsFolder = new File(FileLocator.toFileURL(FrameworkUtil.getBundle(fr.inria.atlanmod.emftocsp.eclipsecs.EclipseSolver.class).getEntry("/libs")).toURI());
			} catch (URISyntaxException e) {
				throw new ProcessingException(e);
			} catch (IOException e) {
				throw new ProcessingException(e);
			} 
			File[] libs = importsFolder.listFiles(
					new FilenameFilter() {
						public boolean accept(File dir, String name) {    
							return name.matches(".*\\.ecl$");  //$NON-NLS-1$
						}
					}
					);
			ArrayList<File> libList = new ArrayList<File>();
			for(int i = 0; i < libs.length; i++)
				libList.add(libs[i]);
			String msgTitle = ""; //$NON-NLS-1$
			String message = ""; //$NON-NLS-1$
			if (modelSolver.solveModel(libList)) {
				MessageBox messageBox = new MessageBox(this.getShell(), SWT.ICON_INFORMATION | SWT.OK);
				msgTitle = Messages.ValidationWizard_0;
				message = Messages.ValidationWizard_1;
				messageBox.setText(msgTitle);
				messageBox.setMessage(message);
				messageBox.open();
				modelSolver.getLogger().writeInfoMessage(this.getClass().toString(), "Closing EMFtoCSP");
				modelSolver.getLogger().close(logFileName);
				try {
					modelSolver.getResultLocation().refreshLocal(IResource.DEPTH_INFINITE, null);
				} 
				catch (CoreException ex) {
					ex.printStackTrace();
				}
				return true;
			}
			msgTitle = Messages.ValidationWizard_2;
			message = Messages.ValidationWizard_3;	      
			MessageBox messageBox = new MessageBox(this.getShell(), SWT.ICON_WARNING | SWT.OK);
			messageBox.setText(msgTitle);
			messageBox.setMessage(message);
			messageBox.open();    
			//      modelSolver.getLogger().writeInfoMessage(this.getClass().toString(), "Closing EMFtoCSP");
			//      modelSolver.getLogger().close(logFileName);
			//      modelSolver.getResultLocation().refreshLocal(IResource.DEPTH_INFINITE, null);
			return false;
		}
		catch(Exception e) {
			e.printStackTrace();
			MessageBox messageBox = new MessageBox(this.getShell(), SWT.ICON_ERROR | SWT.OK);
			String msgTitle = Messages.ValidationWizard_4;
			String message = Messages.ValidationWizard_5;     
			messageBox.setText(msgTitle);
			messageBox.setMessage(message + ":\n\n" + e.getMessage());
			messageBox.open();    
			modelSolver.getLogger().writeInfoMessage(this.getClass().toString(), "Closing EMFtoCSP");
			modelSolver.getLogger().close(logFileName);     
			try {
				modelSolver.getResultLocation().refreshLocal(IResource.DEPTH_INFINITE, null);
			} 
			catch (CoreException ex) {
				ex.printStackTrace();
			}
			return true;
		}
	}
	
	@Override
	public boolean canFinish() {
	  IWizardPage[] wizardPages = wizardNavigation.getWizardPages();
	  IWizardPage lastPage = wizardPages[wizardPages.length - 1];
	  if (!(lastPage instanceof SelectionPage))
	    return false;
	  return ((SelectionPage)lastPage).getElementSelectionWidget().isSelectedElementValid();
	}	
	
	public final void SetWizardNavigation(IWizardNavigation wizardNavigation) {
	  this.wizardNavigation = wizardNavigation;
	}
	
	public final IWizardNavigation GetWizardNavigation() {
      return wizardNavigation;
	}
}
