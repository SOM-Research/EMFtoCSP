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
package fr.inria.atlanmod.emftocsp;

import java.util.List;

import org.eclipse.core.resources.IFile;

/**
 * @author <a href="mailto:carlos.gonzalez@inria.fr">Carlos A. González</a>
 *
 */
public interface IOclParser<CT, R> {
  
  CT parseOclConstraint(Object context, String key, String constraint);
  
  List<CT> parseOclDocument(IFile oclDocument, R modelResource) throws ProcessingException;
  
  List<CT> parseEmbeddedConstraints(R modelResource);
  
  List<CT> parseModelConstraints(R modelResource, IFile oclDocument) throws ProcessingException;
  
  List<String> getModelConstraintsNames(R modelResource, IFile oclDocument) throws ProcessingException;

  List<String> getModelInvariantNames(R modelResource, IFile oclDocument) throws ProcessingException;
  
}
