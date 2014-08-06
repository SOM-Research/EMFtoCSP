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
package fr.inria.atlanmod.emftocsp.impl;


import java.util.List;

import javax.annotation.Generated;

import org.eclipse.core.resources.IFolder;


import fr.inria.atlanmod.emftocsp.IModelBuilder;
import fr.inria.atlanmod.emftocsp.IModelReader;
import fr.inria.atlanmod.emftocsp.modelbuilder.AssocStruct;
import fr.inria.atlanmod.emftocsp.modelbuilder.ObjectStruct;
import fr.inria.atlanmod.emftocsp.modelbuilder.Struct;

/**
 * @author <a href="mailto:amine.benelallam@inria.fr">Amine Benelallam</a>
 *
 */
public abstract class ModelBuilder<R, P, C, AS, AT, OP,ST> implements IModelBuilder<R, P, C, AS, AT, OP,ST> {
	
	protected ST solution;
	protected IModelReader<R, P, C, AS, AT, OP> modelReader;
	protected List<AssocStruct> assocStructures;
	protected List<ObjectStruct> objectStructures;
	public ModelBuilder(){
		
	}
	
	@Override
	public abstract void generateInstance(String path);
	public ST getSolution() {
		return solution;
	}
	@Override
	public void setSolution(ST solution) {
		this.solution = solution;
	}

	public IModelReader<R ,P, C, AS, AT, OP> getModelReader() {
		return modelReader;
	}

	public void setModelReader(IModelReader<R ,P, C, AS, AT, OP> modelReader) {
		this.modelReader = modelReader;
	}

	// use the AssocNameList provided by the model reader to infer the return value 
	@Override
	public abstract void decorticateResult();
	
	public boolean isAssoc(String s){
		return !modelReader.getClassesNames().contains(s);
	}

	public List<AssocStruct> getAssocStructures() {
		return assocStructures;
	}

	public void setAssocStructures(List<AssocStruct> assocStructures) {
		this.assocStructures = assocStructures;
	}

	public List<ObjectStruct> getObjectStructures() {
		return objectStructures;
	}

	public void setObjectStructures(List<ObjectStruct> objectStructures) {
		this.objectStructures = objectStructures;
	}

	@Override
	public String toString() {
		return "ModelBuilder [assocStructures=" + assocStructures
				+ ", objectStructures=" + objectStructures + "]";
	}
	
	@Override
	public abstract boolean solutionIsEmpty();




}
