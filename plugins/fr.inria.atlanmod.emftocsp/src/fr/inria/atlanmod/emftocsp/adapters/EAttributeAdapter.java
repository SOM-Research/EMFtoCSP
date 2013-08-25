package fr.inria.atlanmod.emftocsp.adapters;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData.EStructuralFeatureExtendedMetaData;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData.EStructuralFeatureExtendedMetaData.Holder;

public abstract class EAttributeAdapter<A> implements EAttribute, Holder {

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((origAttribute == null) ? 0 : origAttribute.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof EAttributeAdapter))
			return false;
		EAttributeAdapter other = (EAttributeAdapter) obj;
		if (origAttribute == null) {
			if (other.origAttribute != null)
				return false;
		} else if (!origAttribute.equals(other.origAttribute))
			return false;
		return true;
	}
	protected A origAttribute;
	
	public EAttributeAdapter(A newAttribute){
		origAttribute = newAttribute;
	}
	@Override
	public boolean isTransient() {
		throw new UnsupportedOperationException();	
	}

	@Override
	public void setTransient(boolean value) {
		throw new UnsupportedOperationException();		
	}

	@Override
	public boolean isVolatile() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setVolatile(boolean value) {
		
		
	}

	@Override
	public boolean isChangeable() {
		throw new UnsupportedOperationException();	
	}

	@Override
	public void setChangeable(boolean value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getDefaultValueLiteral() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setDefaultValueLiteral(String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object getDefaultValue() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setDefaultValue(Object value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean isUnsettable() {
		throw new UnsupportedOperationException();	
	}

	@Override
	public void setUnsettable(boolean value) {
		throw new UnsupportedOperationException();			
	}

	@Override
	public boolean isDerived() {
		throw new UnsupportedOperationException();	
	}

	@Override
	public void setDerived(boolean value) {
		throw new UnsupportedOperationException();	
		
	}

	@Override
	public abstract EClass getEContainingClass();

	@Override
	public Class<?> getContainerClass() {
		throw new UnsupportedOperationException();	
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
	public abstract int getLowerBound() ;

	@Override
	public void setLowerBound(int value) {
		throw new UnsupportedOperationException();		
	}

	@Override
	public abstract int getUpperBound();

	@Override
	public void setUpperBound(int value) {
		throw new UnsupportedOperationException();	
		
	}

	@Override
	public abstract boolean isMany() ;

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
	public abstract String getName() ;

	@Override
	public void setName(String value) {
		throw new UnsupportedOperationException();	
	}

	@Override
	public abstract EList<EAnnotation> getEAnnotations();

	@Override
	public abstract EAnnotation getEAnnotation(String source);

	@Override
	public EClass eClass() {
		throw new UnsupportedOperationException();	
	}

	@Override
	public Resource eResource() {
		throw new UnsupportedOperationException();	
	}

	@Override
	public abstract EObject eContainer();
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
	public boolean isID() {
		throw new UnsupportedOperationException();	
	}

	@Override
	public void setID(boolean value) {
		throw new UnsupportedOperationException();	
		
	}

	@Override
	public abstract EDataType getEAttributeType();
	

	@Override
	public int getFeatureID() {
		throw new UnsupportedOperationException();
	}
	@Override
	public EStructuralFeatureExtendedMetaData getExtendedMetaData() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setExtendedMetaData(
			EStructuralFeatureExtendedMetaData eStructuralFeatureExtendedMetaData) {
		// TODO Auto-generated method stub
		
	}

	public A getOriginalAttribute(){
		return origAttribute;
	}
}

