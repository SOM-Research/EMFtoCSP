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

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.jface.wizard.IWizardPage;

import fr.inria.atlanmod.emftocsp.IModelToCspSolver;
import fr.inria.atlanmod.emftocsp.ui.messages.Messages;
import fr.inria.atlanmod.emftocsp.ui.wizards.IWizardNavigation;

/**
 * @author <a href="mailto:carlos.gonzalez@inria.fr">Carlos A. González</a>
 *
 */
public class ModelWizardNavigation implements IWizardNavigation {
  IWizardPage[] wizardPages = new IWizardPage[4];
	
	public ModelWizardNavigation(IModelToCspSolver<?,?> modelSolver) {
    wizardPages[0] = new OclFileSelectionPage("OCL constraints selection", Messages.ModelWizardNavigation_0, null, new ArrayList<String>(Arrays.asList("ocl")), false, modelSolver); //$NON-NLS-3$ //$NON-NLS-1$
    wizardPages[1] = new ModelElementDomainPage(Messages.ModelWizardNavigation_1, modelSolver);
    wizardPages[2] = new PropertiesSelectionPage(Messages.ModelWizardNavigation_2, modelSolver);
    wizardPages[3] = new ResultLocationSelectionPage("Validation results", Messages.ModelWizardNavigation_3, null, new ArrayList<String>(Arrays.asList("Folder")), modelSolver); //$NON-NLS-3$ //$NON-NLS-1$
	}
	
	@Override
	public IWizardPage[] getWizardPages() {
		return wizardPages;
	}

}

