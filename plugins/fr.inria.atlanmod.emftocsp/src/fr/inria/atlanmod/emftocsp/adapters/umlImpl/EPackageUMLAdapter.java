package fr.inria.atlanmod.emftocsp.adapters.umlImpl;

import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData.EPackageExtendedMetaData;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.PackageableElement;
import org.eclipse.uml2.uml.UMLPackage;

import fr.inria.emftocsp.adapters.EPackageAdapter;

public class EPackageUMLAdapter extends EPackageAdapter<Package> {

	protected Resource owningResource;
	public EPackageUMLAdapter(Package newPackage) {
		super(newPackage);
	}

	@Override
	public String getName() {
		return origPackage.getName();
	}

	@Override
	public EList<EAnnotation> getEAnnotations() {
		EList<EAnnotation> result = new BasicEList<EAnnotation>();
			for (EAnnotation annot : origPackage.getEAnnotations())
				result.add(new EAnnotationUMLAdapter(annot));
		return result;
	}

	public EAnnotation getEAnnotation(String source) {
		if (origPackage.getEAnnotation(source) != null)
			return new EAnnotationUMLAdapter(origPackage.getEAnnotation(source));
		return null;
	}

	@Override
	public Resource eResource() {
		return origPackage.eResource();
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
						result.add(new EClassUMLAdapter((Class) element));
		return result;
	}

	@Override
	public EList<EPackage> getESubpackages() {
		EList <EPackage> result = new BasicEList<EPackage>();
			for (Package pck :origPackage.getNestedPackages())
				result.add(new EPackageUMLAdapter(pck));
		return result;
	}
	@Override
	public EPackage getESuperPackage() {
		return new EPackageUMLAdapter(origPackage.getNestingPackage());
	}

	@Override
	public String getNsURI() {
		return origPackage.getURI() != "" ? origPackage.getURI() : origPackage.getQualifiedName();
	}

	@Override
	public EClassifier getEClassifier(String name) {
		return new EClassifierUMLAdapter((Classifier)origPackage.getPackagedElement(name));
	}

	@Override
	public EPackageExtendedMetaData getExtendedMetaData() {
		return null;
	}

	

}
