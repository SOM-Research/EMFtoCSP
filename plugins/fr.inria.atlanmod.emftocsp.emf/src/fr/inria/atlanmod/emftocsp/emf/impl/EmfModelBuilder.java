/**
 * 
 */
package fr.inria.atlanmod.emftocsp.emf.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import java.math.BigDecimal;
import java.math.BigInteger;


import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EFactory;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.XMLResource;
import org.eclipse.emf.ecore.xmi.impl.XMLResourceFactoryImpl;

import com.parctechnologies.eclipse.CompoundTerm;

import fr.inria.atlanmod.emftocsp.IModelReader;
import fr.inria.atlanmod.emftocsp.impl.ModelBuilder;
import fr.inria.atlanmod.emftocsp.modelbuilder.AssocStruct;
import fr.inria.atlanmod.emftocsp.modelbuilder.Field;
import fr.inria.atlanmod.emftocsp.modelbuilder.ObjectStruct;
import fr.inria.atlanmod.emftocsp.modelbuilder.Point;

/**
 * @author abenelal
 *
 */
public class EmfModelBuilder extends ModelBuilder<Resource, EPackage, EClass, EAssociation, EAttribute, EOperation,CompoundTerm> {

	/**
	 * 
	 */
	protected Map <Point,EObject> idToObj = new HashMap<Point,EObject>(); 
	protected EList<EObject> objList = new BasicEList<EObject>(); 
	public EmfModelBuilder() {
		
	}
	public EmfModelBuilder(IModelReader<Resource, EPackage, EClass, EAssociation, EAttribute, EOperation> modelReader, CompoundTerm ct){		
		solution = ct;
		this.modelReader = modelReader;
	}


	public EmfModelBuilder(
			IModelReader<Resource, EPackage, EClass, EAssociation, EAttribute, EOperation> modelReader) {
		this(modelReader,null);
	}

	@Override
	public void setSolution(CompoundTerm ct){
		solution = ct;
	}
	@Override
	public void generateInstance(String fileName) {
	
		if (solutionIsEmpty()) {
				System.out.println("EMPTY MODEL"); return; 
			}
		
		createEObjects();
		
		setupEReferences();
		
		Map<String, Boolean> options = new HashMap<String, Boolean>();
		
		options.put(XMLResource.OPTION_SCHEMA_LOCATION, Boolean.TRUE);
		options.put(XMLResource.OPTION_USE_ENCODED_ATTRIBUTE_STYLE, Boolean.TRUE);
		options.put(XMLResource.OPTION_KEEP_DEFAULT_CONTENT, true);
		
		serializeInstance(fileName,options);
	}

	private void serializeInstance(String fileName, Map<String, Boolean> options) {
		ResourceSet rscSet = new ResourceSetImpl();
		EPackage topPck= (EPackage) modelReader.getResource().getContents().get(0);
		
		rscSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put(
				"xmi", new  XMLResourceFactoryImpl());
		EPackage.Registry.INSTANCE.put(topPck.getNsURI(), topPck);
		fileName+=".xmi";
		Resource resource = rscSet.createResource(URI.createURI(fileName));
		Iterator<EObject> objIt = objList.iterator();
		while (objIt.hasNext()) {
			EObject obj = objIt.next();
			if (isRoot(obj))
				resource.getContents().add(obj);
		}
		try{			
			resource.save(options);
		}catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	@SuppressWarnings("unchecked")
	private void setupEReferences() {
		
		for (Iterator<AssocStruct> assocIt = assocStructures.iterator();assocIt.hasNext();){
			AssocStruct assStruct = assocIt.next();
			EReference ref = getEReferenceFromAssoc(assStruct.getName());
			EClass srcCls = ref.getEContainingClass();
			EClass trgCls = ref.getEReferenceType();
			int mostInnerSrcClsId = getMostConcreteClsId(srcCls,assStruct.getSrcOid());
			int mostInnerTrgClsId = getMostConcreteClsId(trgCls,assStruct.getTrgOid());
			EObject srcObj = idToObj.get(new Point(mostInnerSrcClsId ,assStruct.getSrcOid()));
			EObject trgObj = idToObj.get(new Point(mostInnerTrgClsId ,assStruct.getTrgOid()));
			if (ref.isMany())
				((EList<EObject>)srcObj.eGet(ref)).add(trgObj);
			else
				srcObj.eSet(ref,trgObj);

		}
		
	}
	private void createEObjects() {
		
		for (Iterator<ObjectStruct> objIt = objectStructures.iterator();objIt.hasNext();){
			ObjectStruct struct = objIt.next();
			List<EClass> subtypes = new ArrayList<EClass>();
			subtypes=modelReader.getClassSubtypes(modelReader.getClasses(),getEClassFromObj(struct.getName()));

			if ( subtypes!=null && existsOidInSubtypes(subtypes,struct.getOid())  )
				continue;
			if (!(getEClassFromObj(struct.getName()).getEAllSuperTypes().isEmpty()))
				reconstruct(struct);
			EClass associatedClass = getEClassFromObj(struct.getName());	
			EFactory factory = associatedClass.getEPackage().getEFactoryInstance();
			EObject obj = factory.create(associatedClass);
			int objOid = (Integer) struct.getOid();
			idToObj.put(new Point(associatedClass.getClassifierID(),objOid), obj);
			List<EAttribute> attList = associatedClass.getEAllAttributes();
			if (attList.size() != 0){
				for (int i=1; i<= attList.size();i++){
					Field field = struct.getFields().get(i);
					Object value = null;
					String stringType = attList.get(i-1).getEType().getName();
					System.out.println(stringType);				
					if (stringType.equals("EString") 
							|| attList.get(i-1).getEType().getName().contains("EChar"))
						value = " ";
					else if (stringType.contains("EBoolean"))
						if (((Integer)field.getValue())==0)
							value = false;
						else value =true;
					else if (stringType.contains("EByte")){
						int cast = (Integer)field.getValue();
							value = new Byte(String.valueOf(cast)) ;}
					else if (stringType.equals("EBigInteger"))
							value = new BigInteger( BigInteger.valueOf(Long.parseLong(String.valueOf(field.getValue()))).toByteArray());
					else if (attList.get(i-1).getEType().getName().equals("EBigDecimal"))
							value = new BigDecimal(String.valueOf(field.getValue()).toCharArray());
					else if (stringType.contains("ELong"))
						value = Long.parseLong(String.valueOf(field.getValue()));
					else value =  field.getValue();
										
					obj.eSet(attList.get(i-1), value);				
				}
			}
			objList.add(obj);
		}

		
	}
	@Override
	public boolean solutionIsEmpty() {
			return objectStructures.isEmpty();
	}
	private int getMostConcreteClsId(EClass cls,int srcOid) {
		List<EClass> subTypes = new ArrayList<EClass>();
		modelReader.getClassSubtypes(modelReader.getClasses(), cls, subTypes);
		subTypes.add(0, cls);
		Point point = null;
		for (EClass c : subTypes){
			point = new Point(c.getClassifierID(),srcOid);
			if (idToObj.containsKey(point))
				return idToObj.get(point).eClass().getClassifierID();}
		return -1;
	}
	protected void reconstruct(ObjectStruct struct) {
		List<Field> newFields = new ArrayList<Field>();
		newFields.add(struct.getFields().get(0));
		List<ObjectStruct> listStructs = sortedSuperStructs(struct);
			for (ObjectStruct objStr : listStructs){
				for (int i=1; i<objStr.getFields().size();i++)
					newFields.add(objStr.getFields().get(i));
			}			
		struct.getFields().clear();
		struct.getFields().addAll(newFields);
		
	}
	protected List<ObjectStruct> sortedSuperStructs(ObjectStruct struct) {
		List<ObjectStruct> listObj = new ArrayList<ObjectStruct>();
		listObj = getSuperStructures(struct);
		sortByMostAbstract(listObj);
		listObj.add(struct);
		return listObj;
	}
	private void sortByMostAbstract(List<ObjectStruct> listObj) {
		for (int i=0; i<listObj.size()-1;i++)
			for (int j= 1; j< listObj.size();j++){
				if (!abstractThan(listObj.get(i),listObj.get(j))){
					ObjectStruct tmp = listObj.get(i);
					listObj.set(i, listObj.get(j));
					listObj.set(j, tmp);				
							}
			}
		
		
	}
	protected boolean abstractThan(ObjectStruct objectStruct,
			ObjectStruct objectStruct2) {		
		return getSuperStructures(objectStruct2).contains(objectStruct);
	}
	protected List<ObjectStruct> getSuperStructures(ObjectStruct struct) {
		List<ObjectStruct> listObj = new ArrayList<ObjectStruct>();
		EClass cls =getEClassFromObj(struct.getName());
		EList<EClass> listCls= cls.getEAllSuperTypes();
		for (EClass c : listCls){
			String cName=c.getName().toLowerCase();
			for (ObjectStruct obj : objectStructures )
				if (obj.getName().equals(cName))
					listObj.add(obj);
		}
		
		return listObj;
	}
	protected boolean existsOidInSubtypes(List<EClass> subtypes, int oid) {
		if (subtypes==null)
			return false;
		for (EClass cls : subtypes){
			String strCls = cls.getName().toLowerCase();		
			for (ObjectStruct objStr : objectStructures )
				if (objStr.getName().equals(strCls) && objStr.getOid()==oid)
					return true;			
			}
				
		return false;
	}
	protected boolean isRoot(EObject obj) {
		return obj.eContainer()==null ? true : obj.eContainingFeature().getFeatureID()==-1;
	}
	protected EReference getEReferenceFromAssoc(String name) {
		for ( Iterator<EAssociation> it = modelReader.getAssociations().iterator(); it.hasNext();){
			EAssociation ass = it.next();
			if (ass.getName().toLowerCase().equals(name))
				return ass.getDestinationEnd();			
		}
		return null;
	}
	protected EClass getEClassFromObj(String str) {
		for (Iterator<EClass> it = modelReader.getClasses().iterator();it.hasNext();)
		{
			EClass cls = it.next();
			if (cls.getName().toLowerCase().equals(str))
				return cls;
		}
		return null;
	}
	@Override
	public void decorticateResult() {
		assocStructures = new ArrayList<AssocStruct>();
		objectStructures = new ArrayList<ObjectStruct>();
		CompoundTerm term = (CompoundTerm) solution.arg(1);
		if (!(term.arg(1) == Collections.EMPTY_LIST  ) ){
		@SuppressWarnings("unchecked")
		List<LinkedList<CompoundTerm>> rangesList=(List<LinkedList<CompoundTerm>>)term.arg(1);
			for (Iterator<LinkedList<CompoundTerm>> iter=rangesList.iterator(); iter.hasNext();)
			{
				Object intermediate = iter.next() ;
				if (intermediate != Collections.EMPTY_LIST  ){
				@SuppressWarnings("unchecked")
				LinkedList<CompoundTerm> cpList = (LinkedList<CompoundTerm>) intermediate;
					for (Iterator<CompoundTerm> iterL2 = cpList.iterator(); iterL2.hasNext();)
					{
						CompoundTerm cp = iterL2.next();
						Boolean  bol = getEClassFromObj(cp.functor())==null;
						if (bol)  assocStructures.add(new AssocStruct (cp,cp.functor()));
						else objectStructures.add(new ObjectStruct(cp,cp.functor()));
					}
				}
			}	
		}
	}
}
