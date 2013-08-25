package fr.inria.atlanmod.emftocsp.adapters.umlImpl;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Parameter;

import fr.inria.atlanmod.emftocsp.adapters.EParameterAdapter;

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
