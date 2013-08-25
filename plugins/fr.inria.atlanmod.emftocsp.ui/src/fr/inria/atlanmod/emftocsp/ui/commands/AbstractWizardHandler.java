package fr.inria.atlanmod.emftocsp.ui.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.resources.IFile;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.PlatformUI;
import com.parctechnologies.eclipse.CompoundTerm;

import fr.inria.atlanmod.emftocsp.ICspSolver;
import fr.inria.atlanmod.emftocsp.IModelToCspSolver;
import fr.inria.atlanmod.emftocsp.IModelToCspSolverFactory;
import fr.inria.atlanmod.emftocsp.eclipsecs.EclipseSolver;
import fr.inria.atlanmod.emftocsp.emf.impl.EmfModelToCspSolverFactory;
import fr.inria.atlanmod.emftocsp.emftoecl.EmfToEclCodeGenerator;
import fr.inria.atlanmod.emftocsp.impl.FileLogger;
import fr.inria.atlanmod.emftocsp.ui.wizards.impl.ModelSelectedWizard;
import fr.inria.atlanmod.emftocsp.ui.wizards.impl.ValidationWizard;

public abstract class AbstractWizardHandler extends AbstractHandler implements IHandler {

	/**
	 * Returns the path of GraphViz from the UIPlugin
	 * Example :
	 * 	===> return Activator.getDefault().getPreferenceStore()
	 * 			.getString("GraphvizPath"); //$NON-NLS-1$ 
	 * @return {@link String} 
	 */

	protected abstract String getGVizPathFromPreferenceStore();

	/**
	 * Returns the path of GraphViz from the UIPlugin
	 * Example :
	 * 	===> return Activator.getDefault().getPreferenceStore()
	 * 			.getString("EclipsePath"); //$NON-NLS-1$ 
	 * @return {@link String} 
	 */
	protected abstract String getEclipsePathFromPreferenceStore();

	/**
	 * switch case to see if the extension is handled 
	 * and return the  
	 * Example :
	 * ===>	return extension.equals("ecore");
	 * @param extension {@link String}
	 * @return {@link Integer}
	 */
	protected abstract int canHandle(String extension);
	
	/**
	 * Load the resource from URI 
	 * Example for EMF models :
	 * 
	 * ===>	ResourceSet rSet = new ResourceSetImpl();
	 * 
	 * ===>	rSet.setPackageRegistry(EPackage.Registry.INSTANCE);
	 *
	 * ===>	return  rSet.getResource(modelFileURI, true);
	 * @param modelFileURI
	 * @param i 
	 * @return
	 */
	protected abstract Resource loadResource(URI modelFileURI, int i);
	
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IStructuredSelection selection = (IStructuredSelection)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
		IFile modelFile = (IFile)selection.getFirstElement();
		String eclipsePath = 
				getEclipsePathFromPreferenceStore();
		
		String graphvizPath = 
				getGVizPathFromPreferenceStore();
		
		String extension = modelFile.getFileExtension();
		if (canHandle(extension)> -1){
			URI modelFileURI = URI.createFileURI(modelFile.getRawLocation().toOSString());
			Resource resource = loadResource(modelFileURI, canHandle(extension));
			launchWizard(modelFile, eclipsePath, graphvizPath, resource);
			//launchUmlWizard(modelFile, eclipsePath, graphvizPath);
		}
		  
		return null;
	}   

	
	public void launchWizard(IFile modelFile, String eclipsePath, String graphvizPath, Resource resource) {
		    
		@SuppressWarnings("rawtypes")
		ICspSolver solver = new EclipseSolver(eclipsePath, graphvizPath);
		IModelToCspSolverFactory<Resource,CompoundTerm> modelSolverFactory = new EmfModelToCspSolverFactory();
		IModelToCspSolver<Resource,CompoundTerm> modelSolver = modelSolverFactory.getModelToCspSolver();
		modelSolver.setModelFileName(modelFile.getName());
		modelSolver.setModel(resource);
		modelSolver.setSolver(solver);
		modelSolver.setCspCodeGenerator(new EmfToEclCodeGenerator(modelSolver));
		modelSolver.setLogger(new FileLogger());
		//    modelSolver.setLogger(new FileLogger(modelFile.getRawLocation().toOSString().concat(".emftocsp.log")));
		modelSolver.getLogger().writeInfoMessage(this.getClass().toString(), "Starting EMFtoCSP GUI");
		modelSolver.getBuilder();   
		ValidationWizard wizard = new ModelSelectedWizard(modelSolver);
		WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
		dialog.open();
	}

//	public void launchUmlWizard(IFile modelFile, String eclipsePath, String graphvizPath) {
//		URI modelFileURI = URI.createFileURI(modelFile.getRawLocation().toOSString());    
//		@SuppressWarnings("rawtypes")
//		ICspSolver solver = new EclipseSolver(eclipsePath, graphvizPath);
//
//		ResourceSet rSet = new ResourceSetImpl();
//		UMLResource r = (UMLResource)rSet.getResource(modelFileURI, true);
//		IModelToCspSolverFactory<UMLResource,CompoundTerm> modelSolverFactory = new UmlModelToCspSolverFactory();
//
//		IModelToCspSolver<UMLResource,CompoundTerm> modelSolver = modelSolverFactory.getModelToCspSolver(); 
//		modelSolver.setModelFileName(modelFile.getName());
//		modelSolver.setModel(r);
//		modelSolver.setSolver(solver);
//		modelSolver.setCspCodeGenerator(new UmlToEclCodeGenerator(modelSolver));
//
//		modelSolver.setLogger(new FileLogger());
//		//  modelSolver.setLogger(new FileLogger(modelFile.getRawLocation().toOSString().concat(".emftocsp.log")));
//		modelSolver.getLogger().writeInfoMessage(this.getClass().toString(), "Starting EMFtoCSP GUI");
//
//		ValidationWizard wizard = new ModelSelectedWizard(modelSolver);
//		WizardDialog dialog = new WizardDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), wizard);
//		dialog.open();
//	}
//	
	
}
