package fr.inria.atlanmod.emftocsp.adapters.umlImpl;

import java.util.List;

import fr.inria.atlanmod.emftocsp.adapters.EResourceAdapter;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EParameter;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.resource.UMLResource;

public class EResourceUMLAdapter extends EResourceAdapter<UMLResource> {

	protected List<EClassifier> loadedClasses;
	protected List<EParameter> loadedParameters;
 	public EResourceUMLAdapter(UMLResource newResource) {
		super(newResource);
		loadedClasses = new BasicEList<EClassifier>();
		loadedParameters = new BasicEList<EParameter>();
	}

	@Override
	public EList<EObject> getContents() {
		EList<EObject> result = new BasicEList<EObject>();
		//EObject tokenEObject =null;
		EPackage pac = null;
		for (EObject eObject : origResource.getContents()){
			if ( eObject instanceof Package){
				EPackage pck = new EPackageUMLAdapter((Package)eObject,this);
				result.add(pck);	
				EPackage.Registry registry =  origResource.getResourceSet().getPackageRegistry();
				if (!registry.containsKey(pck.getNsURI())){
					registry.put(pck.getNsURI(),pck);	
					pck = registry.getEPackage(pck.getNsURI());
					}
				for (Object next : registry.values()){
					if (next instanceof EPackage)
						 pac = (EPackage) next;
					System.out.print("");
					}
				}
			
		}
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

	public EClassifier getClassIfNotExists(EClassifier cls){
		Assert.isNotNull(cls);
		for (EClassifier eCls : loadedClasses)
			if (eCls.equals(cls))
				return eCls;
			loadedClasses.add(cls);
			return cls;
	}
	
	public EParameter getParamIfNotExists(EParameter param){
		Assert.isNotNull(param);
		for (EParameter par : loadedParameters)
			if (par.equals(param))
				return par;
		loadedParameters.add(param);
		return param;
	}
}
