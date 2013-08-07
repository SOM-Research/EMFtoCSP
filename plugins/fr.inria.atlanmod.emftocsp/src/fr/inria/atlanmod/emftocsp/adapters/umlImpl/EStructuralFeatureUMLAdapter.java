package fr.inria.atlanmod.emftocsp.adapters.umlImpl;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Property;

import fr.inria.emftocsp.adapters.EStructuralFeatureAdapter;

public class EStructuralFeatureUMLAdapter extends
		EStructuralFeatureAdapter<Property> {

	protected Resource owningResource;
	public EStructuralFeatureUMLAdapter(Property newEStructFeat) {
		super(newEStructFeat);
	}

	@Override
	public int getLowerBound() {
		return origEStructuralFeature.getLower();
	}

	@Override
	public int getUpperBound() {
		return origEStructuralFeature.getUpper();
	}

	@Override
	public boolean isMany() {
		return origEStructuralFeature.isMultivalued();
	}

	@Override
	public EClassifier getEType() {
		return new EClassifierUMLAdapter((Classifier)origEStructuralFeature.getType());
	}

	@Override
	public String getName() {
		return origEStructuralFeature.getName();
	}

	@Override
	public EList<EAnnotation> getEAnnotations() {
		EList<EAnnotation> result = new BasicEList<EAnnotation>();
			for (EAnnotation annot : origEStructuralFeature.getEAnnotations())
				result.add(new EAnnotationUMLAdapter(annot));
		return result;
	}

	public EAnnotation getEAnnotation(String source) {
		if (origEStructuralFeature.getEAnnotation(source) != null)
			return new EAnnotationUMLAdapter(origEStructuralFeature.getEAnnotation(source));
		return null;
	}

	@Override
	public EObject eContainer() {
		return origEStructuralFeature.eContainer();
	}

	@Override
	public EStructuralFeature eContainingFeature() {
		return origEStructuralFeature.eContainingFeature();
	}

	@Override
	public EClass getEContainingClass() {
		return new EClassUMLAdapter(origEStructuralFeature.getClass_());
	
	}



}
