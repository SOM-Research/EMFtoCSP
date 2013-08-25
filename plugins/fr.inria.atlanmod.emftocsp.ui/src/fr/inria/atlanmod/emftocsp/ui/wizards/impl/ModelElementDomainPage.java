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
import java.util.Map;

import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerEditor;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationStrategy;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.FocusCellOwnerDrawHighlighter;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.TreeViewerEditor;
import org.eclipse.jface.viewers.TreeViewerFocusCellManager;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.uml2.uml.Association;
import org.eclipse.uml2.uml.Class;
import org.eclipse.uml2.uml.Operation;
import org.eclipse.uml2.uml.Package;
import org.eclipse.uml2.uml.Property;
import org.eclipse.uml2.uml.resource.UMLResource;

import fr.inria.atlanmod.emftocsp.IModelReader;
import fr.inria.atlanmod.emftocsp.IModelToCspSolver;
import fr.inria.atlanmod.emftocsp.emf.impl.EAssociation;
import fr.inria.atlanmod.emftocsp.ui.messages.Messages;

/**
 * @author <a href="mailto:carlos.gonzalez@inria.fr">Carlos A. González</a>
 *
 */
public class ModelElementDomainPage extends WizardPage {
        static class StringAttributeContainer {
                public enum Kind { LENGTH, DOMAIN };
                public EAttribute attribute;
                public Kind kind;
                public StringAttributeContainer(EAttribute a, Kind k) {
                        attribute = a;
                        kind = k;
                }
                
                public String getKey() { 
                        String base = attribute.getEContainingClass().getName() + "." + attribute.getName();
                        if (kind == Kind.LENGTH) {
                                return base + ".length";
                        }
                        if (kind == Kind.DOMAIN) {
                                return base + ".domain";
                        }
                        throw new RuntimeException();
                }
        }
        
        
  private final static int CLASS = 1;
  private final static int ASSOCIATION = 2;
  private TreeViewer treeViewer;
  private Map<String, String> modelElementsDomain;
  private IModelToCspSolver<?,?> modelSolver;
  
  private class StructuralNode {
    private int nodeType;
    IModelToCspSolver<?,?> modelSolver;
        
    public StructuralNode(int nodeType, IModelToCspSolver<?,?> modelSolver) {
      this.nodeType = nodeType;
      this.modelSolver = modelSolver;
    }
    
    public int getNodeType() {
      return nodeType;
    }

    public Object getParent() {
      return modelSolver.getModel(); 
    }
    
    public Object[] getChildren() {
      IModelReader<?, ?, ?, ?, ?, ?> modelReader =  modelSolver.getModelReader();     
      if (nodeType == CLASS)
        return modelReader.getClasses().toArray();
      return modelReader.getAssociations().toArray();           
    }
    
    public boolean hasChildren() {
      IModelReader<?, ?, ?, ?, ?, ?> modelReader =  modelSolver.getModelReader();     
      if (nodeType == CLASS)
        return modelReader.getClasses().size() != 0;
      return modelReader.getAssociations().size() != 0;
    }
  }
        
  private class ModelTreeContentProvider implements ITreeContentProvider {
    private StructuralNode classContainerNode;
    private StructuralNode associationContainerNode;
    private ArrayList<StructuralNode> structuralNodes;
    IModelToCspSolver<?,?> modelSolver;
    
    public ModelTreeContentProvider(IModelToCspSolver<?,?> modelSolver) {
      classContainerNode = new StructuralNode(CLASS, modelSolver);
      associationContainerNode = new StructuralNode(ASSOCIATION, modelSolver);
      structuralNodes = new ArrayList<StructuralNode>();
      structuralNodes.add(classContainerNode);
      structuralNodes.add(associationContainerNode);
      this.modelSolver = modelSolver;
    }
    
    public void dispose() {
    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    }

    public Object[] getElements(Object inputElement) {
      return getChildren(inputElement);
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public Object[] getChildren(Object parentElement) {
      if (parentElement instanceof Resource || parentElement instanceof UMLResource)
        return structuralNodes.toArray();
      if (parentElement instanceof StructuralNode)
        return ((StructuralNode)parentElement).getChildren();
      if (parentElement instanceof Class) {
        IModelReader modelReader = modelSolver.getModelReader();     
        return modelReader.getClassAttributes(parentElement).toArray();
      }
      if (parentElement instanceof EClass) {
          EClass c = (EClass) parentElement;
          IModelReader modelReader = modelSolver.getModelReader();     
          List<Object> res = new ArrayList<Object>();
          for (EAttribute a : c.getEAttributes()) {
                  if (a.getEType().getName().endsWith("String")) {
                          res.add(new StringAttributeContainer(a, StringAttributeContainer.Kind.LENGTH));
                          res.add(new StringAttributeContainer(a, StringAttributeContainer.Kind.DOMAIN));
                  } else {
                          res.add(a);
                  }
          }
          return res.toArray();
      }
      return null;
    }

    public Object getParent(Object element) {
      if (element instanceof Resource)
        return null;
      if (element instanceof StructuralNode)
        return ((StructuralNode)element).getParent();
      if (element instanceof EClass || element instanceof Class) 
        return classContainerNode;
      if (element instanceof String)
        return associationContainerNode;
      return null;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public boolean hasChildren(Object element) {
      if (element instanceof Resource || element instanceof UMLResource)
        return true;
      if (element instanceof StructuralNode)
        return ((StructuralNode)element).hasChildren();
      if (element instanceof Class || element instanceof EClass) {
        IModelReader modelReader = modelSolver.getModelReader();     
        return modelReader.getClassAttributes(element).size() > 0;
      }
      return false;
    }   
  }

  public ModelElementDomainPage(String pageName, IModelToCspSolver<?,?> modelSolver) {
        super(pageName);
        setTitle(pageName);
        String description = Messages.ModelElementDomainPage_0;
        setDescription(description);
    this.modelSolver = modelSolver;
  }     
  
  @SuppressWarnings("unchecked")
  private void InitializeModelElementsDomain(Resource r) {
    modelElementsDomain = modelSolver.getModelElementsDomain();
    if (modelElementsDomain != null)
      return;
    modelElementsDomain = new HashMap<String, String>();
    
    if (r instanceof UMLResource) {
      IModelReader<UMLResource, Package, Class, Association, Property, Operation> modelReader = (IModelReader<UMLResource, Package, Class, Association, Property, Operation>)modelSolver.getModelReader();    
      List<Class> cList = (List<Class>) modelReader.getClasses();
      for (Class c : cList) {
        modelElementsDomain.put(c.getPackage().getName() + "." + c.getName(), "0..5"); //$NON-NLS-1$ //$NON-NLS-2$
        List<Property> atList = modelReader.getClassAttributes(c);
        for (Property at : atList) 
          if (at.getType() != null && "Boolean".equals(at.getType().getName())) //$NON-NLS-1$
            modelElementsDomain.put(at.getClass_().getName() + "." + at.getName(), "0..1"); //$NON-NLS-1$ //$NON-NLS-2$
          else
            modelElementsDomain.put(at.getClass_().getName() + "." + at.getName(), "[1,10,20]"); //$NON-NLS-1$ //$NON-NLS-2$
      }
      List<String> asNames = modelReader.getAssociationsNames();
      for (String asName : asNames) 
        modelElementsDomain.put(asName, "0..10"); //$NON-NLS-1$
    }
    else {
      IModelReader<Resource, EPackage, EClass, EAssociation, EAttribute, EOperation> modelReader = (IModelReader<Resource, EPackage, EClass, EAssociation, EAttribute, EOperation>)modelSolver.getModelReader();    
      List<EClass> cList = modelReader.getClasses();
      for (EClass c : cList) {
        modelElementsDomain.put(c.getEPackage().getName() + "." + c.getName(), "0..5"); //$NON-NLS-1$ //$NON-NLS-2$
        List<EAttribute> atList = modelReader.getClassAttributes(c);
        for (EAttribute at : atList){ 
        	String name = at.getEAttributeType().getName();
          if (at.getEAttributeType().getName().equalsIgnoreCase("boolean") || at.getEAttributeType().getName().equalsIgnoreCase("boolean")  ) //$NON-NLS-1$
            modelElementsDomain.put(at.getEContainingClass().getName() + "." + at.getName(), "0..1"); //$NON-NLS-1$ //$NON-NLS-2$
          else if (at.getEAttributeType().getName().equalsIgnoreCase("string") || at.getEAttributeType().getName().equalsIgnoreCase("estring") ) {
                  modelElementsDomain.put(at.getEContainingClass().getName() + "." + at.getName() + ".length", "0..10"); //$NON-NLS-1$ //$NON-NLS-2$
                  modelElementsDomain.put(at.getEContainingClass().getName() + "." + at.getName() + ".domain", ""); //$NON-NLS-1$ //$NON-NLS-2$
          }
          else
            modelElementsDomain.put(at.getEContainingClass().getName() + "." + at.getName(), "[1,10,20]"); //$NON-NLS-1$ //$NON-NLS-2$
        }
      }
      List<String> asNames = modelReader.getAssociationsNames();
      for (String asName : asNames)
        modelElementsDomain.put(asName, "0..10"); //$NON-NLS-1$
    } 
    modelSolver.setModelElementsDomain(modelElementsDomain);    
  }
  
  @Override
  public void setVisible(boolean visible) {
    super.setVisible(visible);
    if (visible) {
      InitializeModelElementsDomain(modelSolver.getModel() instanceof UMLResource ? (UMLResource)modelSolver.getModel() : (Resource)modelSolver.getModel());     
      treeViewer.setContentProvider(new ModelTreeContentProvider(modelSolver));
      treeViewer.setInput(modelSolver.getModel() instanceof UMLResource ? (UMLResource)modelSolver.getModel() : (Resource)modelSolver.getModel());
    }
  }     
  
  public void createControl(Composite parent) {
    Composite container = new Composite(parent, SWT.NONE);
    container.setLayout(new FormLayout());
    setControl(container);
    
    FormData formData = new FormData();
    formData.left = new FormAttachment(0, 0);   
    formData.top = new FormAttachment(0, 0);
    formData.right = new FormAttachment(100, -5);
    formData.bottom = new FormAttachment(100, 0);   
    
    treeViewer = new TreeViewer(container, SWT.BORDER | SWT.FULL_SELECTION);
    treeViewer.getControl().setLayoutData(formData);
    treeViewer.getTree().setLinesVisible(true);
    treeViewer.getTree().setHeaderVisible(true);
    TreeViewerFocusCellManager focusCellManager = new TreeViewerFocusCellManager(treeViewer, new FocusCellOwnerDrawHighlighter(treeViewer));
    
    ColumnViewerEditorActivationStrategy actSupport = new ColumnViewerEditorActivationStrategy(treeViewer) {
      protected boolean isEditorActivationEvent(ColumnViewerEditorActivationEvent event) {
        return event.eventType == ColumnViewerEditorActivationEvent.TRAVERSAL
        || event.eventType == ColumnViewerEditorActivationEvent.MOUSE_DOUBLE_CLICK_SELECTION
        || (event.eventType == ColumnViewerEditorActivationEvent.KEY_PRESSED && event.keyCode == SWT.CR)
        || event.eventType == ColumnViewerEditorActivationEvent.PROGRAMMATIC;
       }
    };    
    
    TreeViewerEditor.create(treeViewer, focusCellManager, actSupport, 
                ColumnViewerEditor.TABBING_HORIZONTAL
                | ColumnViewerEditor.TABBING_MOVE_TO_ROW_NEIGHBOR
                | ColumnViewerEditor.TABBING_VERTICAL 
                | ColumnViewerEditor.KEYBOARD_ACTIVATION);
    
    final TextCellEditor textCellEditor = new TextCellEditor(treeViewer.getTree());   
    TreeViewerColumn column = new TreeViewerColumn(treeViewer, SWT.NONE);
    column.getColumn().setWidth(200);
    column.getColumn().setMoveable(true);
    column.getColumn().setText("Model Elements");    //$NON-NLS-1$
    
    column.setLabelProvider(
      new ColumnLabelProvider() {
        @Override
        @SuppressWarnings({ "rawtypes", "unchecked" })
        public String getText(Object element) {
          if (element instanceof StructuralNode)
            return ((StructuralNode)element).getNodeType() == CLASS ? "Classes" : "Associations"; //$NON-NLS-1$ //$NON-NLS-2$
          if (element instanceof EClass)
            return ((EClass)element).getName();
          if (element instanceof Class)
            return ((Class)element).getName();
          if (element instanceof Association) {
            IModelReader modelReader = modelSolver.getModelReader();     
            return modelReader.getAssociationName(((Association)element));
          }
          if (element instanceof EAssociation) {
            IModelReader modelReader = modelSolver.getModelReader();     
            return modelReader.getAssociationName(((EAssociation)element));
          }
                if (element instanceof EAttribute){
                  EAttribute a = (EAttribute) element;
              return ((EAttribute)element).getName() + ": " + ((EAttribute)element).getEAttributeType().getName(); //$NON-NLS-1$                         
                }
                if (element instanceof StringAttributeContainer){
                        StringAttributeContainer c = (StringAttributeContainer) element;
            String base = c.attribute.getName() + ": " + c.attribute.getEAttributeType().getName(); //$NON-NLS-1$
            if (c.kind == StringAttributeContainer.Kind.LENGTH) {
                return base + " (length)";
            }
            if (c.kind == StringAttributeContainer.Kind.DOMAIN) {
                return base + " (domain)";
            }
                }
          if (element instanceof Property)
            return ((Property)element).getName() + ": " + ((Property)element).getType().getName(); //$NON-NLS-1$
          return "";         //$NON-NLS-1$
        }
        
        public Image getImage(Object element) {
          return null;
        }       
       }
    );      
    column = new TreeViewerColumn(treeViewer, SWT.NONE);
    column.getColumn().setWidth(200);
    column.getColumn().setMoveable(true);
    column.getColumn().setText("Value"); //$NON-NLS-1$
    column.setLabelProvider(
      new ColumnLabelProvider() {
        @SuppressWarnings({ "rawtypes", "unchecked" })        
        public String getText(Object element) {
          String result = ""; //$NON-NLS-1$
          if (element instanceof StringAttributeContainer)
              result = modelElementsDomain.get( ((StringAttributeContainer)element).getKey());
          if (element instanceof EAttribute)
            result = modelElementsDomain.get(((EAttribute)element).getEContainingClass().getName() + "." + ((EAttribute)element).getName()); //$NON-NLS-1$
          if (element instanceof EClass)
            result = modelElementsDomain.get(((EClass)element).getEPackage().getName() + "." + ((EClass)element).getName()); //$NON-NLS-1$
          if (element instanceof Class)
            result = modelElementsDomain.get(((Class)element).getPackage().getName() + "." + ((Class)element).getName()); //$NON-NLS-1$
          if (element instanceof Property)
            result = modelElementsDomain.get(((Property)element).getClass_().getName() + "." + ((Property)element).getName()); //$NON-NLS-1$
          if (element instanceof Association) {
            IModelReader modelReader = modelSolver.getModelReader();     
            return modelElementsDomain.get(modelReader.getAssociationName(((Association)element)));
          }
          if (element instanceof EAssociation) {
            IModelReader modelReader = modelSolver.getModelReader();     
            return modelElementsDomain.get(modelReader.getAssociationName(((EAssociation)element)));
          }
          return result != null ? result : ""; //$NON-NLS-1$
        }
      }
    );
    column.setEditingSupport(
      new EditingSupport(treeViewer) {
        
        protected boolean canEdit(Object element) {
          return !(element instanceof StructuralNode);
        }
        
        protected CellEditor getCellEditor(Object element) {
          return textCellEditor;
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })        
        protected Object getValue(Object element) {
          String result = ""; //$NON-NLS-1$
          if (element instanceof EAttribute)
            result = modelElementsDomain.get(((EAttribute)element).getEContainingClass().getName() + "." + ((EAttribute)element).getName()); //$NON-NLS-1$
          if (element instanceof StringAttributeContainer)
              result = modelElementsDomain.get( ((StringAttributeContainer)element).getKey());
          if (element instanceof EClass)
            result = modelElementsDomain.get(((EClass)element).getEPackage().getName() + "." + ((EClass)element).getName()); //$NON-NLS-1$
          if (element instanceof Class)
            result = modelElementsDomain.get(((Class)element).getPackage().getName() + "." + ((Class)element).getName()); //$NON-NLS-1$
          if (element instanceof Property)
            result = modelElementsDomain.get(((Property)element).getClass_().getName() + "." + ((Property)element).getName()); //$NON-NLS-1$
          if (element instanceof Association) {
            IModelReader modelReader = modelSolver.getModelReader();     
            return modelElementsDomain.get(modelReader.getAssociationName(((Association)element)));
          }
          if (element instanceof EAssociation) {
            IModelReader modelReader = modelSolver.getModelReader();     
            return modelElementsDomain.get(modelReader.getAssociationName(((EAssociation)element)));
          }
          return result != null ? result : ""; //$NON-NLS-1$
        }

        @SuppressWarnings({ "rawtypes", "unchecked" })
        protected void setValue(Object element, Object value) {
          String key = ""; //$NON-NLS-1$
          if (element instanceof EAttribute)
            key = ((EAttribute)element).getEContainingClass().getName() + "." + ((EAttribute)element).getName(); //$NON-NLS-1$
          if (element instanceof StringAttributeContainer)
              key =  ((StringAttributeContainer)element).getKey();
          if (element instanceof EClass)
            key = ((EClass)element).getEPackage().getName() + "." + ((EClass)element).getName(); //$NON-NLS-1$
          if (element instanceof Class)
            key = ((Class)element).getPackage().getName() + "." + ((Class)element).getName(); //$NON-NLS-1$
          if (element instanceof Property)
            key = ((Property)element).getClass_().getName() + "." + ((Property)element).getName(); //$NON-NLS-1$
          if (element instanceof Association) {
            IModelReader modelReader = modelSolver.getModelReader();     
            key = modelReader.getAssociationName(((Association)element));
          }
          if (element instanceof EAssociation) {
            IModelReader modelReader = modelSolver.getModelReader();     
            key = modelReader.getAssociationName(((EAssociation)element));
          }
          modelElementsDomain.remove(key);
          modelElementsDomain.put(key, (String)value);
          treeViewer.update(element, null);
          modelSolver.setModelElementsDomain(modelElementsDomain);
        }
      }
    );        
  }
}