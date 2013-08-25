package fr.inria.atlanmod.emftocsp.adapters.umlImpl;

import java.util.ArrayList;
import java.util.List;


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
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.Property;

import fr.inria.atlanmod.emftocsp.adapters.EClassAdapter;
import fr.inria.atlanmod.emftocsp.adapters.umlImpl.EResourceUMLAdapter;

public class EClassUMLAdapter extends EClassAdapter<Class> implements EClassifier{

	protected Resource owningResource;
	public EClassUMLAdapter(Class newClass, Resource owningResource) {
		super(newClass);
		this.owningResource = owningResource;
		
	}

	@Override
	public EPackage getEPackage() {
		Assert.isNotNull(origClass.getPackage(),"NULL Package");
		return new EPackageUMLAdapter(origClass.getPackage(),owningResource);
	}


	@Override
	public String getName() {
		return origClass.getName();
	}

	@Override
	public EObject eContainer() {
		if (origClass.eContainer() instanceof Package)
			return new EPackageUMLAdapter((Package) origClass.eContainer(), owningResource);
		else if (origClass.eContainer() instanceof Class )
		return ((EResourceUMLAdapter)owningResource).getClassIfNotExists(new EClassUMLAdapter((Class) origClass.eContainer(),owningResource));
		try {
			throw new Exception ("Unhandled Type : " + origClass.eContainer().eClass());
		} catch (Exception e) {
			e.printStackTrace();
		} 
		 return origClass.eContainer();
	}

	@Override
	public EList<EClass> getESuperTypes() {
		EList<EClass> result = new BasicEList<EClass>();
			for (Generalization g : origClass.getGeneralizations() ){
				if ((Class)g.getGeneral() != origClass) 
					result.add((EClass)((EResourceUMLAdapter)owningResource).getClassIfNotExists(new EClassUMLAdapter((Class)g.getGeneral(),owningResource)));}
		return result ;
	}

	@Override
	public EList<EClass> getEAllSuperTypes() {
		//TODO
		EList<EClass> result = new BasicEList<EClass>();
		EList<Class> allSuperTypes = new BasicEList<Class>();
		allSuperTypes(origClass,allSuperTypes);
		for (Class cls : allSuperTypes)
			result.add((EClass)((EResourceUMLAdapter)owningResource).getClassIfNotExists(new EClassUMLAdapter(cls,owningResource)));
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
			for (Property pro : origClass.getAttributes())
				result.add(new EAttributeUMLAdapter(pro,owningResource));
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
					result.add(new EReferenceUMLAdapter(pro,owningResource));
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
	public EList<EOperation> getEOperations() {
		EList<EOperation> result = new BasicEList<EOperation>();
			for (Operation operation : origClass.getOperations()){
				if (operation != null)
					result.add(new EOperationUMLAdapter(operation,owningResource));}
		return result;
	}

	@Override
	public EList<EOperation> getEAllOperations() {
		EList<EOperation> result = new BasicEList<EOperation>();
		for (Operation operation : origClass.getAllOperations())
			result.add(new EOperationUMLAdapter(operation,owningResource));
		return result;
	}

	@Override
	public EList<EAnnotation> getEAnnotations() {
		EList<EAnnotation> result = new BasicEList<EAnnotation>();
			for (EAnnotation annot : origClass.getEAnnotations())
				result.add(new EAnnotationUMLAdapter(annot,owningResource));
		return result;
	}

	public EAnnotation getEAnnotation(String source) {
		if (origClass.getEAnnotation(source) != null)
			return new EAnnotationUMLAdapter(origClass.getEAnnotation(source),owningResource);
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

	@Override
	public EList<EStructuralFeature> getEStructuralFeatures() {
		EList<EStructuralFeature> result = new BasicEList<EStructuralFeature>();
		result.addAll(getEAttributes());
		result.addAll(getEReferences());
		return result;
	}

	@Override
	public EList<EStructuralFeature> getEAllStructuralFeatures() {
		EList<EStructuralFeature> result = new BasicEList<EStructuralFeature>();
		result.addAll(getEStructuralFeatures());
		for (EClass cls : getESuperTypes())
			result.addAll(cls.getEAllStructuralFeatures());
		return result;
	}

	@Override
	public EList<EReference> getEAllReferences() {
		EList<EReference> result = new BasicEList<EReference>();
		result.addAll(getEReferences());
		for (EClass cls : getESuperTypes())
			result.addAll(cls.getEAllReferences());
		return result;
	}

	@Override
	public EList<EReference> getEAllContainments() {
		EList<EReference> result = new BasicEList<EReference>();
		for (EReference ref : getEAllReferences())
			if (ref.isContainment())
				result.add(ref);
		return result;				
	}

	@Override
	public EList<EAttribute> getEAllAttributes() {
		EList<EAttribute> result = new BasicEList<EAttribute>();
		result.addAll(getEAttributes());
		for (EClass cls : getESuperTypes())
			result.addAll(cls.getEAllAttributes());
		return result;
	}

	@Override
	public boolean isSuperTypeOf(EClass someClass) {
		for (Generalization gen : origClass.getGeneralizations())
			if (gen.getGeneral().getName().equalsIgnoreCase(origClass.getName()))				
				if (((EResourceUMLAdapter)owningResource).
						getClassIfNotExists(new EClassUMLAdapter((Class)gen
								.getSpecific(),owningResource)).equals(someClass))
					return true;							
		return false;

	}

	public Class getOriginalClass() {
		return origClass;
	}
}
