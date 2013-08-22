/*******************************************************************************
 * Copyright (c) 2011 INRIA Rennes Bretagne-Atlantique.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     INRIA Rennes Bretagne-Atlantique - initial API and implementation
 *******************************************************************************/
package fr.inria.atlanmod.emftocsp.emf.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;

import fr.inria.atlanmod.emftocsp.IModelReader;

/**
 * @author <a href="mailto:carlos.gonzalez@inria.fr">Carlos A. González</a>
 *
 */
public class EmfModelReader implements IModelReader<Resource, EPackage, EClass, EAssociation, EAttribute, EOperation> {
  Resource r;
  public EmfModelReader(Resource r) {
    this.r = r;
  }
  
  @Override
  public Resource getModelResource() {
    return r;
  }
  @Override
  public Resource getResource() {
	return r;
}
  @Override
  public List<EPackage> getPackages() {
          ArrayList<EPackage> pList = new ArrayList<EPackage>();
          if (r.getContents() != null)
                  for (EObject obj : r.getContents())
                          if (obj instanceof EPackage) {
                                  EPackage pack = (EPackage) obj;
                                  retrieveSubPackages(pack, pList);
                                  pList.add((EPackage) obj);
                                  EPackage.Registry.INSTANCE.put(((EPackage) obj).getNsURI(),
                                                  (EPackage) obj);
                          }
          return pList;
  }

  private void retrieveSubPackages(EPackage parentPackage,
                  List<EPackage> allPackages) {
          for (EPackage subPackage : parentPackage.getESubpackages()) {
                  allPackages.add(subPackage);
                  EPackage.Registry.INSTANCE.put(subPackage.getNsURI(), subPackage);
                  retrieveSubPackages(subPackage, allPackages);
          }
  }
	private List<EClass> getClassesFromPackage(EPackage p) {
	  ArrayList<EClass> cList = new ArrayList<EClass>();
	  if (p.getEClassifiers() != null)
	    for (EClassifier c : p.getEClassifiers())
	      if (c instanceof EClass) 
	        cList.add((EClass)c);
	  return cList;
	}

	@Override
	public List<EClass> getClasses() {
	  List<EPackage> pList = getPackages();
	  ArrayList<EClass> cList = new ArrayList<EClass>();          
	  for(EPackage p : pList) 
	    cList.addAll(getClassesFromPackage(p));  
	  return cList;
	}

	
	@Override
	public List<String> getClassesNames() {
    List<EClass> cList = getClasses();
    ArrayList<String> names = new ArrayList<String>();
    for(EClass c : cList)
      names.add(c.getName());   
    return names;
	}

  @Override
  public List<EAttribute> getClassAttributes(EClass c) {
//    ArrayList<EAttribute> atList = new ArrayList<EAttribute>();
//    if (c.getEAttributes() != null)
//      for (EAttribute at : c.getEAttributes())
//        atList.add(at);
//    return atList;
	  return c.getEAttributes();
  }

  @Override
  public List<EOperation> getClassOperations(EClass c) {
    ArrayList<EOperation> opList = new ArrayList<EOperation>();
    if (c.getEOperations() != null)
      for (EOperation op : c.getEOperations())
        opList.add(op);
    return opList; 
  }
  
  @Override
  public List<EClass> getClassSubtypes(List<EClass> classList, EClass c  ) {
    ArrayList<EClass> subTypesList = new ArrayList<EClass>();  
    if (classList != null) 
      for (EClass cl : classList) 
        for (EClass superType : cl.getESuperTypes()) 
          if (c.equals(superType))
            subTypesList.add(cl);
    return subTypesList.size() > 0 ? subTypesList : null;
  }
  public void getClassSubtypes(List<EClass> classList , EClass c , List<EClass> nestedSubtypes){
	  if (classList != null) 
	      for (EClass cl : classList) 
	        for (EClass superType : cl.getESuperTypes()) 
	          if (c == superType){
	        	  nestedSubtypes.add(cl);
	        	  getClassSubtypes(classList , cl ,  nestedSubtypes);}
	   
  }
  
  @Override
  public EClass getBaseClass(EClass c) {
    if (c.getESuperTypes() == null || (c.getESuperTypes() != null && c.getESuperTypes().size() == 0))
      return c;
    if (c.getESuperTypes().size() > 1)
      return null;
    return getBaseClass(c.getESuperTypes().get(0));
  }  
  
	@Override
	public List<EAssociation> getAssociations() {
		
    HashMap<String, Boolean> procAs = new HashMap<String, Boolean>();
    
    ArrayList<EAssociation> asList = new ArrayList<EAssociation>();
    
    List<EClass> cList = getClasses();
    for (EClass c : cList) 
      if (c.getEReferences() != null) 
        for (EReference ref : c.getEReferences()) {
        	if (ref.isContainer()) continue;
          String dstRoleName = ref.getName();
          String srcRoleName = ref.getEOpposite() != null ? ref.getEOpposite().getName() : c.getName();
          String asName = srcRoleName.compareToIgnoreCase(dstRoleName) < 0 ? srcRoleName + "_" + dstRoleName : dstRoleName + "_" + srcRoleName; //$NON-NLS-1$ //$NON-NLS-2$
          
          if (procAs.get(asName) == null || (procAs.get(asName) != null && !procAs.get(asName))) {
            procAs.put(asName, true);
            EAssociation as = new EAssociation(asName, c, ref);
            asList.add(as);
          }
        }
    return asList;
	}
  
	@Override
	public List<String> getAssociationsNames() {
	  List<EAssociation> asList = getAssociations();
    ArrayList<String> names = new ArrayList<String>();
    for(EAssociation as : asList)
      names.add(as.getName());   
    return names;
	}
	@Override	
	public List<String> getAssociationNamesOfNonAbsClasses(){
		List<EAssociation> asList = getAssociations();
	    ArrayList<String> names = new ArrayList<String>();
	    for(EAssociation as : asList)
	    	if(!assWithAbsEnd(as))
	    		names.add(as.getName());
	    return names;
	}
	private boolean assWithAbsEnd(EAssociation as){
		
		EClass srcCls = as.getSourceEnd();
		EClass trgCls = as.getDestinationEnd().getEReferenceType();
		return (srcCls.isAbstract() && getClassSubtypes(getClasses(), srcCls)==null) || 
				(trgCls.isAbstract() && getClassSubtypes(getClasses(), trgCls)==null);
	}
  @Override
  public String getAssociationName(EAssociation as) {
    return as.getName();
  }

  @Override
  public String getAssociationEndName(EAttribute asEnd) {
    return asEnd.getName();
  }
}