package ch.archilogic.solver.intersection;

import ch.archilogic.math.vector.Vector3D;
import ch.archilogic.object.Face;

public class IFace {
	public Face face;
	public Vector3D point;
	public Vector3D dir;
	
	public IFace(Face face) {
		this.face = face;
	}
}
