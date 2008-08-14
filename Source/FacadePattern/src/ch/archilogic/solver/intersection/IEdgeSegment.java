package ch.archilogic.solver.intersection;

import ch.archilogic.math.vector.Vector3D;
import ch.archilogic.object.Face;

public class IEdgeSegment {
	public Face face;
	public Vector3D point;
	
	public IEdgeSegment(Face face, Vector3D point) {
		this.face = face;
		this.point = point;
	}
}
