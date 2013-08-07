package fr.inria.atlanmod.emftocsp.adapters.umlImpl;



import java.util.List;

import fr.inria.emftocsp.adapters.EResourceAdapter;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.resource.UMLResource;

public class EResourceUMLAdapter extends EResourceAdapter<UMLResource> {

	protected List<EClass> loadedClasses;
	public EResourceUMLAdapter(UMLResource newResource) {
		super(newResource);
		loadedClasses = new BasicEList<EClass>();
	}

	@Override
	public EList<EObject> getContents() {
		EList<EObject> result = new BasicEList<EObject>();
		//EObject tokenEObject =null;
		for (EObject eObject : origResource.getContents())
			if ( eObject instanceof Package)
				result.add(new EPackageUMLAdapter((Package)eObject));
		return result;
	}

	@Override
	public TreeIterator<EObject> getAllContents() {
		return origResource.getAllContents();
	}

	@Override
	public URI getURI() {
		return origResource.getURI();
	}

	@Override
	public ResourceSet getResourceSet() {
		return origResource.getResourceSet();
	}

	public EClass getClassIfNotExists(EClass cls){
		Assert.isNotNull(cls);
		for (EClass eCls : loadedClasses)
			if (eCls.equals(cls))
				return eCls;
			return cls;
	}
	
}
