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
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Parameter;

import fr.inria.emftocsp.adapters.EOperationAdapter;

public class EOperationUMLAdapter extends EOperationAdapter<Operation> {

	protected Resource owningResource;
	public EOperationUMLAdapter(Operation newOperation) {
		super(newOperation);
		// TODO Auto-generated constructor stub
	}

	@Override
	public EList<EAnnotation> getEAnnotations() {
		EList<EAnnotation> result = new BasicEList<EAnnotation>();
			for (EAnnotation annot : origOperation.getEAnnotations())
				result.add(new EAnnotationUMLAdapter(annot));
		return result;
	}

	@Override
	public EAnnotation getEAnnotation(String source) {
		return new EAnnotationUMLAdapter(origOperation.getEAnnotation(source));
	}

	@Override
	public String getName() {
		return origOperation.getName();
	}

	@Override
	public EClass getEContainingClass() {
		return new EClassUMLAdapter(origOperation.getClass_());
	}

	@Override
	public EList<EParameter> getEParameters() {
		List<Parameter> paramList = origOperation.getOwnedParameters();
		EList<EParameter> result = new BasicEList<EParameter>();
	    if (! paramList.isEmpty()){
	    	for (Parameter param : paramList)
	    		result.add(new EParameterUMLAdapter(param));
	    }
	return result;
	}

	@Override
	public EClassifier getEType() {
		return new EClassifierUMLAdapter((Classifier) origOperation.getType());
	}

	@Override
	public boolean isMany() {
		return origOperation.getUpper() == -1 || origOperation.getUpper() > 1 ;
	}

}
