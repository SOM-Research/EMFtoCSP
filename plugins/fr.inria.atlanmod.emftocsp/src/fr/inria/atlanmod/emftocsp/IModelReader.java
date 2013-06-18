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




/**
 * @author <a href="mailto:carlos.gonzalez@inria.fr">Carlos A. González</a>
 *
 */
public interface IModelReader<R, P, C, AS, AT, OP> {

	public R getModelResource(); 

	public List<P> getPackages();

	public List<C> getClasses();

	public List<String> getClassesNames();

	public List<AT> getClassAttributes(C c);

	public List<OP> getClassOperations(C c);  

	public List<C> getClassSubtypes(List<C> classList, C c);
	
	public void getClassSubtypes(List<C> cList, C c, List<C> subTypes);

	public C getBaseClass(C c);  

	public List<AS> getAssociations();

	public List<String> getAssociationsNames();

	public String getAssociationName(AS as);

	public String getAssociationEndName(AT asEnd);

	public List<String> getAssociationNamesOfNonAbsClasses();

	public R getResource();
	

}
