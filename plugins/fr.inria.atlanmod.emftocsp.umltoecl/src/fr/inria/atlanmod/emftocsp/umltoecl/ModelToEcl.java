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
package fr.inria.atlanmod.emftocsp.umltoecl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Generalization;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.resource.UMLResource;

import fr.inria.atlanmod.emftocsp.ILogger;
import fr.inria.atlanmod.emftocsp.IModelProperty;
import fr.inria.atlanmod.emftocsp.IModelReader;
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
  IModelReader<UMLResource, Package, Class, Association, Property, Operation> umlModelReader;
  List<Package> pList; 
  List<Class> cList; 
  List<String> cListNames; 
  List<Association> asList;
  List<String> asListNames;
  List<String> constraintsNames;
  Map<String, String> elementsDomain;
  List<IModelProperty> properties;
  ILogger logger;

  public ModelToEcl(IModelReader<UMLResource, Package, Class, Association, Property, Operation> umlModelReader, Map<String, String> elementsDomain, List<IModelProperty> properties, List<String> constraintsNames, ILogger logger) {
    this.umlModelReader = umlModelReader;
    cList = umlModelReader.getClasses();
    pList = umlModelReader.getPackages();
    asList = umlModelReader.getAssociations();
    cListNames = umlModelReader.getClassesNames();
    asListNames = umlModelReader.getAssociationsNames();
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
    List<Property> pList = new ArrayList<Property>();

    for (Class c : cList) {
      s.append(":- local struct(");
      s.append(c.getName().toLowerCase());
      s.append("(oid");
      pList = umlModelReader.getClassAttributes(c);
      for (Property p : pList) { 
        s.append(",");
        s.append(p.getName());
      }
      s.append(")).\n");
    }
    for (Association as : asList) {
      s.append(":- local struct(");
      s.append(umlModelReader.getAssociationName(as).toLowerCase());
      s.append("(");
      StringBuilder asEnds = new StringBuilder();
      for(Property p : as.getOwnedEnds()) {
        asEnds.append(",");
        asEnds.append(umlModelReader.getAssociationEndName(p));        
      }
      s.append(asEnds.toString().substring(1));
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
    s.append("\t%Cardinality definitions\n\t");
    for (Class c : cList) {
      s.append("S");
      s.append(c.getName());
      nameList += "S" + c.getName() + ", ";
      s.append("::");
      s.append(elementsDomain.get(c.getPackage().getName() + "." + c.getName()));
      s.append(", ");
    }
    s.append("\n\t");

    for (String asName : asListNames) {
      s.append("S");
      s.append(asName);
      nameList += "S" + asName + ",";      
      s.append("::");
      s.append(elementsDomain.get(asName));
      s.append(", ");
    }
    s.append("\n\t");
    s.append("CardVariables=[");
    s.append(nameList.substring(0, nameList.length() - 1));
    s.append("],\n\t");
    return s.toString();
  }
  
  protected String genCardinalityConstraintsSection() {  
    StringBuilder s = new StringBuilder();
    s.append("\t%Cardinality constraints\n\t");

    for(IModelProperty prop : properties) {
      if (prop instanceof StrongSatisfiabilityModelProperty)
        s.append("strongSatisfiability(CardVariables),");
      if (prop instanceof WeakSatisfiabilityModelProperty)
        s.append("\n\tweakSatisfiability(CardVariables),");
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
    for (Class c : cList) {
      List<Class> subTypes = umlModelReader.getClassSubtypes(cList, c);
      StringBuilder subTypeNames = new StringBuilder();
      if (subTypes != null) {
        for(Class subType : subTypes) { 
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
      s.append(asName);
      s.append("Card(CardVariables),\n\t");
    }
    return s.toString();    
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

    for (Class c : cList) {
      s.append("creation");
      s.append(c.getName());
      s.append("(O");
      s.append(c.getName());
      s.append(", S");
      s.append(c.getName());
      s.append(", S");
      s.append(umlModelReader.getBaseClass(c).getName());
      s.append(", At");
      s.append(c.getName());
      s.append("),\n\t");
    }
    s.append("\n\t");    

    for (String cName : cListNames) {
      s.append("\n\tdifferentOids");
      s.append(cName);
      s.append("(O");
      s.append(cName);
      s.append("),");
    }
    s.append("\n\t");    
    
    for (String cName : cListNames) {
      s.append("\n\torderedInstances");
      s.append(cName);
      s.append("(O");
      s.append(cName);
      s.append("),");
    }
    s.append("\n\t");     
    for (Class c : cList) {
      List<Class> subTypes = umlModelReader.getClassSubtypes(cList, c);
      if (subTypes != null) 
        for(Class subType : subTypes) { 
          s.append("existingOids");
          s.append(subType.getName());
          s.append("In");  
          s.append(c.getName());
          s.append("(O");
          s.append(subType.getName());
          s.append(", O");
          s.append(c.getName());
          s.append("),\n\t");
        }
    }    
    for (Class c : cList) {
      List<Class> subTypes = umlModelReader.getClassSubtypes(cList, c);
      StringBuilder subTypeNames = new StringBuilder();
      if (subTypes != null && subTypes.size() > 0) {
        for(Class subType : subTypes) { 
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
   
    for (Association as : asList) {
      String asName = umlModelReader.getAssociationName(as);
      s.append("creation");
      s.append(asName);
      s.append("(L");
      s.append(asName);
      s.append(", S");
      s.append(asName);
      s.append(", P");
      s.append(asName);
      for(Property p : as.getOwnedEnds()) {
        s.append(", S");
        s.append(umlModelReader.getBaseClass((Class)p.getType()).getName());
      }
      s.append("),\n\t");
    }    
    for (String asName : asListNames) {
        s.append("differentLinks");
        s.append("(L");
        s.append(asName);
        s.append("),\n\t");
      } 
      for (String asName : asListNames) {
        s.append("orderedLinks");
        s.append("(Instances,\"" + asName + "\"");
        s.append("),\n\t");
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
      s.append(asName);
      s.append(", ");
    }   
    s.deleteCharAt(s.length() - 2);
    s.append("],\n\t");
    return s.toString();
  }
 
  protected String genInstancesSection2() {
	    StringBuilder s = new StringBuilder();
    
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
      s.append(asName);
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
  
  protected String genAllAttributesSection() {
    StringBuilder s = new StringBuilder();
    
    s.append("\tAllAttributes = [");
    for (String asName : asListNames) {
      s.append("P");
      s.append(asName);
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
    s.append("\n\t%Instantiation of attributes values\n\t");
    s.append("labeling(Attributes).\n");
    return s.toString();    
  }
  
  protected String genGeneralizationSection() {
    StringBuilder s = new StringBuilder();
    
    for (Class c : cList) {
      List<Class> subTypes = umlModelReader.getClassSubtypes(cList, c);
      if (subTypes != null && subTypes.size() > 0) {
        s.append("constraintsGen");
        s.append(c.getName());
        for(Class subType : subTypes) 
          s.append(subType.getName());        
        s.append("(S");
        s.append(c.getName());
        for(Class subType : subTypes) {
          s.append(", S");
          s.append(subType.getName());        
        }
        s.append("):-\n");
        s.append(c.isAbstract() ? "constraintsAbstractDisjointSubtypesCard(S" : "constraintsDisjointSubtypesCard(S");
        s.append(c.getName());
        s.append(", [");
        for(Class subType : subTypes) {
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
      s.append(asName);
      s.append("\",");
      s.append(i++);
      s.append(").\n");
    }      
    List<Property> pList = new ArrayList<Property>();
    for (Class c : cList) {
      i = 1;
      pList = umlModelReader.getClassAttributes(c);
      for (Property p : pList) { 
        s.append("attIndex(\"");
        s.append(c.getName());
        s.append("\",\"");
        s.append(p.getName());
        s.append("\",");
        s.append(++i);
        s.append(").\n");
      }
    }
    for (Class c : cList) {
        i = 1;
        List<Property> atList = umlModelReader.getClassAttributes(c);
        for (Property at : atList) { 
          s.append("attType(\"");
          s.append(c.getName());
          s.append("\",\"");
          s.append(at.getName());
          s.append("\",\"");
          s.append(at.getType().getName());
          s.append("\").\n");
          ++i;
        }
      }  
    return s.toString();
  }
  
  protected String genAssociationRolesSection() {
    StringBuilder s = new StringBuilder();

    for (Association as : asList) {
      String asName = umlModelReader.getAssociationName(as);
      int i = 1;
      for(Property p : as.getOwnedEnds()) {
        s.append("roleIndex(\"");
        s.append(asName);
        s.append("\",\"");
        s.append(umlModelReader.getAssociationEndName(p));
        s.append("\",");
        s.append(i++);
        s.append(").\n");
      }
    }     
    for (Association as : asList) {
      String asName = umlModelReader.getAssociationName(as);
      for(Property p : as.getOwnedEnds()) {
        s.append("roleType(\"");
        s.append(asName);
        s.append("\",\"");
        s.append(umlModelReader.getAssociationEndName(p));
        s.append("\",\"");
        s.append(p.getType().getName());
        s.append("\").\n");
      }
    }   
    for (Association as : asList) {
      String asName = umlModelReader.getAssociationName(as);
      for(Property p : as.getOwnedEnds()) {
        s.append("roleMin(\"");
        s.append(asName);
        s.append("\",\"");
        s.append(umlModelReader.getAssociationEndName(p));
        s.append("\",");
        s.append(p.lowerBound());
        s.append(").\n");
      }
    }       
    for (Association as : asList) {
      String asName = umlModelReader.getAssociationName(as);
      for(Property p : as.getOwnedEnds()) {
        s.append("roleMax(\"");
        s.append(asName);
        s.append("\",\"");
        s.append(umlModelReader.getAssociationEndName(p));
        s.append("\",");
        s.append(p.upperBound() == -1 ? "\"*\"" : p.upperBound());  
        s.append(").\n");
      }
    }       
    return s.toString();    
  }
   
  protected String genAssociationIsUniqueSection() {
    StringBuilder s = new StringBuilder();

    for (String asName : asListNames) {
      s.append("assocIsUnique(\"");
      s.append(asName);
      s.append("\", 1).\n");
    }     
    return s.toString();
  }
  
  protected String genClassGeneralization() {
    StringBuilder s = new StringBuilder();
    
    for (Class c : cList)
      if (c.getGeneralizations() != null)
        for (Generalization g : c.getGeneralizations()) {
          s.append("isSubTypeOf(\"");
          s.append(c.getName());
          s.append("\",\"");
          s.append(g.getGeneral().getName());
          s.append("\").\n");          
        }    
    for (Class c : cList) {
      List<Class> subTypes = umlModelReader.getClassSubtypes(cList, c);
      StringBuilder subTypeNames = new StringBuilder();
      if (subTypes != null && subTypes.size() > 0) {
        for(Class subType : subTypes) {
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

    for (Association as : asList) {
      String asName = umlModelReader.getAssociationName(as);
      s.append("constraints");
      s.append(asName);
      s.append("Card(CardVariables):-constraintsBinAssocMultiplicities(\"");
      s.append(asName);
      s.append("\"");
      for(Property p : as.getOwnedEnds()) {
        s.append(", \"");
        s.append(umlModelReader.getAssociationEndName(p));
        s.append("\"");
      }
      s.append(", CardVariables).\n");
    }       
    return s.toString();
  }
  
  protected String genClassCreationSection() {
    StringBuilder s = new StringBuilder();
    
    for (Class c : cList) {
      s.append("creation");
      s.append(c.getName());
      if (c.getGeneralizations() != null && c.getGeneralizations().size() > 0) 
        s.append("(Instances, Size, MaxId, Attributes):-\n\t");
      else 
        s.append("(Instances, Size, _, Attributes):-\n\t");
      s.append("length(Instances, Size),\n\t");
      if (c.getGeneralizations() != null && c.getGeneralizations().size() > 0) {
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
      
      List<Property> pList = umlModelReader.getClassAttributes(c);
      int i = 1;
      for (Property p : pList) { 
        s.append(",");
        s.append(p.getName());
        s.append(":");
        s.append(p.getType().getName());
        s.append(++i);
      }
      if (c.getGeneralizations() != null && c.getGeneralizations().size() > 0)
        s.append("}, Integer1::1..MaxId, ");
      else
        s.append("}, ");
      i = 1;
      for (Property p : c.getAttributes()) { 
        s.append(p.getType().getName());
        s.append(++i);
        s.append("::");
        s.append(elementsDomain.get(c.getName() + "." + p.getName()));
        s.append(", ");
      }
      s.append("\n\t\t");

      if (c.getGeneralizations() != null && c.getGeneralizations().size() > 0)
        s.append("append([Integer1");
      else
        s.append("append([N");
      i = 1;
      for (Property p : c.getAttributes()) { 
        s.append(",");
        s.append(p.getType().getName());
        s.append(++i);
      }
      s.append("],AtIn, AtOut)).\n");
    }
    for (String cName : cListNames) {
      s.append("differentOids");
      s.append(cName);
      s.append("(Instances) :- differentOids(Instances).\n");
      s.append("orderedInstances");
      s.append(cName);
      s.append("(Instances) :- orderedInstances(Instances).\n");      
    }
    
    for (Class c : cList) {
      List<Class> subTypes = umlModelReader.getClassSubtypes(cList, c);
      if (subTypes != null && subTypes.size() > 0)
        for(Class subType : subTypes) {
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

    for (Association as : asList) {
      String asName = umlModelReader.getAssociationName(as);
      s.append("creation");
      s.append(asName);
      s.append("(Instances, Size, Participants");
      for(Property p : as.getOwnedEnds()) {
        s.append(", S");
        s.append(p.getType().getName());
      }
      s.append("):-\n\tlength(Instances, Size),\n\t(foreach(Xi, Instances), fromto([],AtIn,AtOut,Participants)");
      for(Property p : as.getOwnedEnds()) {
        s.append(", param(S");
        s.append(p.getType().getName());
        s.append(")");
      }
      s.append(" do\n\t\tXi=");
      s.append(asName.toLowerCase());
      s.append("{");
      int i = 1;
      StringBuilder vPart = new StringBuilder();
      for(Property p : as.getOwnedEnds()) {
        vPart.append(",");
        vPart.append(umlModelReader.getAssociationEndName(p));
        vPart.append(":ValuePart");
        vPart.append(i++);
      }
      s.append(vPart.toString().substring(1));
      s.append("}");
      i = 1;
      for(Property p : as.getOwnedEnds()) {
        s.append(", ValuePart");
        s.append(i);
        s.append("#>0, ValuePart");
        s.append(i);
        s.append("#=<S");
        s.append(p.getType().getName());
        i++;
      }      
      s.append(",\n\t\tappend([");       // Check this
      for(i = 1; i <= as.getOwnedEnds().size(); i++) {
        s.append("ValuePart");
        s.append(i);
        if (i != as.getOwnedEnds().size())
          s.append(",");
      } 
      s.append("],AtIn, AtOut)).\n");
    } 
    for (String asName : asListNames) {
      s.append("differentLinks");
      s.append(asName);
      s.append("(X):- differentLinks(X).\n");
    }
    for (String asName : asListNames) {
      s.append("orderedLinks");
      s.append(asName);
      s.append("(X):- orderedLinks(X).\n");
    }    
    for (Association as : asList) {
      String asName = umlModelReader.getAssociationName(as);
      s.append("cardinalityLinks");
      s.append(asName);
      s.append("(Instances):-\n\tlinksConstraintMultiplicities(Instances, \"");
      s.append(asName);
      s.append("\"");
      for(Property p : as.getOwnedEnds()) {
        s.append(",\"");
        s.append(umlModelReader.getAssociationEndName(p));
        s.append("\"");
      } 
      s.append(").\n");      
    }    
    return s.toString();
  }     
}
