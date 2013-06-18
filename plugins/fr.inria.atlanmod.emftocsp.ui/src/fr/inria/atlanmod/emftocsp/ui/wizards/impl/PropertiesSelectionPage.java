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
package fr.inria.atlanmod.emftocsp.ui.wizards.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

import fr.inria.atlanmod.emftocsp.IModelProperty;
import fr.inria.atlanmod.emftocsp.IModelReader;
import fr.inria.atlanmod.emftocsp.IModelToCspSolver;
import fr.inria.atlanmod.emftocsp.IOclParser;
import fr.inria.atlanmod.emftocsp.impl.LackOfConstraintsRedundanciesModelProperty;
import fr.inria.atlanmod.emftocsp.impl.LackOfConstraintsSubsumptionsModelProperty;
import fr.inria.atlanmod.emftocsp.impl.LivelinessModelProperty;
import fr.inria.atlanmod.emftocsp.impl.StrongSatisfiabilityModelProperty;
import fr.inria.atlanmod.emftocsp.impl.WeakSatisfiabilityModelProperty;
import fr.inria.atlanmod.emftocsp.ui.messages.Messages;

/**
 * @author <a href="mailto:carlos.gonzalez@inria.fr">Carlos A. González</a>
 *
 */
public class PropertiesSelectionPage extends WizardPage {  
   
  private Button cmdStrongSatisfiability;
  private Button cmdWeakSatisfiability;
  private Button cmdLiveliness;
  private Combo cbLiveliness;
  private Button cmdLackOfConstraintsSubsumptions;
  private Combo cbLackSubsumptions1;
  private Combo cbLackSubsumptions2;
  private Button cmdLackOfConstraintsRedundancies;
  private Combo cbLackRedundancies1;
  private Combo cbLackRedundancies2;
  IModelToCspSolver<?,?> modelSolver;
  HashMap<String,Object> mapModelElementName;
  
  public PropertiesSelectionPage(String pageName, IModelToCspSolver<?,?> modelSolver) {
	  super(pageName);
	  setTitle(pageName);
	  String description = Messages.PropertiesSelectionPage_0;
	  setDescription(description);
	  this.modelSolver = modelSolver; 
  }
  
  private void setModelProperties() {
    List<IModelProperty> modelProperties = new ArrayList<IModelProperty>();
    if (cmdStrongSatisfiability.getSelection())
      modelProperties.add(new StrongSatisfiabilityModelProperty());
    if (cmdWeakSatisfiability.getSelection())
      modelProperties.add(new WeakSatisfiabilityModelProperty());
    if (cmdLiveliness.getSelection()) {
      ArrayList<String> modelElementsNames = new ArrayList<String>();
      modelElementsNames.add(cbLiveliness.getText());
      modelProperties.add(new LivelinessModelProperty(modelElementsNames));
    }
    if (cmdLackOfConstraintsSubsumptions.getSelection()) {
      ArrayList<String> modelElementsNames = new ArrayList<String>();
      modelElementsNames.add(cbLackSubsumptions1.getText() + "," + cbLackSubsumptions2.getText());  //$NON-NLS-1$
      modelProperties.add(new LackOfConstraintsSubsumptionsModelProperty(modelElementsNames));
    }
    if (cmdLackOfConstraintsRedundancies.getSelection()) {
      ArrayList<String> modelElementsNames = new ArrayList<String>();
      modelElementsNames.add(cbLackRedundancies1.getText() + "," + cbLackRedundancies2.getText());  //$NON-NLS-1$
      modelProperties.add(new LackOfConstraintsRedundanciesModelProperty(modelElementsNames));
    }
    modelSolver.setModelProperties(modelProperties);
  }
  

  @Override
  public void setVisible(boolean visible) {
  	super.setVisible(visible);
  	if (visible) {
  		fillLivelinessCombo();
  		fillConstraintsCombos();
      setModelProperties();  		
  	}
  }

  private void fillLivelinessCombo() {
    if (cbLiveliness.getItemCount() == 0) {
      IModelReader<?,?,?,?,?,?> modelReader = modelSolver.getModelReader();
      List<String> cList = modelReader.getClassesNames();
      for (String className : cList)  
        cbLiveliness.add(className);
      List<String> aList = modelReader.getAssociationsNames();
      for (String asName : aList) 
        cbLiveliness.add(asName);
      if (cbLiveliness.getItemCount() > 0) {
        cbLiveliness.select(0);
        cmdLiveliness.setEnabled(true);
      }
    }
  }
  
  @SuppressWarnings({ "unchecked", "rawtypes" })
  private void fillConstraintsCombos() {
    cbLackSubsumptions1.removeAll();
    cbLackSubsumptions2.removeAll();
    cbLackRedundancies1.removeAll();
    cbLackRedundancies2.removeAll();
    IOclParser oclParser = modelSolver.getOclParser();
    try {
      List<String> cNames = oclParser.getModelInvariantNames(modelSolver.getModel(), modelSolver.getConstraintsDocument());
      for(String c : cNames) {
        cbLackSubsumptions1.add(c);
        cbLackSubsumptions2.add(c);
        cbLackRedundancies1.add(c);
        cbLackRedundancies2.add(c);
      }
      if (cNames.size() > 0) {
        cbLackSubsumptions1.select(0);
        cbLackSubsumptions2.select(0);
        cbLackRedundancies1.select(0);
        cbLackRedundancies2.select(0);
      }
      cmdLackOfConstraintsSubsumptions.setEnabled(cNames.size() > 0);
      cmdLackOfConstraintsRedundancies.setEnabled(cNames.size() > 0);
      cbLackSubsumptions1.setEnabled(cmdLackOfConstraintsSubsumptions.getSelection());
      cbLackSubsumptions2.setEnabled(cmdLackOfConstraintsSubsumptions.getSelection());
      cbLackRedundancies1.setEnabled(cmdLackOfConstraintsRedundancies.getSelection());
      cbLackRedundancies2.setEnabled(cmdLackOfConstraintsRedundancies.getSelection());
    } 
    catch (Exception e) {
      e.printStackTrace();
    }
  }
  
  private void createModelProperties(Composite parent) {
  	Group grpModelProperties = new Group(parent, SWT.SHADOW_IN);
  	grpModelProperties.setText("Model properties"); //$NON-NLS-1$
  	grpModelProperties.setLayout(new RowLayout(SWT.VERTICAL));
  	cmdStrongSatisfiability = new Button(grpModelProperties, SWT.CHECK);
  	cmdStrongSatisfiability.setText("Strong Satisfiability"); //$NON-NLS-1$
  	cmdStrongSatisfiability.addSelectionListener(
        new SelectionAdapter() {
          public void widgetSelected(SelectionEvent event) {
            setModelProperties();
          }
        }
    );    
  	cmdWeakSatisfiability = new Button(grpModelProperties, SWT.CHECK);
  	cmdWeakSatisfiability.setText("Weak Satisfiability"); //$NON-NLS-1$
  	cmdWeakSatisfiability.addSelectionListener(
        new SelectionAdapter() {
          public void widgetSelected(SelectionEvent event) {
            setModelProperties();
          }
        }
    );    
  	Composite c = new Composite(grpModelProperties, SWT.NONE);
  	c.setLayout(new RowLayout(SWT.HORIZONTAL));
  	cmdLiveliness = new Button(c, SWT.CHECK);
    cmdLiveliness.setEnabled(false);
  	cmdLiveliness.setText("Liveliness"); //$NON-NLS-1$
    cmdLiveliness.addSelectionListener(
        new SelectionAdapter() {
          public void widgetSelected(SelectionEvent event) {
            cbLiveliness.setEnabled(cmdLiveliness.getSelection());
            setModelProperties();
          }
        }
    );    
  	cbLiveliness = new Combo(c, SWT.DROP_DOWN);
  	cbLiveliness.setEnabled(false);
  	cbLiveliness.addSelectionListener(
        new SelectionAdapter() {
          public void widgetSelected(SelectionEvent event) {
            setModelProperties();
          }
        }
    );    
  	grpModelProperties.pack();
  }
  
  private void createConstraintsProperties(Composite parent) {
    Group grpConstraintsProperties = new Group(parent, SWT.SHADOW_IN);
    grpConstraintsProperties.setText("Constraints properties"); //$NON-NLS-1$
    grpConstraintsProperties.setLayout(new RowLayout(SWT.VERTICAL));
    cmdLackOfConstraintsSubsumptions = new Button(grpConstraintsProperties, SWT.CHECK);
    cmdLackOfConstraintsSubsumptions.setText("Lack Of Constraints Subsumptions"); //$NON-NLS-1$
    cmdLackOfConstraintsSubsumptions.addSelectionListener(
        new SelectionAdapter() {
          public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
            cbLackSubsumptions1.setEnabled(cbLackSubsumptions1.getItemCount() > 0 && cmdLackOfConstraintsSubsumptions.getSelection());
            cbLackSubsumptions2.setEnabled(cbLackSubsumptions2.getItemCount() > 0 && cmdLackOfConstraintsSubsumptions.getSelection());
            setModelProperties();
          }
        }
    );    
    Composite c1 = new Composite(grpConstraintsProperties, SWT.NONE);
    c1.setLayout(new RowLayout(SWT.HORIZONTAL));
    cbLackSubsumptions1 = new Combo(c1, SWT.DROP_DOWN);
    cbLackSubsumptions1.setEnabled(false);
    cbLackSubsumptions1.addSelectionListener(
        new SelectionAdapter() {
          public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
            setModelProperties();
          }
        }
    );
    cbLackSubsumptions2 = new Combo(c1, SWT.DROP_DOWN);   
    cbLackSubsumptions2.setEnabled(false);
    cbLackSubsumptions2.addSelectionListener(
        new SelectionAdapter() {
          public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
            setModelProperties();
          }
        }
    );
    cmdLackOfConstraintsRedundancies = new Button(grpConstraintsProperties, SWT.CHECK);   
    cmdLackOfConstraintsRedundancies.setText("Lack Of Constraints Redundancies"); //$NON-NLS-1$
    cmdLackOfConstraintsRedundancies.addSelectionListener(
        new SelectionAdapter() {
          public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
            cbLackRedundancies1.setEnabled(cbLackRedundancies1.getItemCount() > 0 && cmdLackOfConstraintsRedundancies.getSelection());
            cbLackRedundancies2.setEnabled(cbLackRedundancies2.getItemCount() > 0 && cmdLackOfConstraintsRedundancies.getSelection());
            setModelProperties();
          }
        }
    );
    Composite c2 = new Composite(grpConstraintsProperties, SWT.NONE);
    c2.setLayout(new RowLayout(SWT.HORIZONTAL));
    cbLackRedundancies1 = new Combo(c2, SWT.DROP_DOWN);
    cbLackRedundancies1.setEnabled(false);
    cbLackRedundancies1.addSelectionListener(
        new SelectionAdapter() {
          public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
            setModelProperties();
          }
        }
    );    
    cbLackRedundancies2 = new Combo(c2, SWT.DROP_DOWN);
    cbLackRedundancies2.setEnabled(false);    
    cbLackRedundancies2.addSelectionListener(
        new SelectionAdapter() {
          public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
            setModelProperties();
          }
        }
    );    
  }
  
  public void createControl(Composite parent) {
  	Composite container = new Composite(parent, SWT.NONE);
  	container.setLayout(new RowLayout(SWT.HORIZONTAL));
  	setControl(container);
  	createModelProperties(container);
  	createConstraintsProperties(container);
  } 
}
