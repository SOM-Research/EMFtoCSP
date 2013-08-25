package fr.inria.atlanmod.emftocsp.adapters;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EModelElement;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;

public abstract class EAnnotationAdapter<A> implements EAnnotation {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((origEAnnotation == null) ? 0 : origEAnnotation.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof EAnnotationAdapter))
			return false;
		EAnnotationAdapter other = (EAnnotationAdapter) obj;
		if (origEAnnotation == null) {
			if (other.origEAnnotation != null)
				return false;
		} else if (!origEAnnotation.equals(other.origEAnnotation))
			return false;
		return true;
	}

	protected A origEAnnotation;
	
	public EAnnotationAdapter(A newEAnnotation){
		origEAnnotation = newEAnnotation;
	}
	@Override
	public EList<EAnnotation> getEAnnotations() {
		throw new UnsupportedOperationException();
	}

	@Override
	public EAnnotation getEAnnotation(String source) {
		throw new UnsupportedOperationException();
	}

	@Override
	public EClass eClass() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Resource eResource() {
		throw new UnsupportedOperationException();
	}

	@Override
	public EObject eContainer() {
		throw new UnsupportedOperationException();
	}

	@Override
	public EStructuralFeature eContainingFeature() {
		throw new UnsupportedOperationException();
	}

	@Override
	public EReference eContainmentFeature() {
		throw new UnsupportedOperationException();
	}

	@Override
	public EList<EObject> eContents() {
		throw new UnsupportedOperationException();
	}

	@Override
	public TreeIterator<EObject> eAllContents() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean eIsProxy() {
		return false;
	}

	@Override
	public EList<EObject> eCrossReferences() {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object eGet(EStructuralFeature feature) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object eGet(EStructuralFeature feature, boolean resolve) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void eSet(EStructuralFeature feature, Object newValue) {
		throw new UnsupportedOperationException();

	}

	@Override
	public boolean eIsSet(EStructuralFeature feature) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void eUnset(EStructuralFeature feature) {
		throw new UnsupportedOperationException();

	}

	@Override
	public Object eInvoke(EOperation operation, EList<?> arguments)
			throws InvocationTargetException {
		throw new UnsupportedOperationException();
	}

	@Override
	public EList<Adapter> eAdapters() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean eDeliver() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void eSetDeliver(boolean deliver) {
		throw new UnsupportedOperationException();

	}

	@Override
	public void eNotify(Notification notification) {
		throw new UnsupportedOperationException();

	}

	@Override
	public abstract String getSource();

	@Override
	public abstract EMap<String, String> getDetails();

	@Override
	public abstract EModelElement getEModelElement();

	@Override
	public void setEModelElement(EModelElement value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setSource(String value) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public EList<EObject> getContents() {
		throw new UnsupportedOperationException();
	}

	@Override
	public EList<EObject> getReferences() {
		throw new UnsupportedOperationException();
	}

}
