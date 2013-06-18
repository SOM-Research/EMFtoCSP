package fr.inria.atlanmod.emftocsp.modelbuilder;

import com.parctechnologies.eclipse.CompoundTerm;

public class AssocStruct extends Struct {

	public AssocStruct(CompoundTerm cp, String functor) {
		super(functor);
		// TODO Auto-generated constructor stub
		srcOid=(Integer) cp.arg(1);
		trgOid=(Integer) cp.arg(2);
	}
	private int srcOid;
	private int trgOid;
	@Override
	public String toString() {
		return "AssocStruct [srcOid=" + srcOid + ", trgOid=" + trgOid + "]";
	}
	public int getSrcOid() {
		return srcOid;
	}
	public void setSrcOid(int srcOid) {
		this.srcOid = srcOid;
	}
	public int getTrgOid() {
		return trgOid;
	}
	public void setTrgOid(int trgOid) {
		this.trgOid = trgOid;
	}
	
	
}
