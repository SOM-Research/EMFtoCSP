/*******************************************************************************
 * Copyright (c) 2012 INRIA Rennes Bretagne-Atlantique.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     INRIA Rennes Bretagne-Atlantique - initial API and implementation
 *******************************************************************************/
package fr.inria.atlanmod.emftocsp.emftoecl;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EEnumLiteral;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.ocl.ecore.CallOperationAction;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.ecore.SendSignalAction;
import org.eclipse.ocl.expressions.Variable;
import org.eclipse.ocl.expressions.VariableExp;
import org.eclipse.ocl.utilities.AbstractVisitor;

/**
 * Determines if the "self" variable is referenced in this ocl expression.
 * @author Fabian B�ttner
 */
public class LookupSelfVariableVisitor extends AbstractVisitor<Variable<EClassifier, EParameter>, EClassifier, EOperation, EStructuralFeature, EEnumLiteral, EParameter, EObject, CallOperationAction, SendSignalAction, Constraint> {
	
	public  Variable<EClassifier, EParameter> visitVariableExp(VariableExp<EClassifier, EParameter> v) {
		Variable<EClassifier, EParameter> referredVar = v.getReferredVariable();
		if ("self".equals(referredVar.getName())) {
			result = referredVar;
		}
		return result;
	}
	
	/**
	 * If the "self" variable was referenced, this method return the declaration of the variable, 
	 * otherwise the method return null.
	 */
	public Variable<EClassifier, EParameter> getResult() {
		return result;
	}
}