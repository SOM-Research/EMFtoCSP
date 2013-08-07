package fr.inria.atlanmod.emftocsp.adapters.umlImpl;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Parameter;

import fr.inria.emftocsp.adapters.EParameterAdapter;

public class EParameterUMLAdapter extends EParameterAdapter<Parameter> {

	protected Resource owningResource;
	public EParameterUMLAdapter(Parameter param) {
		super(param);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getName() {
		return origParameter.getName();
//		throw new UnsupportedOperationException();
	}

	@Override
	public EClassifier getEType() {
		return new EClassifierUMLAdapter((Classifier)origParameter.getType());
	}

	@Override
	public boolean isMany() {
		return origParameter.isMultivalued();
	}
	

}
