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
package fr.inria.atlanmod.emftocsp.ui.main.preferences;


import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import fr.inria.atlanmod.emftocsp.ui.main.Activator;

/**
 * @author <a href="mailto:carlos.gonzalez@inria.fr">Carlos A. González</a>
 *
 */
public class PreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
  
    public PreferencePage() {
      super(GRID);
      setPreferenceStore(Activator.getDefault().getPreferenceStore());
      setDescription("EMFtoCSP Preferences"); //$NON-NLS-1$
    }

    public void createFieldEditors() {
      addField(new DirectoryFieldEditor("EclipsePath", "Eclipse Constraint Solver Path: ", getFieldEditorParent())); //$NON-NLS-1$ //$NON-NLS-2$
      addField(new DirectoryFieldEditor("GraphvizPath", "Graphviz Dot Path: ", getFieldEditorParent())); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void init(IWorkbench workbench) {
    }
  }