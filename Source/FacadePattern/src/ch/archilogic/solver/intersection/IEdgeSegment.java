package ch.archilogic.solver.intersection;

import ch.archilogic.math.vector.Vector3D;
import ch.archilogic.object.Face;

public class IEdgeSegment {
	public Face endFace;
	public Vector3D endPoint;
	
	public IEdgeSegment(Face face, Vector3D point) {
		this.endFace = face;
		this.endPoint = point;
	}
}
