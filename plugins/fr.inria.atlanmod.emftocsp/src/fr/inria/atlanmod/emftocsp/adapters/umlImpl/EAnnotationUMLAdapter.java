package fr.inria.atlanmod.emftocsp.adapters.umlImpl;

import fr.inria.emftocsp.adapters.EAnnotationAdapter;

import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Property;
public class EAnnotationUMLAdapter extends EAnnotationAdapter<EAnnotation> {

	public EAnnotationUMLAdapter(EAnnotation newEAnnotation) {
		super(newEAnnotation);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getSource() {
		return origEAnnotation.getSource();
	}

	@Override
	public EMap<String, String> getDetails() {
		return origEAnnotation.getDetails();
	}

	@Override
	public EModelElement getEModelElement() {
		
		EModelElement element = origEAnnotation.getEModelElement();
			if (element instanceof Operation)
				return (new EOperationUMLAdapter((Operation) element));
			if (element instanceof Classifier)
				return new EClassifierUMLAdapter((Classifier)element);
			if (element instanceof Property)
				return new EStructuralFeatureUMLAdapter((Property)element);
		return origEAnnotation.getEModelElement();
	}

}
