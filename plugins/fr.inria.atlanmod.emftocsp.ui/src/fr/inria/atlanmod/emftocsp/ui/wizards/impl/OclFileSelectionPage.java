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

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IStructuredSelection;

import fr.inria.atlanmod.emftocsp.IModelToCspSolver;
import fr.inria.atlanmod.emftocsp.ui.widgets.IElementSelectionWidget;

/**
 * @author <a href="mailto:carlos.gonzalez@inria.fr">Carlos A. González</a>
 *
 */
public class OclFileSelectionPage extends SelectionPage {
	IModelToCspSolver<?,?> modelSolver;
	
	public OclFileSelectionPage(String pageName, String description, IStructuredSelection selection, Collection<String> allowedTypes, IModelToCspSolver<?,?> modelSolver) {
		super(pageName,description, selection, allowedTypes);
		this.modelSolver = modelSolver;
	}

	public OclFileSelectionPage(String pageName, String description, IStructuredSelection selection, Collection<String> allowedTypes, boolean mandatorySelection, IModelToCspSolver<?,?> modelSolver) {
		super(pageName,description, selection, allowedTypes, mandatorySelection);
		this.modelSolver = modelSolver;
	}

	@Override
	public void elementSelectionWidgetChanged(IElementSelectionWidget esWidget) {
	  IFile file = null;
	  if (esWidget.getElementSelected() == null)
      modelSolver.setConstraintsDocument(file);
	  else if (esWidget.getElementSelected() instanceof IFile) {
	    IFile constraintsDocument = (IFile)esWidget.getElementSelected();
		  modelSolver.setConstraintsDocument(constraintsDocument);
	  }
		super.elementSelectionWidgetChanged(esWidget);
	}	
}