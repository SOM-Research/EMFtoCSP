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
package fr.inria.atlanmod.emftocsp.emftoecl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.resource.Resource;

import fr.inria.atlanmod.emftocsp.ILogger;
import fr.inria.atlanmod.emftocsp.IModelProperty;
import fr.inria.atlanmod.emftocsp.IModelReader;
import fr.inria.atlanmod.emftocsp.ProcessingException;
import fr.inria.atlanmod.emftocsp.emf.impl.EAssociation;
import fr.inria.atlanmod.emftocsp.impl.LackOfConstraintsRedundanciesModelProperty;
import fr.inria.atlanmod.emftocsp.impl.LackOfConstraintsSubsumptionsModelProperty;
import fr.inria.atlanmod.emftocsp.impl.LivelinessModelProperty;
import fr.inria.atlanmod.emftocsp.impl.StrongSatisfiabilityModelProperty;
import fr.inria.atlanmod.emftocsp.impl.WeakSatisfiabilityModelProperty;

/**
 * @author <a href="mailto:carlos.gonzalez@inria.fr">Carlos A. González</a>
 *
 */
public class ModelToEcl {
  IModelReader<Resource, EPackage, EClass, EAssociation, EAttribute, EOperation> emfModelReader;
  List<EPackage> pList; 
  List<EClass> cList; 
  List<String> cListNames; 
  List<EAssociation> asList;
  List<String> asListNames;
  List<String> constraintsNames;
  Map<String, String> elementsDomain;
  List<IModelProperty> properties;
  ILogger logger;
  
  public ModelToEcl(IModelReader<Resource, EPackage, EClass, EAssociation, EAttribute, EOperation> emfModelReader, Map<String, String> elementsDomain, List<IModelProperty> properties, List<String> constraintsNames, ILogger logger) {
    this.emfModelReader = emfModelReader;
    cList = emfModelReader.getClasses();
    pList = emfModelReader.getPackages();
    asList = emfModelReader.getAssociations();
    cListNames = emfModelReader.getClassesNames();
    asListNames = emfModelReader.getAssociationsNames();
    this.properties = properties;
    this.elementsDomain = elementsDomain;
    this.constraintsNames = constraintsNames;
    this.logger = logger;
  }

  protected String genLibsSection() {
    return ":-lib(ic).\n:-lib(ic_global).\n:-lib(ic_global_gac).\n:-lib(apply).\n:-lib(apply_macros).\n:-lib(lists).\n:-lib(ech).";
  }
  
  protected String genStructSection() {
    StringBuilder s = new StringBuilder();
    List<EAttribute> atList = new ArrayList<EAttribute>();
    for (EClass c : cList) {
      s.append(":- local struct(");
      s.append(c.getName().toLowerCase());
      s.append("(oid");
      atList = emfModelReader.getClassAttributes(c);
      for (EAttribute at : atList) { 
        s.append(",");
        s.append(at.getName());
      }
      s.append(")).\n");
    }    
    for (EAssociation as : asList) {
      s.append(":- local struct(");
      s.append(emfModelReader.getAssociationName(as).toLowerCase());
      s.append("(");
      s.append(as.getSourceRoleName().toLowerCase());
      s.append(",");
      s.append(as.getDestinationRoleName().toLowerCase());
      s.append(")).\n");              
    }
    return s.toString();
  }
  
  protected String genHeaderSection() {
    return "findSolutions(Instances):-\n";   
  }  
  
  protected String genCardinalityDefinitionsSection() {  
    StringBuilder s = new StringBuilder();
    String nameList = "";
    String nameList2 = "";
    s.append("\t%Cardinality definitions\n\t");
    for (EClass c : cList) {
      s.append("S");
      s.append(c.getName());
      nameList += "S" + c.getName() + ", ";
      if (!isAbsWithNoChilds(c))
      nameList2 += "S" + c.getName() + ", ";
      s.append("::");
      s.append(elementsDomain.get(c.getEPackage().getName() + "." + c.getName()));
      s.append(", ");
    }
    s.append("\n\t");
    
    for (String asName : asListNames) {
      s.append("S");
      s.append(asName.toLowerCase());
      nameList += "S" + asName.toLowerCase() + ", ";
      s.append("::");
      s.append(elementsDomain.get(asName));
      s.append(", ");
    }  
    for (String name : emfModelReader.getAssociationNamesOfNonAbsClasses())
    	nameList2 += "S" + name.toLowerCase() + ", ";
    s.append("\n\t");
    s.append("CardVariables=[");
    s.append(nameList.substring(0, nameList.length() - 2));
    s.append("],\n\t");
    
    s.append("\n\t");
    s.append("CardNonAbsVariables=[");
    s.append(nameList2.substring(0, nameList2.length() - 2));
    s.append("],\n\t");
    return s.toString();
  }
  
  
  private boolean isAbsWithNoChilds(EClass c){
	  return c.isAbstract() && emfModelReader.getClassSubtypes(cList, c)== null;
	  
  }
  protected String genCardinalityConstraintsSection() {  
    StringBuilder s = new StringBuilder();
    s.append("\t%Cardinality constraints\n\t");
    
    s.append("% cardinality constraints derived from containment tree (compositions)\n");
    for (EClass c : cList) {
    	boolean complete = true;
    	List<String> cardVars = getContainments(c,"S");
//    	List<String> cardVars = new ArrayList<String>();
//		for (EReference ref : c.getEReferences()) {
//			if (ref.isContainer()) {
//				EAssociation assoc = getAssociation(ref);
//				cardVars.add("S" + assoc.getName().toLowerCase());
//				if(ref.getLowerBound() != 1) {
//					complete = false;
//				}
//			}
//		}
//	
		if (! cardVars.isEmpty() ) {
			s.append("\tS" + c.getName());
			if (complete) {
				s.append(" #= ");
			} else {
				s.append(" #>= ");
			}
			for (Iterator<String> itCardVar = cardVars.iterator(); itCardVar.hasNext();) {
				s.append(itCardVar.next());
				if (itCardVar.hasNext()) {
					s.append(" + ");
				}
				else {
					s.append(",\n");
				}
			}
		}
	}

    for(IModelProperty prop : properties) {
      if (prop instanceof StrongSatisfiabilityModelProperty)
        s.append("strongSatisfiability(CardNonAbsVariables),");
      if (prop instanceof WeakSatisfiabilityModelProperty)
        s.append("\n\tweakSatisfiability(CardNonAbsVariables),");
      if (prop instanceof LivelinessModelProperty) {
        List<String> liveliness = prop.getTargetModelElementsNames();
        for (String cName : liveliness) {
          s.append("\n\tliveliness(CardVariables, \"");  
          s.append(cName);  
          s.append("\"");
          s.append("),");  
        }
      }
    }    
    s.append("\n\t");
    for (EClass c : cList) {      
      List<EClass> subTypes = emfModelReader.getClassSubtypes(cList, c);
      StringBuilder subTypeNames = new StringBuilder();
      if (subTypes != null) {
        for(EClass subType : subTypes) { 
          subTypeNames.append(",");
          subTypeNames.append(subType.getName());
        }
        s.append("constraintsGen");
        s.append(c.getName());
        s.append(subTypeNames.toString().replace(",",""));  
        s.append("(S");
        s.append(c.getName());
        s.append(subTypeNames.toString().replace(",",", S"));  
        s.append("),\n\t");
      }
    }
    s.append("\n\t");
    for (String asName : asListNames) {
      s.append("constraints");
      s.append(asName.toLowerCase());
      s.append("Card(CardVariables),\n\t");
    }      
    return s.toString();    
  }
  
  private List<String> getContainments(EClass c, String s) {
	List<String> cardVars= new ArrayList<String>();
	for (EAssociation as : asList) {
		if ( (as.getDestinationEnd().isContainment() && as.getDestinationEnd().getEReferenceType().equals(c)) ||
				(as.getDestinationEnd().isContainer() && as.getDestinationEnd().getEOpposite().getEReferenceType().equals(c)))
			cardVars.add( s + as.getName().toLowerCase());
	}

	return cardVars;
}

/**
   * Returns the association that includes this reference.
   */
  @SuppressWarnings("unused")
private EAssociation getAssociation(EReference ref) {
	  for (EAssociation as : asList) {
		  EReference asRef1 = as.getDestinationEnd();
		  EReference asRef2 = as.getDestinationEnd().getEOpposite();
		  if (ref.equals(asRef1)) {
			  return as;
		  }
		  if (ref.equals(asRef2)) {
			  return as;
		  }
	  }
	  throw new RuntimeException("Internal error (this should never happen): Could not find association for " + ref);
}

protected String genCardinalityInstantiationSection() { 
    StringBuilder s = new StringBuilder();
    s.append("\t%Instantiation of cardinality variables\n\t");    
    s.append("labeling(CardVariables),\n\t");
    return s.toString();
  }
   
  protected String genObjectsCreationSection() {
    StringBuilder s = new StringBuilder();
    s.append("\t%Object creation\n\t");    

    for (EClass c : cList) {
      s.append("creation");
      s.append(c.getName());
      s.append("(O");
      s.append(c.getName());
      s.append(", S");
      s.append(c.getName());
      s.append(", S");
      s.append(emfModelReader.getBaseClass(c).getName());
      s.append(", At");
      s.append(c.getName());
      s.append("),\n\t");
    }
    s.append("\n\t");    

    for (String cName : cListNames) {
      s.append("\n\tdifferentOids");
      s.append("(O");
      s.append(cName);
      s.append("),");
    }
    s.append("\n\t");    
    
    for (String cName : cListNames) {
      s.append("\n\torderedInstances");
      s.append("(O");
      s.append(cName);
      s.append("),");
    }
    s.append("\n\t");     
    for (EClass c : cList) {
    	List<EClass> subTypes = emfModelReader.getClassSubtypes(cList, c);
      String b= "";
      if (subTypes!=null) 
    	
        for(EClass subType : subTypes) { 
          s.append("existingOids");
          s.append(subType.getName());
          s.append("In");  
          s.append(c.getName());
          s.append("(O");
          s.append(subType.getName());
          s.append(", O");
          s.append(c.getName());
          s.append("),\n\t");
          b+=("O"+subType.getName()+", ");
        }
      if (c.isAbstract()){
    	  s.append("existsOidInOneOf(O");
    	  s.append(c.getName());
    	  s.append(", [");
    	  if (b.length() >= 2)
    	  s.append(b.substring(0, b.length()-2));
    	  s.append("]),\n\t");
      }
    	  
    }    
    for (EClass c : cList) {
      List<EClass> subTypes = emfModelReader.getClassSubtypes(cList, c);
      StringBuilder subTypeNames = new StringBuilder();
      if (subTypes != null && subTypes.size() > 0) {
        for(EClass subType : subTypes) { 
          subTypeNames.append(", O");
          subTypeNames.append(subType.getName());
        }
        s.append("disjointInstances");
        s.append(subTypeNames.toString().replace(", O", ""));
        s.append("(");
        s.append(subTypeNames.toString().substring(2));
        s.append("),\n\t");
      }
    }    
    return s.toString();
  }
  
  protected String genLinksCreationSection() {
    StringBuilder s = new StringBuilder();
    s.append("\t%Links creation\n\t");    
   
    for (EAssociation as : asList) {
      String asName = emfModelReader.getAssociationName(as).toLowerCase();
      s.append("creation");
      s.append(asName);
      s.append("(L");
      s.append(asName);
      s.append(", S");
      s.append(asName);
      s.append(", P");
      s.append(asName);
      s.append(", S");
      s.append(emfModelReader.getBaseClass(as.getSourceEnd()).getName()); 
      s.append(", S");
      s.append(emfModelReader.getBaseClass((EClass)as.getDestinationEnd().getEType()).getName());                                                 
      s.append("),\n\t");
    }    
    for (String asName : asListNames) {
      s.append("differentLinks");
      s.append("(L");
      s.append(asName.toLowerCase());
      s.append("),\n\t");
    } 
    for (String asName : asListNames) {
      s.append("orderedLinks");
      s.append("(Instances,\"" + asName.toLowerCase() + "\"");
      s.append("),\n\t");
    }     
    
    for (EClass c : cList){
    	List <String> contList = getContainments(c,"L");
    	if (contList.size() > 1)
    	{	String st="noSharing([";
    		for (String str : contList){
    			st+=(str+", ");
    		}
    		st=st.substring(0,st.length()-2);
    		st+="]),\n\t";
    		s.append(st);}
    }
    return s.toString(); 
  }
  
  protected String genInstancesSection1() {

	  StringBuilder s = new StringBuilder();

	  s.append("\tInstances = [");
	  for (String cName : cListNames) {
		  s.append("O");
		  s.append(cName);
		  s.append(", ");
	  }    
	  for (String asName : asListNames) {
		  s.append("L");
		  s.append(asName.toLowerCase());
		  s.append(", ");
	  } 
	  s.deleteCharAt(s.length() - 2);
	  s.append("],\n\t");
	  return s.toString();
  }
  
  protected String genInstancesSection2() {
    StringBuilder s = new StringBuilder();

    s.append("\t");
    
    for(IModelProperty prop : properties) {
      if (prop instanceof LackOfConstraintsSubsumptionsModelProperty)
        for (String constraintNames : prop.getTargetModelElementsNames()) {
          s.append("noSubsumption");
          s.append(constraintNames.replace(",", ""));
          s.append("(Instances),\n\t");
        }
      if (prop instanceof LackOfConstraintsRedundanciesModelProperty)
        for (String constraintNames : prop.getTargetModelElementsNames()) {
          s.append("noRedundancy");
          s.append(constraintNames.replace(",", ""));
          s.append("(Instances),\n\t");
        }
    }    
    for (String asName : asListNames) {
      s.append("cardinalityLinks");
      s.append(asName.toLowerCase());
      s.append("(Instances),\n\t");
    } 
    return s.toString(); 
  }

  protected String genOclRootSection() {
    StringBuilder s = new StringBuilder();
       
    LackOfConstraintsSubsumptionsModelProperty cSub = null;
    LackOfConstraintsRedundanciesModelProperty cRed = null;
    for(IModelProperty prop : properties) {
      if (prop instanceof LackOfConstraintsSubsumptionsModelProperty)
        cSub = (LackOfConstraintsSubsumptionsModelProperty) prop;
      if (prop instanceof LackOfConstraintsRedundanciesModelProperty)
        cRed = (LackOfConstraintsRedundanciesModelProperty) prop;
    }   
    if (cSub == null && cRed == null)  
      for(String cName : constraintsNames) {
        String firstChar = cName.substring(0, 1); 
        String firstCharLower = firstChar.toLowerCase();
        s.append(cName.replaceFirst(firstChar, firstCharLower));
        s.append("(Instances),\n\t");
      }    
    return s.toString();
  }
  

  protected String genAttributeLabelingSection() {
	    StringBuilder s = new StringBuilder();
	    s.append("\tAllAttributes = [");
	    for (String asName : asListNames) {
	        s.append("P");
	        s.append(asName.toLowerCase());
	        s.append(", ");
	      } 
	    for (String cName : cListNames) {
	      s.append("At");
	      s.append(cName);
	      s.append(", ");
	    }    
	    s.deleteCharAt(s.length() - 2);
	    s.append("],\n\t");
	    s.append("flatten(AllAttributes, Attributes),\n\t");
        //s.append("search(Attributes,0,first_fail, indomain, complete, []),\n");
        s.append("labeling(Attributes),\n\t");


	    return s.toString();    
	  }

  protected String genStringLabelingSection() {
	    StringBuilder s = new StringBuilder();
	    s.append("\t");
		s.append("str_labeling.");
	    return s.toString();    
	  }

  protected String genGeneralizationSection() {
    StringBuilder s = new StringBuilder();
    
    for (EClass c : cList) {
      List<EClass> subTypes = emfModelReader.getClassSubtypes(cList, c);
      if (subTypes != null && subTypes.size() > 0) {
        s.append("\tconstraintsGen");
        s.append(c.getName());
        for(EClass subType : subTypes) 
          s.append(subType.getName());        
        s.append("(S");
        s.append(c.getName());
        for(EClass subType : subTypes) {
          s.append(", S");
          s.append(subType.getName());        
        }
        s.append("):-\n\t");
        s.append(c.isAbstract() ? "constraintsAbstractDisjointSubtypesCard(S" : "constraintsDisjointSubtypesCard(S");
        s.append(c.getName());
        s.append(", [");
        for(EClass subType : subTypes) {
          s.append("S");
          s.append(subType.getName());        
          s.append(",");
        }        
        s.deleteCharAt(s.length() - 1);
        s.append("]).\n");
      }
    }   
    return s.toString();
  }
  
  protected String genIndexesSection() {
    StringBuilder s = new StringBuilder();
    int i = 1;
    
    for (String cName : cListNames) {
      s.append("index(\"");
      s.append(cName);
      s.append("\",");
      s.append(i++);
      s.append(").\n");
    }    
    for (String asName : asListNames) {
      s.append("index(\"");
      s.append(asName.toLowerCase());
      s.append("\",");
      s.append(i++);
      s.append(").\n");
    } 
    List<EAttribute> atList = new ArrayList<EAttribute>();
    for (EClass c : cList) {
      i = 1;
      atList = emfModelReader.getClassAttributes(c);
      for (EAttribute at : atList) { 
        s.append("attIndex(\"");
        s.append(c.getName());
        s.append("\",\"");
        s.append(at.getName());
        s.append("\",");
        s.append(++i);
        s.append(").\n");
      }
    }    
    for (EClass c : cList) {
      i = 1;
      atList = emfModelReader.getClassAttributes(c);
      for (EAttribute at : atList) { 
        s.append("attType(\"");
        s.append(c.getName());
        s.append("\",\"");
        s.append(at.getName());
        s.append("\",\"");
        s.append(at.getEAttributeType().getName());
        s.append("\").\n");
        ++i;
      }
    }    
    return s.toString();
  }
  
  protected String genAssociationRolesSection() {
    StringBuilder s = new StringBuilder();

    for (EAssociation as : asList) {
      String asName = emfModelReader.getAssociationName(as).toLowerCase();
      s.append("roleIndex(\"");
      s.append(asName);
      s.append("\",\"");
      s.append(as.getSourceRoleName().toLowerCase());
      s.append("\",1).\n");
      s.append("roleIndex(\"");
      s.append(asName);
      s.append("\",\"");
      s.append(as.getDestinationRoleName().toLowerCase());
      s.append("\",2).\n");
    }     

    for (EAssociation as : asList) {
      String asName = emfModelReader.getAssociationName(as).toLowerCase();
      s.append("roleType(\"");
      s.append(asName);
      s.append("\",\"");
      s.append(as.getSourceRoleName().toLowerCase());
      s.append("\",\"");
      s.append(as.getSourceEnd().getName());
      s.append("\").\n");
      s.append("roleType(\"");
      s.append(asName);
      s.append("\",\"");
      s.append(as.getDestinationRoleName().toLowerCase());
      s.append("\",\"");
      s.append(as.getDestinationEnd().getEType().getName());
      s.append("\").\n");
    }     
    for (EAssociation as : asList) {
      String asName = emfModelReader.getAssociationName(as).toLowerCase();
      s.append("roleMin(\"");
      s.append(asName);
      s.append("\",\"");
      s.append(as.getSourceRoleName().toLowerCase());
      s.append("\",");
      s.append(as.getSourceLowerBound());
      s.append(").\n");
      s.append("roleMin(\"");
      s.append(asName);
      s.append("\",\"");
      s.append(as.getDestinationRoleName().toLowerCase());
      s.append("\",");
      s.append(as.getDestinationLowerBound());
      s.append(").\n");
    }      
    for (EAssociation as : asList) {
      String asName = emfModelReader.getAssociationName(as).toLowerCase();
      s.append("roleMax(\"");
      s.append(asName.toLowerCase());
      s.append("\",\"");
      s.append(as.getSourceRoleName().toLowerCase());
      s.append("\",");
      s.append(as.getSourceUpperBound() == -1 ? "\"*\"" : as.getSourceUpperBound());
      s.append(").\n");
      s.append("roleMax(\"");
      s.append(asName.toLowerCase());
      s.append("\",\"");
      s.append(as.getDestinationRoleName().toLowerCase());
      s.append("\",");
      s.append(as.getDestinationUpperBound() == -1 ? "\"*\"" : as.getDestinationUpperBound());
      s.append(").\n");
    }      
    return s.toString();    
  }
   
  protected String genAssociationIsUniqueSection() {
    StringBuilder s = new StringBuilder();

    for (String asName : asListNames) {
      s.append("assocIsUnique(\"");
      s.append(asName.toLowerCase());
      s.append("\", 1).\n");
    }
    return s.toString();
  }
  
  protected String genClassGeneralization() {
    StringBuilder s = new StringBuilder();
    
    for (EClass c : cList)
      if (c.getESuperTypes() != null)
        for (EClass superType : c.getESuperTypes()) {
          s.append("isSubTypeOf(\"");
          s.append(c.getName());
          s.append("\",\"");
          s.append(superType.getName());
          s.append("\").\n");          
        }    
    for (EClass c : cList) {
      List<EClass> subTypes = emfModelReader.getClassSubtypes(cList, c);
      StringBuilder subTypeNames = new StringBuilder();
      if (subTypes != null && subTypes.size() > 0) {
        for(EClass subType : subTypes) {
          subTypeNames.append(", L");
          subTypeNames.append(subType.getName());  
        }
        s.append("disjointInstances");
        s.append(subTypeNames.toString().replace(", L", ""));
        s.append("(");
        s.append(subTypeNames.toString().substring(2));
        s.append(") :-\n");
        s.append("\tdisjointOids([");
        s.append(subTypeNames.toString().substring(2));
        s.append("]).\n");
      }
    }   
    return s.toString();
  }
  
  protected String genModelPropertiesSection() {
    StringBuilder s = new StringBuilder();

    for(IModelProperty prop : properties) {
      if (prop instanceof StrongSatisfiabilityModelProperty)
        s.append("strongSatisfiability(CardVariables):- strongSatisfiabilityConstraint(CardVariables).\n");
      if (prop instanceof WeakSatisfiabilityModelProperty)
        s.append("weakSatisfiability(CardVariables):- weakSatisfiabilityConstraint(CardVariables).\n");
      if (prop instanceof LivelinessModelProperty) {
        List<String> liveliness = prop.getTargetModelElementsNames();
        for (String cName : liveliness) { 
          s.append("liveliness(CardVariables, \"");
          s.append(cName);
          s.append("\"):- livelinessConstraint(CardVariables, \"");
          s.append(cName);
          s.append("\").\n");
        }
      }
    }   
    
    return s.toString();
  }
  
  protected String genConstraintBinAssocMultiSection() {
    StringBuilder s = new StringBuilder();

    for (EAssociation as : asList) {
      s.append("constraints");
      s.append(as.getName().toLowerCase());
      s.append("Card(CardVariables):-constraintsBinAssocMultiplicities(\"");
      s.append(as.getName().toLowerCase());
      s.append("\"");
      
      s.append(", \"");
      s.append(as.getSourceRoleName().toLowerCase());
      s.append("\", \"");
      s.append(as.getDestinationRoleName().toLowerCase());
      s.append("\", CardVariables).\n");
    }    
    return s.toString();
  }
  
  private String getStandardTypeName(String typeName) {
    if (typeName == "int")
      return "Integer";
    if (typeName == "boolean")
      return "Boolean";
    return typeName;    
  }
  
  protected String genClassCreationSection() throws ProcessingException {
    StringBuilder s = new StringBuilder();
    
    for (EClass c : cList) {
      s.append("creation");
      s.append(c.getName());
      if (c.getESuperTypes() != null && c.getESuperTypes().size() > 0) 
        s.append("(Instances, Size, MaxId, Attributes):-\n\t");
      else 
        s.append("(Instances, Size, _, Attributes):-\n\t");
      s.append("length(Instances, Size),\n\t");
      if (c.getESuperTypes() != null && c.getESuperTypes().size() > 0) {
        s.append("(foreach(Xi, Instances), fromto([],AtIn,AtOut,Attributes), param(MaxId) do\n\t\t");
        s.append("Xi=");
        s.append(c.getName().toLowerCase());
        s.append("{oid:Integer1");
      }
      else {
        s.append("(foreach(Xi, Instances), fromto([],AtIn,AtOut,Attributes), for(N, 1, Size) do\n\t\t");
        s.append("Xi=");
        s.append(c.getName().toLowerCase());
        s.append("{oid:N");
      }
      
      List<EAttribute> atList = emfModelReader.getClassAttributes(c);
      List<EAttribute> atListNumeric = new ArrayList<EAttribute>();
      for (EAttribute at : atList) { 
    	  if (! (at.getEAttributeType().getName().equalsIgnoreCase("string") || at.getEAttributeType().getName().equalsIgnoreCase("estring")) ) {
    		  atListNumeric.add(at);
    	  }
      }
      int i = 1;
      for (EAttribute at : atList) {
    	  	++i;
			s.append(",");
			s.append(at.getName());
			s.append(":");
			if (atListNumeric.contains(at)) {
				s.append("Int");
			} else {
				s.append("Str");
			}
			s.append(i);
      }
      if (c.getESuperTypes() != null && c.getESuperTypes().size() > 0)
        s.append("}, Integer1::1..MaxId, ");
      else
        s.append("}, ");
      i = 1;
      for (EAttribute at : atList) {
    	  ++i;
    	  if (atListNumeric.contains(at)) {
		  	s.append("Int");
	    	s.append(i);
	    	s.append("#::");        
	    	s.append(elementsDomain.get(at.getEContainingClass().getName() + "." + at.getName()));
    	  } else {
    		  String lengthBound = elementsDomain.get(at.getEContainingClass().getName() + "." + at.getName() + ".length");
    		  String domainBound = elementsDomain.get(at.getEContainingClass().getName() + "." + at.getName() + ".domain");
    		  s.append("str_len(Str");
    		  s.append(i);
    		  s.append(",");
    		  s.append("LenStr" + i);
    		  s.append("),");
    		  s.append("LenStr" + i);
    		  s.append("#::");
    		  s.append(lengthBound);
    		  if (domainBound != null && domainBound.length() != 0) {
    			  if (! domainBound.matches("\\[\".*\"\\]") ) {
    				  throw new ProcessingException("String domain must be of the form [\"str1\",\"str2\",...]");
    			  }
    			  s.append(",str_constrain_str_domain(Str" + i + "," + domainBound + ")");
    		  }
    	  }
    	  s.append(",\n\t\t ");
      }

      if (c.getESuperTypes() != null && c.getESuperTypes().size() > 0)
        s.append("append([Integer1");
      else
        s.append("append([N");
      i = 1;
      for (EAttribute at : atList) {
    	  ++i;
    	  if (atListNumeric.contains(at)) {
    		  s.append(",");
    		  s.append("Int");
    		  s.append(i);
    	  }
      }
      s.append("],AtIn, AtOut)).\n\n");
    }

   
    for (EClass c : cList) {
      List<EClass> subTypes = emfModelReader.getClassSubtypes(cList, c);
      if (subTypes != null && subTypes.size() > 0)
        for(EClass subType : subTypes) {
          s.append("existingOids");
          s.append(subType.getName());
          s.append("In");
          s.append(c.getName());
          s.append("(O");
          s.append(subType.getName());
          s.append(", O");
          s.append(c.getName());          
          s.append("):-existsOidIn(O");
          s.append(subType.getName());
          s.append(", O");
          s.append(c.getName());          
          s.append(").\n");
        }
    }   
    return s.toString();
  }

  protected String genAssociationCreationSection() {
    StringBuilder s = new StringBuilder();
    
    for (EAssociation as : asList) {
      s.append("creation");
      s.append(as.getName().toLowerCase());
      s.append("(Instances, Size, Participants");
      s.append(", S");
      s.append(as.getSourceEnd().getName());
      s.append(", S");
      s.append(as.getDestinationEnd().getEType().getName());
      s.append("):-\n\tlength(Instances, Size),\n\t(foreach(Xi, Instances), fromto([],AtIn,AtOut,Participants)");
      s.append(", param(S");
      s.append(as.getSourceEnd().getName());
      s.append(")");
      s.append(", param(S");
      s.append(as.getDestinationEnd().getEType().getName());
      s.append(") do\n\t\tXi=");
      s.append(as.getName().toLowerCase());
      s.append("{");
      s.append(as.getSourceRoleName().toLowerCase());
      s.append(":ValuePart1,");
      s.append(as.getDestinationRoleName().toLowerCase());
      s.append(":ValuePart2}");
      s.append(", ValuePart1#>0, ValuePart1#=<S");
      s.append(as.getSourceEnd().getName());
      s.append(", ValuePart2#>0, ValuePart2#=<S");
      s.append(as.getDestinationEnd().getEType().getName());
      s.append(",\n\t\tappend([ValuePart1, ValuePart2],AtIn, AtOut)).\n");
    }    
    
    for (EAssociation as : asList) {
      s.append("cardinalityLinks");
      s.append(as.getName().toLowerCase());
      s.append("(Instances):-\n\tlinksConstraintMultiplicities(Instances, \"");
      s.append(as.getName().toLowerCase());
      s.append("\",\"");
      s.append(as.getSourceRoleName().toLowerCase());
      s.append("\",\"");
      s.append(as.getDestinationRoleName().toLowerCase());
      s.append("\").\n");
    }
    return s.toString();
  }   
}
