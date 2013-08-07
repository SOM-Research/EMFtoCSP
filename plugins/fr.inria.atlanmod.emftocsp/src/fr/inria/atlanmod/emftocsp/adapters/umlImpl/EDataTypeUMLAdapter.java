package fr.inria.atlanmod.emftocsp.adapters.umlImpl;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.notify.Notification;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EOperation;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.ETypeParameter;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData.EClassifierExtendedMetaData;
import org.eclipse.emf.ecore.util.BasicExtendedMetaData.EClassifierExtendedMetaData.Holder;
import org.eclipse.uml2.uml.DataType;

import fr.inria.emftocsp.adapters.EDataTypeAdapter;

public class EDataTypeUMLAdapter extends EDataTypeAdapter<DataType> implements EClassifier {

	protected Resource owningResource;
	public EDataTypeUMLAdapter(DataType newDataType) {
		super(newDataType);
	}

	@Override
	public String getName() {
		return origEDataType.getName();
	}

	@Override
	public Class<?> getInstanceClass() {
		return origEDataType.getClass();
	}

	@Override
	public String getInstanceClassName() {
		return getInstanceClass().getSimpleName();
	}


	
}
