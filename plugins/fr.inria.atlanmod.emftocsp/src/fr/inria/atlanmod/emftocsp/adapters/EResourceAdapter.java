package fr.inria.atlanmod.emftocsp.adapters;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

public abstract  class EResourceAdapter<R> implements Resource {
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((origResource == null) ? 0 : origResource.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof EResourceAdapter))
			return false;
		EResourceAdapter other = (EResourceAdapter) obj;
		if (origResource == null) {
			if (other.origResource != null)
				return false;
		} else if (!origResource.equals(other.origResource))
			return false;
		return true;
	}

	protected R origResource;
	public R getOriginalResource(){
		return origResource;
	}
	public EResourceAdapter(R newResource){
		origResource= newResource;
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
	public abstract ResourceSet getResourceSet();

	@Override
	public abstract URI getURI();

	@Override
	public void setURI(URI uri) {
		throw new UnsupportedOperationException(); 
	}

	@Override
	public long getTimeStamp() {
		throw new UnsupportedOperationException(); 
	}

	@Override
	public void setTimeStamp(long timeStamp) {
		throw new UnsupportedOperationException(); 
		
	}

	@Override
	public abstract EList<EObject> getContents();

	@Override
	public abstract TreeIterator<EObject> getAllContents() ;

	@Override
	public String getURIFragment(EObject eObject) {
		throw new UnsupportedOperationException(); 
	}

	@Override
	public EObject getEObject(String uriFragment) {
		throw new UnsupportedOperationException(); 
	}

	@Override
	public void save(Map<?, ?> options) throws IOException {
		throw new UnsupportedOperationException(); 	
	}

	@Override
	public void load(Map<?, ?> options) throws IOException {
		throw new UnsupportedOperationException(); 
		
	}

	@Override
	public void save(OutputStream outputStream, Map<?, ?> options)
			throws IOException {
		throw new UnsupportedOperationException(); 
		
	}

	@Override
	public void load(InputStream inputStream, Map<?, ?> options)
			throws IOException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isTrackingModification() {
		throw new UnsupportedOperationException(); 
	}

	@Override
	public void setTrackingModification(boolean isTrackingModification) {
		throw new UnsupportedOperationException(); 
		
	}

	@Override
	public boolean isModified() {
		throw new UnsupportedOperationException(); 
	}

	@Override
	public void setModified(boolean isModified) {
		throw new UnsupportedOperationException(); 
		
	}

	@Override
	public boolean isLoaded() {
		throw new UnsupportedOperationException(); 
	}

	@Override
	public void unload() {
		throw new UnsupportedOperationException(); 	
	}

	@Override
	public void delete(Map<?, ?> options) throws IOException {
		throw new UnsupportedOperationException(); 
		
	}

	@Override
	public EList<Diagnostic> getErrors() {
		throw new UnsupportedOperationException(); 
	}

	@Override
	public EList<Diagnostic> getWarnings() {
		throw new UnsupportedOperationException(); 
	}
	
	
	

}
