package fr.inria.atlanmod.emftocsp.modelbuilder;

import com.parctechnologies.eclipse.CompoundTerm;


/**
 * @author abenelal
 *
 */
public class Struct {
	
	public Struct(String functor) {
		name=functor;
	}
	private String name;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

}
