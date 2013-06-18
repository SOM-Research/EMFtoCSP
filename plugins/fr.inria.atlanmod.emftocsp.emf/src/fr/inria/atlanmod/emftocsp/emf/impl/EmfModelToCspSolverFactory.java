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

import org.eclipse.emf.ecore.resource.Resource;

import com.parctechnologies.eclipse.CompoundTerm;

import fr.inria.atlanmod.emftocsp.IModelToCspSolver;
import fr.inria.atlanmod.emftocsp.emf.IEmfModelToCspSolverFactory;

/**
 * @author <a href="mailto:carlos.gonzalez@inria.fr">Carlos A. González</a>
 *
 */
public class EmfModelToCspSolverFactory implements IEmfModelToCspSolverFactory {

	@Override
	public IModelToCspSolver<Resource,CompoundTerm> getModelToCspSolver() {
		return new EmfModelToCspSolver();
	}

}
