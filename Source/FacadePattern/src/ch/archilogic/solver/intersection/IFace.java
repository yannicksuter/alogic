package ch.archilogic.solver.intersection;

import ch.archilogic.math.vector.Vector3D;

public class IFace extends Object {
	public enum IsecType {
		ON_STARTINGCORNER,
		ON_CORNER,
		ON_EDGE,
	};
	
	public IsecType type = null;
	public int sideIdx = -1;
	public Vector3D point = null;
	
	public boolean hasOngoingIntersection() {
		return (type == IsecType.ON_EDGE || type == IsecType.ON_CORNER);
	}
	
	@Override
	public String toString() {
		return String.format("type: %s idx: %d, p: %s", type.name(), sideIdx, point);		
	}
}
