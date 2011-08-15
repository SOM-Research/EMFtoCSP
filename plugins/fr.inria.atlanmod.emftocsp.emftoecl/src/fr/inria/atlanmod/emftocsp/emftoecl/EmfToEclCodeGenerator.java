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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.utilities.ExpressionInOCL;

import fr.inria.atlanmod.emftocsp.IModelProperty;
import fr.inria.atlanmod.emftocsp.IModelReader;
import fr.inria.atlanmod.emftocsp.IModelToCspSolver;
import fr.inria.atlanmod.emftocsp.IOclParser;
import fr.inria.atlanmod.emftocsp.emf.impl.EAssociation;
import fr.inria.atlanmod.emftocsp.emf.impl.EmfCspCodeGenerator;
import fr.inria.atlanmod.emftocsp.impl.LackOfConstraintsRedundanciesModelProperty;
import fr.inria.atlanmod.emftocsp.impl.LackOfConstraintsSubsumptionsModelProperty;

/**
 * @author <a href="mailto:carlos.gonzalez@inria.fr">Carlos A. González</a>
 *
 */
public class EmfToEclCodeGenerator extends EmfCspCodeGenerator {
  IModelToCspSolver<Resource> modelSolver;
  
  public EmfToEclCodeGenerator(IModelToCspSolver<Resource> modelSolver) {
    this.modelSolver = modelSolver;
  }
    
  @SuppressWarnings("unchecked")
  @Override
  public String getCspCode() {
    setModel(modelSolver.getModel());
    setOclDocument(modelSolver.getConstraintsDocument());
    setModelElementsDomains(modelSolver.getModelElementsDomain());
    setProperties(modelSolver.getModelProperties());
    setModelReader((IModelReader<Resource, EPackage, EClass, EAssociation, EAttribute, EOperation>)modelSolver.getModelReader());
    setOclParser((IOclParser<Constraint, Resource>)modelSolver.getOclParser());
    
    StringBuilder s = new StringBuilder();
    s.append(translateEmfModel(getModelReader(), getModelElementsDomain(), getProperties()));
    s.append(translateOclConstraints(getOclParser(), getProperties(), getModel(), getOclDocument()));
    return s.toString();
  }
  
  private String translateEmfModel(IModelReader<Resource, EPackage, EClass, EAssociation, EAttribute, EOperation> emfModelReader, Map<String, String> modelElementsDomain, List<IModelProperty> properties) {
    StringBuilder s = new StringBuilder();
    try {
      List<String> constraintsNames = getOclParser().getModelInvariantNames(getModel(), getOclDocument());
      ModelToEcl emfTranslator = new ModelToEcl(emfModelReader, modelElementsDomain, properties, constraintsNames);
      
      s.append(emfTranslator.genLibsSection());
      s.append("\n");
      s.append(emfTranslator.genStructSection());
      s.append("\n");
      s.append(emfTranslator.genHeaderSection());
      s.append("\n");
      s.append(emfTranslator.genCardinalityDefinitionsSection());
      s.append("\n");
      s.append(emfTranslator.genCardinalityConstraintsSection());
      s.append("\n");
      s.append(emfTranslator.genCardinalityInstantiationSection());
      s.append("\n");
      s.append(emfTranslator.genObjectsCreationSection());
      s.append("\n");
      s.append(emfTranslator.genLinksCreationSection());
      s.append("\n");
      s.append(emfTranslator.genInstancesSection());
      s.append("\n");
      s.append(emfTranslator.genOclRootSection());
      s.append("\n");
      s.append(emfTranslator.genAllAttributesSection());
      s.append("\n");
      s.append(emfTranslator.genGeneralizationSection());
      s.append("\n");
      s.append(emfTranslator.genIndexesSection());
      s.append("\n");
      s.append(emfTranslator.genAssociationRolesSection());
      s.append("\n");
      s.append(emfTranslator.genAssociationIsUniqueSection());
      s.append("\n");
      s.append(emfTranslator.genClassGeneralization());
      s.append("\n");
      s.append(emfTranslator.genModelPropertiesSection());
      s.append("\n");
      s.append(emfTranslator.genConstraintBinAssocMultiSection());
      s.append("\n");
      s.append(emfTranslator.genClassCreationSection());
      s.append("\n");
      s.append(emfTranslator.genAssociationCreationSection());
      s.append("\n");
    }
    catch (Exception e) {
      e.printStackTrace();
      System.out.println(e.getMessage());
    }
    return s.toString();
  }
  
  @SuppressWarnings("rawtypes")
  private String translateOclConstraints(IOclParser<Constraint, Resource> oclParser, List<IModelProperty> properties, Resource modelResource, IFile oclDocument) {
    StringBuilder s = new StringBuilder();
    HashMap<String, String> ctfpMap = new HashMap<String, String>();
    
    try {
      OclToEcl oclVisitor = OclToEcl.getInstance();                
      List<Constraint> cList = oclParser.parseModelConstraints(modelResource, oclDocument);
      for (Constraint c : cList) {
        if (!c.getStereotype().equalsIgnoreCase("precondition") && !c.getStereotype().equalsIgnoreCase("postcondition")) {
          ExpressionInOCL oclExpression = (ExpressionInOCL) c.getSpecification();
          s.append(oclExpression.accept(oclVisitor));
          ctfpMap.put(c.getName(), oclVisitor.getConstraintFirstPredicate());
          s.append("\n");                      
        }
      }
      LackOfConstraintsSubsumptionsModelProperty cSub = null;
      LackOfConstraintsRedundanciesModelProperty cRed = null;
      for(IModelProperty prop : properties) {
        if (prop instanceof LackOfConstraintsSubsumptionsModelProperty)
          cSub = (LackOfConstraintsSubsumptionsModelProperty) prop;
        if (prop instanceof LackOfConstraintsRedundanciesModelProperty)
          cRed = (LackOfConstraintsRedundanciesModelProperty) prop;
      }   
      if (cSub != null)
        for(String cNames : cSub.getTargetModelElementsNames()) {
          String[] nameList = cNames.split(",");
          s.append("noSubsumption");
          s.append(cNames.replace(",", ""));
          s.append("(Instances):- ");
          s.append("noConstraintSubsumption(Instances, "); 
          s.append(ctfpMap.get(nameList[0]));
          s.append(",");
          s.append(ctfpMap.get(nameList[1]));
          s.append(").\n");
        }
      if (cRed != null)
        for(String cNames : cRed.getTargetModelElementsNames()) {
          String[] nameList = cNames.split(",");
          s.append("noRedundancy");
          s.append(cNames.replace(",", ""));
          s.append("(Instances):- ");
          s.append("noConstraintRedundancy(Instances, "); 
          s.append(ctfpMap.get(nameList[0]));
          s.append(",");
          s.append(ctfpMap.get(nameList[1]));
          s.append(").\n");
        }
    }
    catch (Exception e) {
      e.printStackTrace();
      System.out.println(e.getMessage());
    }
    return s.toString();
  }
    
  @Override
  public String getCspCodeFileExtension() {
    return "ecl";
  }
}
