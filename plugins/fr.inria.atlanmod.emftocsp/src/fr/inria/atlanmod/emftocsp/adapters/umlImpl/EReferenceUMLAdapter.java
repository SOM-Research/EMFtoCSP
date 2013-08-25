package fr.inria.atlanmod.emftocsp.adapters.umlImpl;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.uml2.uml.AggregationKind;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.Class;

import fr.inria.atlanmod.emftocsp.adapters.EReferenceAdapter;


public class EReferenceUMLAdapter extends EReferenceAdapter<Property> implements EStructuralFeature {

	protected Resource owningResource;
	public EReferenceUMLAdapter(Property newResource, Resource owningResource) {
		super(newResource);
		this.owningResource = owningResource;
	}

	@Override
	public EClass getEContainingClass() {	
			return (EClass) getEOpposite().getEType();
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
	public EClassifier getEType() {
		//TODO
		return (EClass) ((EResourceUMLAdapter)owningResource).getClassIfNotExists( new EClassUMLAdapter((Class)origEReference.getType(),owningResource));
	}

	@Override
	public String getName() {
		return origEReference.getName();
	}

	@Override
	public EList<EAnnotation> getEAnnotations() {
		EList<EAnnotation> result = new BasicEList<EAnnotation>();
			for (EAnnotation annot : origEReference.getEAnnotations())
				result.add(new EAnnotationUMLAdapter(annot,owningResource));
		return result;
	}

	public EAnnotation getEAnnotation(String source) {
		if (origEReference.getEAnnotation(source) != null)
			return new EAnnotationUMLAdapter(origEReference.getEAnnotation(source),owningResource);
		return null;
	}

	@Override
	public EObject eContainer() {
		return origEReference.eContainer();
	}

	@Override
	public boolean isContainment() {
		return origEReference.getAggregation().getLiteral().equals(AggregationKind.COMPOSITE_LITERAL.getLiteral());
	}

	@Override
	public boolean isContainer() {
		if (getEOpposite()== null) return false;
		return getEOpposite().isContainment();
	}

	@Override
	public EReference getEOpposite() {
		Property otherEnd = origEReference.getOtherEnd();
		if (origEReference.getOtherEnd()!= null )
			return new EReferenceUMLAdapter(origEReference.getOtherEnd(),owningResource);
		return null;
	}

	@Override
	public EClass getEReferenceType() {
		return (EClass)((EResourceUMLAdapter)owningResource).getClassIfNotExists(new EClassUMLAdapter((Class)origEReference.getType(),owningResource));
	}

	@Override
	public boolean isOrdered() {
		return origEReference.isOrdered();
	}

	@Override
	public boolean isUnique() {
		return origEReference.isUnique();
	}

	@Override
	public boolean isRequired() {
		return isRequired();
	}



}
