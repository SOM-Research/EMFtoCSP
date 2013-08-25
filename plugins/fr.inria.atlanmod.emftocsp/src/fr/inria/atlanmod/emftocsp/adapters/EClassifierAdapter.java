package fr.inria.atlanmod.emftocsp.adapters;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.ETypeParameter;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData.EClassifierExtendedMetaData;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData.EClassifierExtendedMetaData.Holder;
import org.eclipse.uml2.uml.DataType;

public  abstract class EClassifierAdapter <EC> implements EClassifier, Holder{
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((origClassifier == null) ? 0 : origClassifier.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof EClassifierAdapter))
			return false;
		EClassifierAdapter other = (EClassifierAdapter) obj;
		if (origClassifier == null) {
			if (other.origClassifier != null)
				return false;
		} else if (!origClassifier.equals(other.origClassifier))
			return false;
		return true;
	}

	protected EC origClassifier;
	
	public EClassifierAdapter (EC newClassifier) {
		origClassifier = newClassifier;
	}

	@Override
	public  abstract String getName();

	@Override
	public void setName(String value) {
		throw new UnsupportedOperationException();
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
		if (origClassifier instanceof Class)
			return EcorePackage.eINSTANCE.getEClass();
		if (origClassifier instanceof DataType)
			return EcorePackage.eINSTANCE.getEDataType();
		return EcorePackage.eINSTANCE.getEClassifier();
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
	public abstract String getInstanceClassName();

	@Override
	public void setInstanceClassName(String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public abstract Class<?> getInstanceClass();
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
	public  int getClassifierID(){
		throw new UnsupportedOperationException();
	}

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

}
