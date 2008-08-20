package ch.archilogic.solver.intersection;

import ch.archilogic.math.geom.Line;
import ch.archilogic.math.vector.Vector3D;
import ch.archilogic.object.Face;
import ch.archilogic.object.Edge.CornerType;

public class IEdgeSegment {
	public enum IType {
		LINE,
		CORNER,
		STARPOINT,
		ENDPOINT
	};
	
	public Face face;
	public Vector3D point;
	public Line line = null;
	public IType type;
	public double lenRemaining = 0;
	
	// information for corner evaluation
	private int prevSegmentId;
	private int curSegmentId;
	
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
	public IEdgeSegment(Face face, Vector3D point, Line line, IType type) {
		this.face = face;
		this.point = point;
		this.type = type;
		this.line = line;
	}

	public void setLenRemaining(double len) {
		this.lenRemaining = len;
	}

	public int getPrevSegmentId() {
		return prevSegmentId;
	}

	public void setPrevSegmentId(int prevSegmentId) {
		this.prevSegmentId = prevSegmentId;
	}

	public int getCurSegmentId() {
		return curSegmentId;
	}

	public void setCurSegmentId(int curSegmentId) {
		this.curSegmentId = curSegmentId;
	}
}
