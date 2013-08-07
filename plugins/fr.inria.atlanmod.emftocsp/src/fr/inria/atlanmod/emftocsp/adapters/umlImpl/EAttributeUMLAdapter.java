package fr.inria.atlanmod.emftocsp.adapters.umlImpl;

import fr.inria.emftocsp.adapters.EAttributeAdapter;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.DataType;
import org.eclipse.uml2.uml.Property;
public class EAttributeUMLAdapter extends EAttributeAdapter<Property> implements EStructuralFeature{

	protected Resource owningResource;
	public EAttributeUMLAdapter(Property newAttribute) {
		super(newAttribute);
	}


	@Override
	public int getLowerBound() {
		return origAttribute.getLower();
	}

	@Override
	public int getUpperBound() {
		return origAttribute.getUpper();
	}

	@Override
	public boolean isMany() {
		return origAttribute.isMultivalued();
	}

	@Override
	public EClassifier getEType() {
		return new EClassifierUMLAdapter((Classifier)origAttribute.getType());
	}

	@Override
	public String getName() {
		return origAttribute.getName();
	}

	@Override
	public EList<EAnnotation> getEAnnotations() {
		EList<EAnnotation> result = new BasicEList<EAnnotation>();
			for (EAnnotation annot : origAttribute.getEAnnotations())
				result.add(new EAnnotationUMLAdapter(annot));
		return result;
	}

	@Override
	public EAnnotation getEAnnotation(String source) {
		if (origAttribute.getEAnnotation(source) != null)
			return new EAnnotationUMLAdapter(origAttribute.getEAnnotation(source));
		return null;
	}

	@Override
	public EDataType getEAttributeType() {
		return new EDataTypeUMLAdapter((DataType)origAttribute.getType());
		
	}

	@Override
	public EClass getEContainingClass() {
		return new EClassUMLAdapter(origAttribute.getClass_());
	}

}
