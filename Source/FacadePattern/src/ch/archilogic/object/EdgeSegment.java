package ch.archilogic.object;

import ch.archilogic.math.geom.Line;
import ch.archilogic.math.vector.Vector3D;

public class EdgeSegment {
	private Face face;
	private int sideIdx;
	private Line line;
	
	public EdgeSegment(Face face, int sideIdx) {
		this.face = face;
		this.sideIdx = sideIdx;
		this.line = face.getSideLine(sideIdx);
	}
	
	public Face getFace() {
		return face;
	}

	public void setFace(Face face) {
		this.face = face;
	}

	public int getSideIdx() {
		return sideIdx;
	}

	public void setSideIdx(int sideIdx) {
		this.sideIdx = sideIdx;
	}

	public Line getLine() {
		return line;
	}

	public void setLine(Line line) {
		this.line = line;
	}
		
	public Vector3D getStartPoint() {
		if (line == null) {
			return null;
		}		
		return line.getPoint(0);
	}

	public Vector3D getEndPoint() {
		if (line == null) {
			return null;
		}		
		return line.getPoint(1);
	}
	
	public Face createFace() {
		Face f = new Face();
		f.addVertice(getStartPoint());
		f.addVertice(getEndPoint());
		return f;
	}
}
