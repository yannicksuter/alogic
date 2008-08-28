package ch.archilogic.solver;

import ch.archilogic.object.ObjectDef;

public class PlanarSolver {
	private ObjectDef objRef = null;

	public PlanarSolver(ObjectDef objRef) {
		this.objRef = objRef;
	}
	
	public void setObjRef(ObjectDef objRef) {
		this.objRef = objRef;
	}
	
	public void fixObject() {
		
	}
}
