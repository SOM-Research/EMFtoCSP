/*******************************************************************************
 * Copyright (c) 2013 INRIA Rennes Bretagne-Atlantique.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     INRIA Rennes Bretagne-Atlantique - initial API and implementation
 *******************************************************************************/
package fr.inria.atlanmod.emftocsp.ui.messages;

import org.eclipse.osgi.util.NLS;

/**
 * @author <a href="mailto:amine.benelallam@inria.fr">Amine Benelallam</a>
 *
 */
public class Messages extends NLS {
  private static final String BUNDLE_NAME = "fr.inria.atlanmod.emftocsp.ui.messages.messages"; //$NON-NLS-1$
  public static String ModelElementDomainPage_0;
  public static String ModelWizardNavigation_0;
  public static String ModelWizardNavigation_1;
  public static String ModelWizardNavigation_2;
  public static String ModelWizardNavigation_3;
  public static String PropertiesSelectionPage_0;
  public static String ValidationWizard_0;
  public static String ValidationWizard_1;
  public static String ValidationWizard_2;
  public static String ValidationWizard_3;
  public static String ValidationWizard_4;
  public static String ValidationWizard_5;
  static {
    // initialize resource bundle
    NLS.initializeMessages(BUNDLE_NAME, Messages.class);
  }

  private Messages() {
  }
}
