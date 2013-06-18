package fr.inria.atlanmod.emftocsp;



/*
 * This interface takes as parameters a resource and a solver term that represents the result of the csp solver 
 * 
 */
public interface IModelBuilder <R, P, C, AS, AT, OP,ST> {
	
	boolean isAssoc(String s);
	void generateInstance(String path);
	void decorticateResult();
	void setSolution(ST solution);
	boolean solutionIsEmpty();


	

	}
