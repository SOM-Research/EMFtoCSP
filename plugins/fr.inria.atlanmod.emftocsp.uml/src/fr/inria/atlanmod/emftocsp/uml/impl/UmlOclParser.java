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
package fr.inria.atlanmod.emftocsp.uml.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.ocl.OCLInput;
import org.eclipse.ocl.ParserException;
import org.eclipse.ocl.helper.OCLHelper;
import org.eclipse.ocl.uml.OCL;
import org.eclipse.ocl.uml.UMLEnvironmentFactory;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Constraint;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.resource.UMLResource;

import fr.inria.atlanmod.emftocsp.IOclParser;
import fr.inria.atlanmod.emftocsp.ProcessingException;

/**
 * @author <a href="mailto:carlos.gonzalez@inria.fr">Carlos A. González</a>
 *
 */
public class UmlOclParser implements IOclParser<Constraint, UMLResource> {
    
  @Override
  public Constraint parseOclConstraint(Object context, String key, String constraint) {
    OCL ocl = org.eclipse.ocl.uml.OCL.newInstance();
    OCLHelper<Classifier, Operation, Property, Constraint> helper = ocl.createOCLHelper();
    
    if (context instanceof Classifier) 
      helper.setContext((Classifier)context);
    else if (context instanceof Operation) {
      Operation op = (Operation) context;
      Classifier c = (Classifier)op.getOwner();
      helper.setOperationContext(c, op);
    } 
    else if (context instanceof Property) {
      Property p = (Property) context;
      Classifier c = (Classifier)p.getOwner();
      helper.setAttributeContext(c, p);
    }    
    try {
      Constraint c = null;
      if (key.equalsIgnoreCase("postcondition")) //$NON-NLS-1$
        c = helper.createPostcondition(constraint);
      else if (key.equalsIgnoreCase("precondition")) //$NON-NLS-1$
        c = helper.createPrecondition(constraint);
      else //"invariant"
        c = (Constraint)helper.createInvariant(constraint);    
      return c;
    }
    catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  @Override
  public List<Constraint> parseEmbeddedConstraints(UMLResource modelResource) {
    List<Constraint> constraints = new ArrayList<Constraint>();
    UmlModelReader modelReader = new UmlModelReader(modelResource);
    
    for (Class c : modelReader.getClasses()) {
      List<EAnnotation> annotationList = c.getEAnnotations();      
      if (annotationList != null) 
        for (EAnnotation ea : annotationList)
          if (ea.getSource().endsWith("ocl") || ea.getSource().endsWith("OCL"))   //$NON-NLS-1$ //$NON-NLS-2$
            for (String key : ea.getDetails().keySet()) {        
              EObject context = ea.getEModelElement();
              String val = ea.getDetails().get(key);              
              if (!key.equalsIgnoreCase("invariant") && !key.equalsIgnoreCase("body") && !key.equalsIgnoreCase("derivation") && !key.equalsIgnoreCase("precondition") && !key.equalsIgnoreCase("postcondition") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                  && !key.equalsIgnoreCase("inv") && !key.equalsIgnoreCase("pre") && !key.equalsIgnoreCase("post") && context instanceof Classifier) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                Constraint ct = parseOclConstraint(ea.getEModelElement(), key, val);
                ct.setName(key);
                constraints.add(ct);
              }
            }     
      List<Operation> operationList = c.getOperations();
      if (operationList != null) 
        for (Operation op : operationList) {
          annotationList = op.getEAnnotations();
          if (annotationList != null)
            for (EAnnotation ea : annotationList)
              if (ea.getSource().endsWith("ocl") || ea.getSource().endsWith("OCL"))   //$NON-NLS-1$ //$NON-NLS-2$
                for (String key : ea.getDetails().keySet()) {        
                  EObject context = ea.getEModelElement();
                  String val = ea.getDetails().get(key);              
                  if (key.equalsIgnoreCase("precondition") || key.equalsIgnoreCase("pre") || key.equalsIgnoreCase("postcondition") || key.equalsIgnoreCase("post") && context instanceof Operation) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                    Constraint ct = parseOclConstraint(ea.getEModelElement(), key, val);
                    ct.setName(op.getName() + "_" + key); //$NON-NLS-1$
                    constraints.add(ct);
                  }
                }
        }      
    }    
    return constraints;
  }
  
  @Override
  public List<Constraint> parseOclDocument(IFile oclDocument, UMLResource modelResource) throws ProcessingException {
    List<Constraint> constraints = new ArrayList<Constraint>();
    if (oclDocument != null) {
      InputStream in;
	try {
		in = new FileInputStream(oclDocument.getRawLocation().toOSString());
	} catch (FileNotFoundException e) {
		throw new ProcessingException(e);
	}    
      OCLInput document = new OCLInput(in);
      UMLResource resource = modelResource;
      UMLEnvironmentFactory umlEnv = new UMLEnvironmentFactory(resource.getResourceSet());
      OCL oclParser = OCL.newInstance(umlEnv);
      try {
		constraints = oclParser.parse(document);
	} catch (ParserException e) {
		throw new ProcessingException(e);
	}
    }
    return constraints;
  }
  
  @Override
  public List<Constraint> parseModelConstraints(UMLResource modelResource, IFile oclDocument) throws ProcessingException {
    List<Constraint> constraints =  parseEmbeddedConstraints(modelResource);
    constraints.addAll(parseOclDocument(oclDocument, modelResource));
    return constraints;
  }  
  
  @Override
  public List<String> getModelConstraintsNames(UMLResource modelResource, IFile oclDocument) throws ProcessingException {
    List<Constraint> constraints = parseModelConstraints(modelResource, oclDocument);
    List<String> cNames = new ArrayList<String>();
    for (Constraint c : constraints) {
      if (c.getName() == null) //OCL preconditions and postconditions parsed from an external OCL document don't have any name
        cNames.add(""); //$NON-NLS-1$
      cNames.add(c.getName());
    }
    return cNames;
  }

  @Override
  public List<String> getModelInvariantNames(UMLResource modelResource, IFile oclDocument) throws ProcessingException {
    List<Constraint> constraints = parseModelConstraints(modelResource, oclDocument);
    List<String> cNames = new ArrayList<String>();
    for (Constraint c : constraints) {
      List<String> keywords = c.getKeywords();
      if (keywords != null)
        for (String keyword : keywords)
          if (!keyword.equalsIgnoreCase("precondition") && !keyword.equalsIgnoreCase("postcondition")) //$NON-NLS-1$ //$NON-NLS-2$
            cNames.add(c.getName());
    }
    return cNames;
  }
  
}  


