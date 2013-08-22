package fr.inria.atlanmod.emftocsp.adapters.umlImpl;

import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EDataType;
import org.eclipse.emf.ecore.EcorePackage;

public class EDatatypeUtil {

	public static EClassifier convertFromString (String nameLiteral){
		EDataType type = null;
		if (nameLiteral.equalsIgnoreCase("boolean"))
			type = EcorePackage.eINSTANCE.getEBoolean();
		else if (nameLiteral.equalsIgnoreCase("integer"))
			type = EcorePackage.eINSTANCE.getEInt();
		if (nameLiteral.equalsIgnoreCase("real"))
			type = EcorePackage.eINSTANCE.getEFloat();
		else if (nameLiteral.contains("unlimited"))
			type = EcorePackage.eINSTANCE.getEDouble();
		if (nameLiteral.equalsIgnoreCase("String"))
			type = EcorePackage.eINSTANCE.getEString();
		else if (nameLiteral.contains("EInt"))
			type = EcorePackage.eINSTANCE.getEInt();
		else if (nameLiteral.contains("EByte"))
			type = EcorePackage.eINSTANCE.getEByte();
		else if (nameLiteral.contains("EBigInt"))
			type = EcorePackage.eINSTANCE.getEBigInteger();
		else if (nameLiteral.contains("EBiDecimal"))
			type = EcorePackage.eINSTANCE.getEBigDecimal();
		else if (nameLiteral.contains("EString"))
			type = EcorePackage.eINSTANCE.getEString();
		else if (nameLiteral.contains("EDouble"))
			type = EcorePackage.eINSTANCE.getEBigDecimal();
		else if (nameLiteral.contains("ELong"))
			type = EcorePackage.eINSTANCE.getELong();		
		return type;
	}
}
