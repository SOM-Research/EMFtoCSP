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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.ocl.OCLInput;
import org.eclipse.ocl.ParserException;
import org.eclipse.ocl.ecore.Constraint;
import org.eclipse.ocl.ecore.EcoreEnvironmentFactory;
import org.eclipse.ocl.ecore.OCL;
import org.eclipse.ocl.helper.OCLHelper;

import org.eclipse.uml2.uml.Classifier;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Property;

import fr.inria.atlanmod.emftocsp.IOclParser;
import fr.inria.atlanmod.emftocsp.ProcessingException;
import fr.inria.atlanmod.emftocsp.adapters.umlImpl.EClassifierUMLAdapter;
import fr.inria.atlanmod.emftocsp.adapters.umlImpl.EOperationUMLAdapter;
import fr.inria.atlanmod.emftocsp.adapters.umlImpl.EStructuralFeatureUMLAdapter;

/**
 * @author <a href="mailto:carlos.gonzalez@inria.fr">Carlos A. González</a>
 *
 */
public class EmfOclParser implements IOclParser<Constraint, Resource> {

  @Override
  public Constraint parseOclConstraint(Object context, String key, String constraint) {
    OCL ocl = org.eclipse.ocl.ecore.OCL.newInstance();
    OCLHelper<EClassifier, EOperation, EStructuralFeature, Constraint> helper = ocl.createOCLHelper();
    
    if (context instanceof EClassifier ){     
    	  helper.setContext((EClassifier)context);
      }
    else if (context instanceof EOperation) {
    	EOperation eOp = (EOperation) context;
      helper.setOperationContext(eOp.getEContainingClass(), eOp);
    } 
    else if (context instanceof EStructuralFeature  ) {
    	EStructuralFeature sf = (EStructuralFeature) context;
      helper.setAttributeContext(sf.getEContainingClass(), sf);
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
	public List<Constraint> parseEmbeddedConstraints(Resource modelResource) {
		List<Constraint> constraints = new ArrayList<Constraint>();
		EmfModelReader modelReader = new EmfModelReader(modelResource);
		for (EClass c : modelReader.getClasses()) {

			// TODO: where are these constants defined?
			// TODO: make code more robust and verbose
			EAnnotation eaConstraints = c.getEAnnotation("http://www.eclipse.org/emf/2002/Ecore");
			EAnnotation eaOCLPivot = c.getEAnnotation("http://www.eclipse.org/emf/2002/Ecore/OCL/Pivot");
			if (eaConstraints != null && eaOCLPivot != null) {
				String constraintNames = eaConstraints.getDetails().get("constraints");
				if (constraintNames != null) {
					String[] constraintNamesArr = constraintNames.split(" ");
					for (String constraintName : constraintNamesArr) {
						String constraintBody = eaOCLPivot.getDetails().get(constraintName);
						System.err.println("Processing constraint: " + constraintName);
						System.err.println("Body: " + constraintBody);
						Constraint ct = parseOclConstraint(c, constraintName, constraintBody);
						if (ct != null) {
							ct.setName(constraintName);
							constraints.add(ct);
						}
					}
				}
			}

			/*
			 * FIXME: This way of getting the OCL constraints seems to be out of
			 * date?
			 */
			List<EAnnotation> annotationList = c.getEAnnotations();
			if (annotationList != null)
				for (EAnnotation ea : annotationList) {

					if (ea.getSource().endsWith("ocl") || ea.getSource().endsWith("OCL")) //$NON-NLS-1$ //$NON-NLS-2$
						for (String key : ea.getDetails().keySet()) {
							EObject context = ea.getEModelElement();
							String val = ea.getDetails().get(key);
							if (!key.equalsIgnoreCase("invariant") && !key.equalsIgnoreCase("body") && !key.equalsIgnoreCase("derivation") && !key.equalsIgnoreCase("precondition") && !key.equalsIgnoreCase("postcondition") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
									&& !key.equalsIgnoreCase("inv") && !key.equalsIgnoreCase("pre") && !key.equalsIgnoreCase("post") && context instanceof EClassifier) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
								Constraint ct = parseOclConstraint(ea.getEModelElement(), key, val);
								ct.setName(key);
								constraints.add(ct);
							}
						}

				}

			List<EOperation> operationList = c.getEOperations();
			if (operationList != null)
				for (EOperation op : operationList) {
					annotationList = op.getEAnnotations();
					if (annotationList != null)
						for (EAnnotation ea : annotationList)
							if (ea.getSource().endsWith("ocl") || ea.getSource().endsWith("OCL")) //$NON-NLS-1$ //$NON-NLS-2$
								for (String key : ea.getDetails().keySet()) {
									EObject context = ea.getEModelElement();
									String val = ea.getDetails().get(key);
									if (key.equalsIgnoreCase("precondition") || key.equalsIgnoreCase("pre") || key.equalsIgnoreCase("postcondition") || key.equalsIgnoreCase("post") && context instanceof EOperation) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
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
  public List<Constraint> parseOclDocument(IFile oclDocument, Resource modelResource) throws ProcessingException {
    List<Constraint> constraints = new ArrayList<Constraint>();
    if (oclDocument != null) {
      InputStream in;
	try {
		in = new FileInputStream(oclDocument.getRawLocation().toOSString());
	} catch (FileNotFoundException e) {
		throw new ProcessingException( e );
	}  
      OCLInput document = new OCLInput(in);
      Resource resource = modelResource;
      EcoreEnvironmentFactory ecoreEnv = new EcoreEnvironmentFactory(resource.getResourceSet().getPackageRegistry());
      OCL oclParser = OCL.newInstance(ecoreEnv);
      try {
		constraints = oclParser.parse(document);
	} catch (ParserException e) {
		throw new ProcessingException( e );
		}
    }
    return constraints;
  }  

  @Override
  public List<Constraint> parseModelConstraints(Resource modelResource, IFile oclDocument) throws ProcessingException  {
    List<Constraint> constraints =  parseEmbeddedConstraints(modelResource);
    constraints.addAll(parseOclDocument(oclDocument, modelResource));
    return constraints;
  }

  @Override
  public List<String> getModelConstraintsNames(Resource modelResource, IFile oclDocument) throws ProcessingException {
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
  public List<String> getModelInvariantNames(Resource modelResource, IFile oclDocument) throws ProcessingException  {
    List<Constraint> constraints = parseModelConstraints(modelResource, oclDocument);
    List<String> cNames = new ArrayList<String>();
    for (Constraint c : constraints)     
      if (!c.getStereotype().equals("precondition") && !c.getStereotype().equals("postcondition")) //$NON-NLS-1$ //$NON-NLS-2$
        cNames.add(c.getName());
    return cNames;
  }
  
}  


