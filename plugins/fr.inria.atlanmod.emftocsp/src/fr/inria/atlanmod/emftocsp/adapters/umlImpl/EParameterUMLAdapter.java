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
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Parameter;

import fr.inria.atlanmod.emftocsp.adapters.EParameterAdapter;

/**
 * @author <a href="mailto:amine.benelallam@inria.fr">Amine Benelallam</a>
 *
 */
public class EParameterUMLAdapter extends EParameterAdapter<Parameter> {

	protected Resource owningResource;
	public EParameterUMLAdapter(Parameter param, Resource owningResource) {
		super(param);
		this.owningResource=owningResource;
	}

	@Override
	public String getName() {
		return origParameter.getName();
//		throw new UnsupportedOperationException();
	}

	@Override
	public EClassifier getEType() {
		if (origParameter.getType() instanceof Class)
			return ((EResourceUMLAdapter)owningResource).getClassIfNotExists(new EClassUMLAdapter((Class)origParameter.getType(),owningResource));
		return ((EResourceUMLAdapter)owningResource).getClassIfNotExists(EDatatypeUtil.convertFromString(origParameter.getType().getName()));
	}

	@Override
	public boolean isMany() {
		return origParameter.isMultivalued();
	}
	

}
