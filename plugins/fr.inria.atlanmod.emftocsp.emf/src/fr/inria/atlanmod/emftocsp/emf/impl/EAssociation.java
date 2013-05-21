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
package fr.inria.atlanmod.emftocsp.emf.impl;

import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EClass;

/**
 * @author <a href="mailto:carlos.gonzalez@inria.fr">Carlos A. González</a>
 *
 */
public class EAssociation {
  EClass srcEnd;
  EReference dstEnd;
  String name;
  
  public EAssociation(String name, EClass srcEnd, EReference dstEnd) {
    this.name = name;
    this.srcEnd = srcEnd;
    this.dstEnd = dstEnd;
  }
  /*
  *
  * Getters and setters
  */
  public String getName() {
    return name;
  }
  
  public EClass getSourceEnd() {
    return srcEnd;
  }
  
  public EReference getDestinationEnd() {
    return dstEnd;
  }
  
  public String getSourceRoleName() {
    if (dstEnd.getEOpposite() == null)
      return srcEnd.getName();
    return dstEnd.getEOpposite().getName();
  }
  
  public String getDestinationRoleName() {
    return dstEnd.getName();
  }

  public int getSourceLowerBound() {
    if (dstEnd.getEOpposite() == null)
      return 0;
    return dstEnd.getEOpposite().getLowerBound();
  }

  public int getSourceUpperBound() {
    if (dstEnd.getEOpposite() == null && dstEnd.isContainment())
      return 1;
    else if (dstEnd.getEOpposite() == null )
    	return -1;
    return dstEnd.getEOpposite().getUpperBound();
  }  
  
  public int getDestinationLowerBound() {
    return dstEnd.getLowerBound();
  }

  public int getDestinationUpperBound() {
    return dstEnd.getUpperBound();
  }
}
