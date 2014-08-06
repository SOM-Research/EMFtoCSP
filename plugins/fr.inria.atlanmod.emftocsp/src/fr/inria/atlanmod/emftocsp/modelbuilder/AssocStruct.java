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

import com.parctechnologies.eclipse.CompoundTerm;

/**
 * @author <a href="mailto:amine.benelallam@inria.fr">Amine Benelallam</a>
 *
 */
public class AssocStruct extends Struct {

	public AssocStruct(CompoundTerm cp, String functor) {
		super(functor);
		// TODO Auto-generated constructor stub
		srcOid=(Integer) cp.arg(1);
		trgOid=(Integer) cp.arg(2);
	}
	private int srcOid;
	private int trgOid;
	@Override
	public String toString() {
		return "AssocStruct [srcOid=" + srcOid + ", trgOid=" + trgOid + "]";
	}
	public int getSrcOid() {
		return srcOid;
	}
	public void setSrcOid(int srcOid) {
		this.srcOid = srcOid;
	}
	public int getTrgOid() {
		return trgOid;
	}
	public void setTrgOid(int trgOid) {
		this.trgOid = trgOid;
	}
	
	
}
