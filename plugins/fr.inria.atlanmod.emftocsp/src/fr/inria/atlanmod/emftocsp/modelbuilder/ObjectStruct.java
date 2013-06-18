package fr.inria.atlanmod.emftocsp.modelbuilder;

import java.util.LinkedList;
import java.util.List;

import com.parctechnologies.eclipse.CompoundTerm;


public class ObjectStruct extends Struct {
	
	private List<Field> fields;
	
	public List<Field> getFields() {
		return fields;
	}


	public void setFields(List<Field> fields) {
		this.fields = fields;
	}


	public ObjectStruct(CompoundTerm cp, String functor) {
		super(functor);
		// TODO Auto-generated constructor stub
		fields = new LinkedList<Field>();
		for (int i=1; i<= cp.arity(); i++)
			fields.add(new Field(cp.arg(i),i));
	}


	public int getOid(){
		return (Integer) fields.get(0).getValue() ;
		
	}
	@Override
	public String toString() {
		return "ObjectStruct [fields=" + fields + "]";
	}
}
