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
package fr.inria.atlanmod.emftocsp.adapters.umlImpl;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EcorePackage;

/**
 * @author <a href="mailto:amine.benelallam@inria.fr">Amine Benelallam</a>
 *
 */
public class EDatatypeUtil {

	public static EClassifier convertFromString (String nameLiteral){
		EDataType type = null;
		if (nameLiteral.equalsIgnoreCase("boolean"))
			type = EcorePackage.eINSTANCE.getEBoolean();
		else if (nameLiteral.equalsIgnoreCase("integer"))
			type = EcorePackage.eINSTANCE.getEInt();
		if (nameLiteral.equalsIgnoreCase("real"))
			type = EcorePackage.eINSTANCE.getEFloat();
		else if (nameLiteral.contains("unlimited"))
			type = EcorePackage.eINSTANCE.getEDouble();
		if (nameLiteral.equalsIgnoreCase("String"))
			type = EcorePackage.eINSTANCE.getEString();
		else if (nameLiteral.contains("EInt"))
			type = EcorePackage.eINSTANCE.getEInt();
		else if (nameLiteral.contains("EByte"))
			type = EcorePackage.eINSTANCE.getEByte();
		else if (nameLiteral.contains("EBigInt"))
			type = EcorePackage.eINSTANCE.getEBigInteger();
		else if (nameLiteral.contains("EBiDecimal"))
			type = EcorePackage.eINSTANCE.getEBigDecimal();
		else if (nameLiteral.contains("EString"))
			type = EcorePackage.eINSTANCE.getEString();
		else if (nameLiteral.contains("EDouble"))
			type = EcorePackage.eINSTANCE.getEBigDecimal();
		else if (nameLiteral.contains("ELong"))
			type = EcorePackage.eINSTANCE.getELong();		
		return type;
	}
}
