package fr.inria.atlanmod.emftocsp.adapters.umlImpl;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.uml2.uml.AggregationKind;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Class;
import fr.inria.emftocsp.adapters.EReferenceAdapter;


public class EReferenceUMLAdapter extends EReferenceAdapter<Property> implements EStructuralFeature {

	protected Resource owningResource;
	public EReferenceUMLAdapter(Property newResource) {
		super(newResource);
	}

	@Override
	public EClass getEContainingClass() {
		return new EClassUMLAdapter(origEReference.getClass_());
	}

	@Override
	public int getLowerBound() {
		return origEReference.getLower();
	}

	@Override
	public int getUpperBound() {
		return origEReference.getUpper();
	}

	@Override
	public boolean isMany() {
		return origEReference.isMultivalued();
	}

	@Override
	public EClass getEType() {
		return new EClassUMLAdapter((Class)origEReference.getType());
	}

	@Override
	public String getName() {
		return origEReference.getName();
	}

	@Override
	public EList<EAnnotation> getEAnnotations() {
		EList<EAnnotation> result = new BasicEList<EAnnotation>();
			for (EAnnotation annot : origEReference.getEAnnotations())
				result.add(new EAnnotationUMLAdapter(annot));
		return result;
	}

	public EAnnotation getEAnnotation(String source) {
		if (origEReference.getEAnnotation(source) != null)
			return new EAnnotationUMLAdapter(origEReference.getEAnnotation(source));
		return null;
	}

	@Override
	public EObject eContainer() {
		return origEReference.eContainer();
	}

	@Override
	public boolean isContainment() {
		return origEReference.getOtherEnd().getAggregation().getLiteral().equals(AggregationKind.COMPOSITE_LITERAL.getLiteral());
	}

	@Override
	public boolean isContainer() {
		if (getEOpposite()== null) return false;
		return getEOpposite().isContainment();
	}

	@Override
	public EReference getEOpposite() {
		if (origEReference.getOtherEnd()!= null )
			return new EReferenceUMLAdapter(origEReference.getOtherEnd());
		return null;
	}

	@Override
	public EClass getEReferenceType() {
		return new EClassUMLAdapter((Class)origEReference.getType());
	}



}
