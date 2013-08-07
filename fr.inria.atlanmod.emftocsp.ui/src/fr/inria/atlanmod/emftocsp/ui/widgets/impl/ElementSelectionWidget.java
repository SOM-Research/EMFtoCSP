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
package fr.inria.atlanmod.emftocsp.ui.widgets.impl;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.internal.ui.viewsupport.AppearanceAwareLabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.DrillDownComposite;

import fr.inria.atlanmod.emftocsp.ui.widgets.IElementSelectionWidget;
import fr.inria.atlanmod.emftocsp.ui.widgets.IElementSelectionWidgetListener;

/**
 * @author <a href="mailto:carlos.gonzalez@inria.fr">Carlos A. González</a>
 *
 */
public class ElementSelectionWidget extends Composite implements IElementSelectionWidget {

	class TreeContentProvider implements ITreeContentProvider {

		private Object[] NO_CHILDREN = new Object[0];
		
		public Object[] getElements(Object inputElement) {
			return getChildren(inputElement);
		}

		public Object[] getChildren(Object parentElement) {
			try {
			  if (parentElement instanceof IProject[])
				return (Object[])parentElement;
			  if (IProject.class.isInstance(parentElement))
				return ((IProject)parentElement).members();
			  if (IFolder.class.isInstance(parentElement))
				return ((IFolder)parentElement).members();				
			  return NO_CHILDREN;
			}
			catch (CoreException e) {
			  return NO_CHILDREN;
			}
		}

		public Object getParent(Object element) {
		  if (IResource.class.isInstance(element))
			return ((IResource)element).getParent();
		  return null;
		}

		public boolean hasChildren(Object element) {
			try {
			  if (IProject.class.isInstance(element))
				return ((IProject)element).members().length != 0;
			  if (IFolder.class.isInstance(element))
				  return ((IFolder)element).members().length != 0;
			}
			catch (CoreException e) {
				return false;
			}		
			return false;
		}

		public void dispose() {
		}

		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}	

	@SuppressWarnings("restriction")
	class TreeLabelProvider extends AppearanceAwareLabelProvider {

		public void addListener(ILabelProviderListener listener) {
		}

		public void dispose() {
		}

		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {
		}

		public Image getImage(Object element) {
			return super.getImage(element); //Discourage access
		}

		public String getText(Object element) {
		  if (IResource.class.isInstance(element)) 
			return ((IResource)element).getName();
		  return ""; //$NON-NLS-1$
		}
	}
	
	private TreeViewer treeViewer;
	private Button chkDisableSelection = null;
	private IStructuredSelection selection;
	private Collection<String> allowedTypes;
	private IElementSelectionWidgetListener listener;
	private boolean mandatorySelection;
	
	public ElementSelectionWidget(Composite parent, IStructuredSelection selection, Collection<String> allowedTypes, IElementSelectionWidgetListener listener) {
		super(parent, SWT.NONE);
		this.selection = selection;
		this.allowedTypes = allowedTypes;
		this.listener = listener;
		createContents();		
	}
	
	public ElementSelectionWidget(Composite parent, IStructuredSelection selection, 
            Collection<String> allowedTypes, 
            IElementSelectionWidgetListener listener,
            boolean mandatorySelection) {
		super(parent, SWT.NONE);
		this.selection = selection;
		this.allowedTypes = allowedTypes;
		this.listener = listener;
		this.mandatorySelection = mandatorySelection;
		createContents();
	}	
	
	private void createContents() {
		setLayout(new FormLayout());
		FormData formData = new FormData();
		formData.left = new FormAttachment(0, 0);		
		formData.top = new FormAttachment(0, 0);
		formData.right = new FormAttachment(100, -5);
		formData.bottom = new FormAttachment(90, 0/*-5*/);		
		
		final DrillDownComposite ddComposite = new DrillDownComposite(this, SWT.BORDER);		
		ddComposite.setLayoutData(formData);
		treeViewer = new TreeViewer(ddComposite);
		ddComposite.setChildTree(treeViewer);
		
		if (!mandatorySelection) {
			FormData formData2 = new FormData();
			formData2.left = new FormAttachment(0, 0);		
			formData2.top = new FormAttachment(90, 0);
			formData2.right = new FormAttachment(100, -5);
			formData2.bottom = new FormAttachment(100, 0/*-5*/);				
			chkDisableSelection = new Button(this, SWT.CHECK);
			chkDisableSelection.setLayoutData(formData2);
			chkDisableSelection.setText("Skip selection");
			chkDisableSelection.addSelectionListener(
				new SelectionAdapter() {
				  public void widgetSelected(org.eclipse.swt.events.SelectionEvent e) {
					ddComposite.setEnabled(!chkDisableSelection.getSelection());
					treeViewer.getControl().setEnabled(!chkDisableSelection.getSelection());
					if (!ddComposite.getEnabled())
						elementSelectionChanged(null);
					else
						elementSelectionChanged((IStructuredSelection)treeViewer.getSelection());
				  }
				}
			);
		}
		
		treeViewer.setComparator(new ViewerComparator());
		treeViewer.setUseHashlookup(true);
		treeViewer.setContentProvider(new TreeContentProvider());
		treeViewer.setLabelProvider(new TreeLabelProvider());
		treeViewer.setInput(ResourcesPlugin.getWorkspace().getRoot().getProjects());
		treeViewer.addSelectionChangedListener(
			new ISelectionChangedListener() {
				public void selectionChanged(SelectionChangedEvent event) {
					elementSelectionChanged((IStructuredSelection)event.getSelection());
				}
			}				
		);
	}
	
	private void elementSelectionChanged(IStructuredSelection selection) {
	  this.selection = selection;
	  if (listener != null)
	    listener.elementSelectionWidgetChanged(this);
	}
	
	public Object getElementSelected() {
	  if (selection != null)
	    return selection.getFirstElement();
	  return null;
	}
	
	public boolean isInSelectionMode() {
	  if (chkDisableSelection == null)
	    return true;
	  return !chkDisableSelection.getSelection();
	}
	
	public boolean isSelectedElementValid() {
		if (selection == null)
		  return false; 
		Object selectedElement = selection.getFirstElement();
		if (selectedElement instanceof IFolder)
			return allowedTypes.contains("Folder"); //$NON-NLS-1$
		if (selectedElement instanceof IFile)
			return allowedTypes.contains(((IFile)selectedElement).getFileExtension());
		return false;
	}	
	
}
