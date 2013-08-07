package fr.inria.atlanmod.emftocsp.adapters.umlImpl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.inria.emftocsp.adapters.EClassAdapter;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData.EClassifierExtendedMetaData;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData.EClassifierExtendedMetaData.Holder;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.Property;

public class EClassUMLAdapter extends EClassAdapter<Class> implements EClassifier{

	protected Resource owningResource;
	public EClassUMLAdapter(Class newClass) {
		super(newClass);
		
	}

	@Override
	public EPackage getEPackage() {
		Assert.isNotNull(origClass.getPackage(),"NULL Package");
		return new EPackageUMLAdapter(origClass.getPackage());
	}


	@Override
	public String getName() {
		return origClass.getName();
	}

	@Override
	public EObject eContainer() {
		return origClass.eContainer();
	}

	@Override
	public EList<EClass> getESuperTypes() {
		EList<EClass> result = new BasicEList<EClass>();
			for (Generalization g : origClass.getGeneralizations() ){
				if ((Class)g.getGeneral() != origClass) 
					result.add(new EClassUMLAdapter((Class)g.getGeneral()));}
		return result ;
	}

	@Override
	public EList<EClass> getEAllSuperTypes() {
		EList<EClass> result = new BasicEList<EClass>();
		EList<Class> allSuperTypes = new BasicEList<Class>();
		allSuperTypes(origClass,allSuperTypes);
		for (Class cls : allSuperTypes)
			result.add(new EClassUMLAdapter(cls));
	return result ;
	}

	private void allSuperTypes(Class cls , EList<Class> allSuperTypes) {
		if (cls.getSuperClasses().isEmpty()) return;
		for (Class clas : cls.getSuperClasses()){
			allSuperTypes.add(clas);
			allSuperTypes(clas, allSuperTypes);
		}	
	}

	@Override
	public EList<EAttribute> getEAttributes() {
		EList<EAttribute> result = new BasicEList<EAttribute>();
		List <Property> pros = origClass.getAttributes();
			for (Property pro : origClass.getAttributes())
				result.add(new EAttributeUMLAdapter(pro));
		return result;
	}

	@Override
	public EList<EAttribute> getEAllAttributes() {
		EList<EAttribute> result = new BasicEList<EAttribute>();
		for (Property pro : origClass.getAllAttributes())
			result.add(new EAttributeUMLAdapter(pro));
	return result;
	}

	@Override
	public EList<EReference> getEReferences() {
		EList<EReference> result = new BasicEList<EReference>();
		ArrayList<Association> asList = getAssociationListFromPackage(origClass.getPackage());
		for (Association ass : asList){
			List<Property> pros = ass.getOwnedEnds();
			for (Property pro : ass.getOwnedEnds()){
				String msg = pro.getOtherEnd().getType() == null ? "NULL" : pro.getOtherEnd().getType().toString();
				if (pro.getOtherEnd().getType().equals(origClass))
					result.add(new EReferenceUMLAdapter(pro));
			}
		}
		return result;
	}

	private ArrayList<Association> getAssociationListFromPackage(Package p) {
		    ArrayList<Association> asList = new ArrayList<Association>();
		    for (PackageableElement pkgElement : p.getPackagedElements()) 
		      if (pkgElement instanceof Association) 
		        asList.add((Association)pkgElement);
		    return asList;
		  }
	 
	@SuppressWarnings("unused")
	private List<Property> getReferences() {
		List <Property> result = new ArrayList<Property>(); 
		EList<Association> associations = origClass.getAssociations();
		for (Association association : associations)
			result.addAll(association.getOwnedEnds());
		return result;
	}

	@Override
	public EList<EReference> getEAllReferences() {
		// TODO Implementing it if needed
		return null;
	}

	@Override
	public EList<EReference> getEAllContainments() {
		// TODO implementing it if needed
		return null;
	}

	@Override
	public EList<EOperation> getEOperations() {
		EList<EOperation> result = new BasicEList<EOperation>();
			for (Operation operation : origClass.getOperations()){
				if (operation != null)
					result.add(new EOperationUMLAdapter(operation));}
		return result;
	}

	@Override
	public EList<EOperation> getEAllOperations() {
		EList<EOperation> result = new BasicEList<EOperation>();
		for (Operation operation : origClass.getAllOperations())
			result.add(new EOperationUMLAdapter(operation));
		return result;
	}

	@Override
	public EList<EAnnotation> getEAnnotations() {
		EList<EAnnotation> result = new BasicEList<EAnnotation>();
			for (EAnnotation annot : origClass.getEAnnotations())
				result.add(new EAnnotationUMLAdapter(annot));
		return result;
	}

	public EAnnotation getEAnnotation(String source) {
		if (origClass.getEAnnotation(source) != null)
			return new EAnnotationUMLAdapter(origClass.getEAnnotation(source));
		return null;
	}

	@Override
	public boolean isAbstract() {
		return origClass.isAbstract();
	}

	@Override
	public EClass eClass() {
		return EcorePackage.eINSTANCE.getEClass();
	}


}
