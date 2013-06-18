package fr.inria.atlanmod.emftocsp.modelbuilder;

public class Field {

	private  Object value;
	private  int index;
	public Field(Object arg, int i) {
		value =arg;
		index=i;
	}
	public Object getValue() {
		return value;
	}
	public int getIndex() {
		return index;
	}
	@Override
	public String toString() {
		return "Field [value=" + value + ", index=" + index + "type"+ value.getClass()+"]";
	}
	
}
