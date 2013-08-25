package fr.inria.atlanmod.emftocsp.adapters.umlImpl;

import java.util.List;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Parameter;
import org.eclipse.uml2.uml.ParameterDirectionKind;

import fr.inria.atlanmod.emftocsp.adapters.EOperationAdapter;

public class EOperationUMLAdapter extends EOperationAdapter<Operation> {

	protected Resource owningResource;
	public EOperationUMLAdapter(Operation newOperation, Resource owningResource) {
		super(newOperation);
		this.owningResource = owningResource;
	}

	@Override
	public EList<EAnnotation> getEAnnotations() {
		EList<EAnnotation> result = new BasicEList<EAnnotation>();
			for (EAnnotation annot : origOperation.getEAnnotations())
				result.add(new EAnnotationUMLAdapter(annot,owningResource));
		return result;
	}

	@Override
	public EAnnotation getEAnnotation(String source) {
		return new EAnnotationUMLAdapter(origOperation.getEAnnotation(source),owningResource);
	}

	@Override
	public String getName() {
		return origOperation.getName();
	}

	@Override
	public EClass getEContainingClass() {
		//TODO
		return (EClass)((EResourceUMLAdapter)owningResource).getClassIfNotExists(new EClassUMLAdapter(origOperation.getClass_(),owningResource));
	}

	@Override
	public EList<EParameter> getEParameters() {
		List<Parameter> paramList = origOperation.getOwnedParameters();
		EList<EParameter> result = new BasicEList<EParameter>();
	    if (! paramList.isEmpty()){
	    	for (Parameter param : paramList)
	    		 if (param.getDirection() != ParameterDirectionKind.RETURN_LITERAL)
	    		result.add(((EResourceUMLAdapter)owningResource).getParamIfNotExists(new EParameterUMLAdapter(param,owningResource)));
	    }
	return result;
	}

	@Override
	public EClassifier getEType() {
		if (origOperation.getType() instanceof Class ) 
			return ((EResourceUMLAdapter)owningResource).getClassIfNotExists(new EClassUMLAdapter((Class)origOperation.getType(),owningResource));
		return ((EResourceUMLAdapter)owningResource).getClassIfNotExists(new EClassifierUMLAdapter((Classifier) origOperation.getType(),owningResource));
	}

	@Override
	public boolean isMany() {
		return origOperation.getUpper() == -1 || origOperation.getUpper() > 1 ;
	}

}
