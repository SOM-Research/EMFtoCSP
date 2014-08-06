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
package fr.inria.atlanmod.emftocsp.modelbuilder;

/**
 * @author <a href="mailto:amine.benelallam@inria.fr">Amine Benelallam</a>
 *
 */
public class Field {

	private  Object value;
	private  int index;
	public Field(Object arg, int i) {
		value =arg;
		index=i;
	}
	public Object getValue() {
		return value;
	}
	public int getIndex() {
		return index;
	}
	@Override
	public String toString() {
		return "Field [value=" + value + ", index=" + index + "type"+ value.getClass()+"]";
	}
	
}
