package fr.inria.atlanmod.emftocsp.adapters;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.ETypeParameter;
import org.eclipse.emf.ecore.resource.Resource;

import fr.inria.atlanmod.emftocsp.adapters.umlImpl.EClassUMLAdapter;

public abstract class EOperationAdapter<O> implements EOperation {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((origOperation == null) ? 0 : origOperation.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof EOperationAdapter))
			return false;
		EOperationAdapter other = (EOperationAdapter) obj;
		if (origOperation == null) {
			if (other.origOperation != null)
				return false;
		} else if (!origOperation.equals(other.origOperation))
			return false;
		return true;
	}

	protected O origOperation;
	
	public EOperationAdapter(O newOperation){
		origOperation = newOperation;
		
	}
	@Override
	public boolean isOrdered() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setOrdered(boolean value) {
		throw new UnsupportedOperationException();
		}

	@Override
	public boolean isUnique() {
		throw new UnsupportedOperationException();	
		}
	

	@Override
	public void setUnique(boolean value) {
		throw new UnsupportedOperationException();
		}

	@Override
	public int getLowerBound() {
		throw new UnsupportedOperationException();

	}

	@Override
	public void setLowerBound(int value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getUpperBound() {
		throw new UnsupportedOperationException();

	}

	@Override
	public void setUpperBound(int value) {
		throw new UnsupportedOperationException();
		}

	@Override
	public abstract boolean isMany();

	@Override
	public boolean isRequired() {
		throw new UnsupportedOperationException();
	}

	@Override
	public abstract EClassifier getEType();

	@Override
	public void setEType(EClassifier value) {
		throw new UnsupportedOperationException();

	}

	@Override
	public EGenericType getEGenericType() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setEGenericType(EGenericType value) {
		throw new UnsupportedOperationException();

	}

	@Override
	public abstract String getName();

	@Override
	public void setName(String value) {
		throw new UnsupportedOperationException();

	}

	@Override
	public abstract EList<EAnnotation> getEAnnotations() ;

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
	public abstract EClass getEContainingClass() ;

	@Override
	public abstract EList<EParameter> getEParameters();

	@Override
	public EList<EClassifier> getEExceptions() {
		throw new UnsupportedOperationException();
	}

	@Override
	public EList<EGenericType> getEGenericExceptions() {
		throw new UnsupportedOperationException();
	}

	@Override
	public int getOperationID() {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isOverrideOf(EOperation someOperation) {
		throw new UnsupportedOperationException();
	}

	@Override
	public EList<ETypeParameter> getETypeParameters() {
		throw new UnsupportedOperationException();
	}

}
