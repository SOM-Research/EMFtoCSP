package fr.inria.atlanmod.emftocsp.emf.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.Assert;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.UMLFactory;
import org.eclipse.uml2.uml.UMLPackage;
import org.eclipse.uml2.uml.resource.UMLResource;
import org.eclipse.uml2.uml.resources.util.UMLResourcesUtil;

import com.parctechnologies.eclipse.CompoundTerm;

import fr.inria.atlanmod.emftocsp.IModelReader;
import fr.inria.atlanmod.emftocsp.adapters.umlImpl.EAttributeUMLAdapter;
import fr.inria.atlanmod.emftocsp.adapters.umlImpl.EClassUMLAdapter;
import fr.inria.atlanmod.emftocsp.adapters.umlImpl.EReferenceUMLAdapter;
import fr.inria.atlanmod.emftocsp.modelbuilder.AssocStruct;
import fr.inria.atlanmod.emftocsp.modelbuilder.Field;
import fr.inria.atlanmod.emftocsp.modelbuilder.ObjectStruct;
import fr.inria.atlanmod.emftocsp.modelbuilder.Point;

public class UmlModelBuilder extends EmfModelBuilder{


	/**
	 * 
	 */
	public UmlModelBuilder() {
		
	}
	public UmlModelBuilder(IModelReader<Resource, EPackage, EClass, EAssociation, EAttribute, EOperation> modelReader, CompoundTerm ct){		
		solution = ct;
		this.modelReader = modelReader;
	}


	public UmlModelBuilder(
			IModelReader<Resource, EPackage, EClass, EAssociation, EAttribute, EOperation> modelReader) {
		this(modelReader,null);
	}
	

	
	@Override
	public void generateInstance(String path) {

		 
		if (solutionIsEmpty()){
			System.out.println("EMPTY MODEL"); return; 
		}
		
		
		createObjects();
		createLinks();
		Map<String, Boolean> options = new HashMap<String, Boolean>();
		
		serializeInstance(path,options);
	}

	@SuppressWarnings("unchecked")
	private void serializeInstance(String fileName, Map<String, Boolean> options) {
		ResourceSet rscSet = new ResourceSetImpl();
		UMLResourcesUtil.init(rscSet);
		fileName+=".uml";
		Resource resource = rscSet.createResource(URI.createURI(fileName).appendFileExtension(UMLResource.FILE_EXTENSION));
		Iterator<EObject> objIt = objList.iterator();
		EObject model = UMLFactory.eINSTANCE.createModel();
		resource.getContents().add(model);
		while (objIt.hasNext()) {
			EObject obj = objIt.next();
			if (isRoot(obj))
			resource.getContents().add(obj);
			((List<EObject>) model.eGet(UMLPackage.eINSTANCE.getPackage_PackagedElement())).add(obj);				
		}
		try{			
			resource.save(options);
		}catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	@SuppressWarnings("unchecked")
	private void createObjects(){
		for (Iterator<ObjectStruct> objIt = objectStructures.iterator();objIt.hasNext();){
		ObjectStruct struct = objIt.next();
		List<EClass> subtypes = new ArrayList<EClass>();
		subtypes=modelReader.getClassSubtypes(modelReader.getClasses(),getEClassFromObj(struct.getName()));

		if ( subtypes!=null && existsOidInSubtypes(subtypes,struct.getOid())  )
			continue;
		if (!(getEClassFromObj(struct.getName()).getEAllSuperTypes().isEmpty()))
			reconstruct(struct);
		EClassUMLAdapter associatedClass = ((EClassUMLAdapter) getEClassFromObj(struct.getName()));	
		EObject instanceSpecification = UMLFactory.eINSTANCE.createInstanceSpecification();
		setUpUMLInstance(instanceSpecification, associatedClass.getOriginalClass());
		int objOid = (Integer) struct.getOid();
		idToObj.put(new Point(associatedClass.hashCode(),objOid), instanceSpecification);
		List<EAttribute> attList = associatedClass.getEAllAttributes();
		if (attList.size() != 0){
			for (int i=1; i<= attList.size();i++){
				Field field = struct.getFields().get(i);
				String stringType = attList.get(i-1).getEType().getName();
				((List<EObject>) instanceSpecification.eGet(UMLPackage.eINSTANCE.getInstanceSpecification_Slot())).add(createSlot(attList.get(i-1), stringType,field));			
			}
	}
		objList.add(instanceSpecification);
		
		
		}
	}
	
	@SuppressWarnings("unchecked")
	private void createLinks(){
		for (Iterator<AssocStruct> assocIt = assocStructures.iterator();assocIt.hasNext();){
			AssocStruct assStruct = assocIt.next();
			EReferenceUMLAdapter ref = (EReferenceUMLAdapter) getEReferenceFromAssoc(assStruct.getName());
			Association assoc = ref.getOriginalReference().getAssociation();
			
		
			Property srcEnd = null;
			Property trgEnd = null;
			
			if (assoc.getOwnedEnds().get(0).getName().equalsIgnoreCase(ref.getName())){
				srcEnd = assoc.getOwnedEnds().get(0);
				trgEnd =  assoc.getOwnedEnds().get(1);
			}else { 
				srcEnd = assoc.getOwnedEnds().get(1);
				trgEnd =  assoc.getOwnedEnds().get(0);
			}
			
				
			EClass srcCls = ref.getEContainingClass();
			EClass trgCls = ref.getEReferenceType();
			
			int mostInnerSrcClsId = getMostConcreteClsId(srcCls,assStruct.getSrcOid());
			int mostInnerTrgClsId = getMostConcreteClsId(trgCls,assStruct.getSrcOid());
			
			EObject srcObj = idToObj.get(new Point(mostInnerSrcClsId ,assStruct.getSrcOid()));
			EObject trgObj = idToObj.get(new Point(mostInnerTrgClsId ,assStruct.getTrgOid()));
			
			EObject instanceSpecification = UMLFactory.eINSTANCE.createInstanceSpecification(); 
			((List<EObject>) instanceSpecification.eGet(UMLPackage.eINSTANCE.getInstanceSpecification_Classifier())).add(assoc);
			
			EObject srcSlot = UMLFactory.eINSTANCE.createSlot();
			srcSlot.eSet(UMLPackage.eINSTANCE.getSlot_DefiningFeature(), srcEnd);
			EObject srcInstance = UMLFactory.eINSTANCE.createInstanceValue();
			srcInstance.eSet(UMLPackage.eINSTANCE.getInstanceValue_Instance(),srcObj);
			((List<EObject>) srcSlot.eGet(UMLPackage.eINSTANCE.getSlot_Value())).add(srcInstance);
			
			EObject trgSlot = UMLFactory.eINSTANCE.createSlot();
			trgSlot.eSet(UMLPackage.eINSTANCE.getSlot_DefiningFeature(), trgEnd);
			EObject trgInstance = UMLFactory.eINSTANCE.createInstanceValue();
			trgInstance.eSet(UMLPackage.eINSTANCE.getInstanceValue_Instance(),trgObj);
			((List<EObject>) trgSlot.eGet(UMLPackage.eINSTANCE.getSlot_Value())).add(trgInstance);
			
			
			((List<EObject>) instanceSpecification.eGet(UMLPackage.eINSTANCE.getInstanceSpecification_Slot())).add(trgSlot);
			((List<EObject>) instanceSpecification.eGet(UMLPackage.eINSTANCE.getInstanceSpecification_Slot())).add(srcSlot);

			objList.add(instanceSpecification);

		}
	}
	@SuppressWarnings("unchecked")
	private EObject createSlot(EAttribute eAttribute, String stringType, Field field) {
		Assert.isTrue(eAttribute instanceof EAttributeUMLAdapter, eAttribute+ "is not instanceof EAttributeUMLAdapter");
		EAttributeUMLAdapter attribute = (EAttributeUMLAdapter) eAttribute;
		EObject slot = UMLFactory.eINSTANCE.createSlot();
		slot.eSet(UMLPackage.eINSTANCE.getSlot_DefiningFeature(), attribute.getOriginalAttribute());
		EObject value = null;
		if (stringType.contains("int") || stringType.contains("Int")){
			value = UMLFactory.eINSTANCE.createLiteralInteger();
			value.eSet(UMLPackage.eINSTANCE.getLiteralInteger_Value(),field.getValue());}
		else if (stringType.contains("double") || stringType.contains("Double")
				   			||
					stringType.contains("Float") || stringType.contains("float")){
			value = UMLFactory.eINSTANCE.createLiteralReal();
			value.eSet(UMLPackage.eINSTANCE.getLiteralReal_Value(),Double.valueOf((Integer)field.getValue()));}
		else if (stringType.contains("long") || stringType.contains("Long")){
			value = UMLFactory.eINSTANCE.createLiteralUnlimitedNatural();
			value.eSet(UMLPackage.eINSTANCE.getLiteralUnlimitedNatural_Value(),field.getValue());}
		else if (stringType.contains("string") || stringType.contains("String")){
			value = UMLFactory.eINSTANCE.createLiteralString();
			value.eSet(UMLPackage.eINSTANCE.getLiteralString_Value()," ");}
		else if (stringType.contains("boolean") || stringType.contains("Boolean")){
			boolean bol = true;
			if (((Integer)field.getValue())==0)
				bol = false;
			value = UMLFactory.eINSTANCE.createLiteralBoolean();
			value.eSet(UMLPackage.eINSTANCE.getLiteralBoolean_Value(),bol);}
		((List<EObject>) slot.eGet(UMLPackage.eINSTANCE.getSlot_Value())).add(value);
		return slot;
	}
	@SuppressWarnings("unchecked")
	private void setUpUMLInstance(EObject instanceSpecification,
			Class originalClass) {
		((List<EObject>) instanceSpecification.eGet(UMLPackage.eINSTANCE.getInstanceSpecification_Classifier())).add(originalClass);	
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
			point = new Point(c.hashCode(),srcOid);
			if (idToObj.containsKey(point))
				return c.hashCode();}
		return -1;
	}
	

}
