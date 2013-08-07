package fr.inria.emftocsp.adapters;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData.EPackageExtendedMetaData;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData.EPackageExtendedMetaData.Holder;

public abstract class EPackageAdapter<P> implements EPackage, Holder {

	protected P origPackage;
	
	public EPackageAdapter (P newPackage){
		origPackage = newPackage;
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
	public abstract EAnnotation getEAnnotation(String source) ;

	@Override
	public  EClass eClass(){
		throw new UnsupportedOperationException(); 
	}

	@Override
	public abstract Resource eResource();

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
	public abstract String getNsURI();

	@Override
	public void setNsURI(String value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public abstract String getNsPrefix();

	@Override
	public void setNsPrefix(String value) {
		throw new UnsupportedOperationException();


	}

	@Override
	public EFactory getEFactoryInstance() {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setEFactoryInstance(EFactory value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void setExtendedMetaData(
			EPackageExtendedMetaData ePackageExtendedMetaData) {
		}
	@Override
	public abstract EList<EClassifier> getEClassifiers() ;

	@Override
	public abstract EList<EPackage> getESubpackages() ;

	@Override
	public abstract EPackage  getESuperPackage() ;

	@Override
	public abstract EClassifier getEClassifier(String name);

	@Override
	public EPackageExtendedMetaData getExtendedMetaData() {
		// TODO Auto-generated method stub
		return null;
	}

}
