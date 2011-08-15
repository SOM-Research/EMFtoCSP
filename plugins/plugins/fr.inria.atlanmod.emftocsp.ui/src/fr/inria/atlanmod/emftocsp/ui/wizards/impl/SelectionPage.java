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
import java.util.Collections;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import fr.inria.atlanmod.emftocsp.ui.widgets.IElementSelectionWidget;
import fr.inria.atlanmod.emftocsp.ui.widgets.IElementSelectionWidgetListener;
import fr.inria.atlanmod.emftocsp.ui.widgets.impl.ElementSelectionWidget;

/**
 * @author <a href="mailto:carlos.gonzalez@inria.fr">Carlos A. González</a>
 *
 */
public abstract class SelectionPage extends WizardPage implements IElementSelectionWidgetListener {
	private IElementSelectionWidget esWidget;
	private IStructuredSelection selection;
	private Collection<String> allowedTypes;
	private boolean mandatorySelection;
	
	public SelectionPage(String pageName, String description, IStructuredSelection selection, Collection<String> allowedTypes) {
		super(pageName);
		setTitle(pageName);
		setDescription(description);
		this.selection = selection;
		mandatorySelection = true;
	  this.allowedTypes = allowedTypes != null ? allowedTypes : Collections.<String> emptyList();
	}
	
	public SelectionPage(String pageName, String description, IStructuredSelection selection, Collection<String> allowedTypes, boolean mandatorySelection) {
		this(pageName, description, selection, allowedTypes);
		this.mandatorySelection = mandatorySelection;
	}	

	public void createControl(Composite parent) {
		esWidget = new ElementSelectionWidget(parent, selection, allowedTypes, this, mandatorySelection);
		setControl((Control) esWidget);
		setPageComplete(validateSelectionWidget());
	}

	public void elementSelectionWidgetChanged(IElementSelectionWidget esWidget) {
		setPageComplete(validateSelectionWidget());		
	}

	private boolean validateSelectionWidget() {
	  if (!esWidget.isInSelectionMode())
	    return true;
	  return esWidget.isSelectedElementValid();
	}
	
	public IElementSelectionWidget getElementSelectionWidget() {
		return esWidget;
	}
	
	public Object getSelectedElement() {	  
	  if (esWidget != null) {
		if (!esWidget.isInSelectionMode())
		  return null;
	    return esWidget.getElementSelected();
	  }
	  return null;
	}

}
