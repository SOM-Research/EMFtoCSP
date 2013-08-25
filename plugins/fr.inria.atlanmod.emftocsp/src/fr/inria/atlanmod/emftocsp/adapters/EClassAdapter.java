package fr.inria.atlanmod.emftocsp.adapters;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EGenericType;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.ETypeParameter;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData.EClassifierExtendedMetaData;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData.EClassifierExtendedMetaData.Holder;

public abstract class EClassAdapter<C> implements EClass, Holder {

	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((origClass == null) ? 0 : origClass.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof EClassAdapter))
			return false;
		@SuppressWarnings("rawtypes")
		EClassAdapter other = (EClassAdapter) obj;
		if (origClass == null) {
			if (other.origClass != null)
				return false;
		} else if (!origClass.equals(other.origClass))
			return false;
		return true;
	}
	
	protected C origClass;
	
	public EClassAdapter (C newClass){
		if (newClass == null) throw new NullPointerException("original class is null");
		origClass = newClass;
	}
	@Override
	public String getInstanceClassName() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setInstanceClassName(String value) {
		throw new UnsupportedOperationException();

	}

	@Override
	public Class<?> getInstanceClass() {
		throw new UnsupportedOperationException();

	}

	@Override
	public void setInstanceClass(Class<?> value) {
		throw new UnsupportedOperationException();

	}

	@Override
	public Object getDefaultValue() {
		throw new UnsupportedOperationException(); 
	}

	@Override
	public String getInstanceTypeName() {
		throw new UnsupportedOperationException(); 
	}

	@Override
	public void setInstanceTypeName(String value) {
		throw new UnsupportedOperationException(); 

	}

	@Override
	public abstract EPackage getEPackage();

	@Override
	public EList<ETypeParameter> getETypeParameters() {
		throw new UnsupportedOperationException(); 
	}

	@Override
	public boolean isInstance(Object object) {
		throw new UnsupportedOperationException(); 
	}

	@Override
	public int getClassifierID(){
		throw new UnsupportedOperationException();
	}

	@Override
	public abstract String getName();

	@Override
	public void setName(String value) {
		throw new UnsupportedOperationException(); 
	}

	@Override
	public abstract EList<EAnnotation> getEAnnotations();

	@Override
	public abstract EAnnotation getEAnnotation(String source);

	@Override
	public abstract EClass eClass();

	@Override
	public Resource eResource() {
		throw new UnsupportedOperationException(); 
	}

	@Override
	public abstract EObject eContainer() ;

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
	public abstract boolean  isAbstract();

	@Override
	public void setAbstract(boolean value) {
		throw new UnsupportedOperationException();	

	}

	@Override
	public boolean isInterface() {
		throw new UnsupportedOperationException();	
	}

	@Override
	public void setInterface(boolean value) {
		throw new UnsupportedOperationException();	
	}

	@Override
	public abstract EList<EClass>  getESuperTypes();

	@Override
	public abstract EList<EClass> getEAllSuperTypes();

	@Override
	public EAttribute getEIDAttribute() {
		throw new UnsupportedOperationException(); 
	}

	

	@Override
	public EList<EGenericType> getEGenericSuperTypes() {
		throw new UnsupportedOperationException();	
	}

	@Override
	public EList<EGenericType> getEAllGenericSuperTypes() {
		throw new UnsupportedOperationException();	
	}

	@Override
	public abstract EList<EAttribute> getEAttributes();

	@Override
	public abstract EList<EAttribute> getEAllAttributes();

	@Override
	public abstract EList<EReference> getEReferences();

	@Override
	public abstract EList<EReference> getEAllReferences() ;

	@Override
	public abstract EList<EReference> getEAllContainments();



	@Override
	public abstract EList<EOperation> getEOperations() ;

	@Override
	public abstract EList<EOperation> getEAllOperations() ;
	
	@Override
	public abstract boolean isSuperTypeOf(EClass someClass);
	
	@Override
	public int getFeatureCount() {
		throw new UnsupportedOperationException(); 
	}

	@Override
	public EStructuralFeature getEStructuralFeature(int featureID) {
		throw new UnsupportedOperationException(); 
	}

	@Override
	public EStructuralFeature getEStructuralFeature(String featureName) {
		throw new UnsupportedOperationException(); 
	}

	@Override
	public int getOperationCount() {
		throw new UnsupportedOperationException(); 
	}

	@Override
	public EOperation getEOperation(int operationID) {
		throw new UnsupportedOperationException(); 
	}

	@Override
	public int getOperationID(EOperation operation) {
		throw new UnsupportedOperationException(); 
	}

	@Override
	public EOperation getOverride(EOperation operation) {
		throw new UnsupportedOperationException(); 
	}

	@Override
	public EGenericType getFeatureType(EStructuralFeature feature) {
		throw new UnsupportedOperationException(); 
	}

	@Override
	public int getFeatureID(EStructuralFeature feature) {
		throw new UnsupportedOperationException(); 
	}
	@Override
	public abstract EList<EStructuralFeature> getEStructuralFeatures();
	@Override
	public abstract EList<EStructuralFeature> getEAllStructuralFeatures();
	@Override
	public void setExtendedMetaData(
			EClassifierExtendedMetaData eClassifierExtendedMetaData) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public EClassifierExtendedMetaData getExtendedMetaData() {
		// TODO Auto-generated method stub
		return null;
	}

	public C getOriginalClass(){
		return origClass;
	}
}
