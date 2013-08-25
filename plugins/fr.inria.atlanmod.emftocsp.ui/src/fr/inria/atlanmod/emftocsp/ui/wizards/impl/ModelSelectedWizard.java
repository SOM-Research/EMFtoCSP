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

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;

import fr.inria.atlanmod.emftocsp.IModelToCspSolver;

/**
 * @author <a href="mailto:carlos.gonzalez@inria.fr">Carlos A. González</a>
 *
 */
public class ModelSelectedWizard extends ValidationWizard {

	public ModelSelectedWizard(IModelToCspSolver<?,?> modelSolver) {
		super(new ModelWizardNavigation(modelSolver), modelSolver);
    ImageDescriptor image = AbstractUIPlugin.imageDescriptorFromPlugin("fr.inria.atlanmod.emftocsp.ui", "resources/icons/Checked64.png"); //$NON-NLS-1$ //$NON-NLS-2$
    setDefaultPageImageDescriptor(image);   
	}	
			
}
