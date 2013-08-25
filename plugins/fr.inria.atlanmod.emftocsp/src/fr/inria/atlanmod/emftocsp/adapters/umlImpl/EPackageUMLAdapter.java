package fr.inria.atlanmod.emftocsp.adapters.umlImpl;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.UMLPackage;

import fr.inria.atlanmod.emftocsp.adapters.EPackageAdapter;

public class EPackageUMLAdapter extends EPackageAdapter<Package> {

	protected Resource owningResource;
	public EPackageUMLAdapter(Package newPackage, Resource owningResource) {
		super(newPackage);
		this.owningResource =owningResource;
		
	}

	@Override
	public String getName() {
		return origPackage.getName();
	}

	@Override
	public EList<EAnnotation> getEAnnotations() {
		EList<EAnnotation> result = new BasicEList<EAnnotation>();
			for (EAnnotation annot : origPackage.getEAnnotations())
				result.add(new EAnnotationUMLAdapter(annot,owningResource));
		return result;
	}

	public EAnnotation getEAnnotation(String source) {
		if (origPackage.getEAnnotation(source) != null)
			return new EAnnotationUMLAdapter(origPackage.getEAnnotation(source),owningResource);
		return null;
	}

	@Override
	public Resource eResource() {
		return owningResource;
	}

	@Override
	public String getNsPrefix() {
		return ((UMLPackage) origPackage).getNsPrefix();
	}

	@Override
	public EList<EClassifier> getEClassifiers() {
		EList <EClassifier> result = new BasicEList<EClassifier>();
			for (PackageableElement element : origPackage.getPackagedElements())
				if (element instanceof Classifier )
					if (element instanceof Class)
						result.add(((EResourceUMLAdapter)owningResource).getClassIfNotExists(new EClassUMLAdapter((Class) element,owningResource)));
		return result;
	}

	@Override
	public EList<EPackage> getESubpackages() {
		EList <EPackage> result = new BasicEList<EPackage>();
			for (Package pck :origPackage.getNestedPackages())
				result.add(new EPackageUMLAdapter(pck,owningResource));
		return result;
	}
	@Override
	public EPackage getESuperPackage() {
		if (origPackage.getNestingPackage() != null)
		return new EPackageUMLAdapter(origPackage.getNestingPackage(),owningResource);
		return null;
	}

	@Override
	public String getNsURI() {
		return origPackage.getURI() != "" ? origPackage.getURI() : origPackage.getQualifiedName();
	}

	@Override
	public EClassifier getEClassifier(String name) {
		if ((Classifier)origPackage.getPackagedElement(name) != null)
			if (origPackage.getPackagedElement(name) instanceof Class)
				return ((EResourceUMLAdapter)owningResource).getClassIfNotExists(new EClassUMLAdapter((Class)origPackage.getPackagedElement(name),owningResource));
			else 
				return ((EResourceUMLAdapter)owningResource).getClassIfNotExists(new EClassifierUMLAdapter((Classifier)origPackage.getPackagedElement(name),owningResource));
		return null;
	}

	@Override
	public EObject eContainer() {
		return getESuperPackage() != null ? getESuperPackage() : null ;
	}

	@Override
	public EFactory getEFactoryInstance() {
		return null;
	}
	

}
