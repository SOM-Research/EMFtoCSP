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
import java.util.Map;

import org.eclipse.core.resources.IFile;

/**
 * @author <a href="mailto:carlos.gonzalez@inria.fr">Carlos A. González</a>
 *
 */
public interface ICspCodeGenerator<R, P, C, AS, AT, OP, CT> {

  void setModelReader(IModelReader<R, P, C, AS, AT, OP> modelReader);
  
  void setProperties(List<IModelProperty> properties);
  
  void setModelElementsDomains(Map<String, String> elementsDomain);
  
  void setOclParser(IOclParser<CT, R> oclParser);
  
  void setOclDocument(IFile oclDocument);
  
  void setModel(R modelResource);
  
  String getCspCode() throws ProcessingException;
  
  String getCspCodeFileExtension();  
}
