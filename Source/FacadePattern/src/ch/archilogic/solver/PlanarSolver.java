package ch.archilogic.solver;

import ch.archilogic.log.Logger;
import ch.archilogic.object.Face;
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
		int nbPlanarOk = 0;
		int nbPlanarFixed = 0;
		int nbTriangle = 0;
		for (Face f : objRef.getFaces()) {
			if (f.getVertices().size() <= 3) {
				nbTriangle++;
			} else
			{
				if ( !f.isPlanar() ) 
				{ // fix planarity
					nbPlanarFixed++;
				} else 
				{ // lock vertices
					nbPlanarOk++;
				}
			}			
		}
		Logger.info(String.format("planarity - triangles: %d fixed: %d ok: %d", nbTriangle, nbPlanarFixed, nbPlanarOk));
	}
}
