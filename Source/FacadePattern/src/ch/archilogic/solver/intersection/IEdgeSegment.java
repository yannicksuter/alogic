package ch.archilogic.solver.intersection;

import ch.archilogic.math.vector.Vector3D;
import ch.archilogic.object.Face;

public class IEdgeSegment {
	public enum IType {
		LINE,
		CORNER,
		STARPOINT,
		ENDPOINT
	};
	
	public Face face;
	public Vector3D point;
	public IType type;
	public double lenRemaining = 0;
	
	public IEdgeSegment(Face face, Vector3D point) {
		this.face = face;
		this.point = point;
		this.type = IType.STARPOINT;
	}
	
	public IEdgeSegment(Face face, Vector3D point, IType type) {
		this.face = face;
		this.point = point;
		this.type = type;
	}

	public void setLenRemaining(double len) {
		this.lenRemaining = len;
	}
}
