package ch.archilogic.object;

import java.util.ArrayList;
import java.util.List;

import ch.archilogic.log.Logger;
import ch.archilogic.math.geom.Line;
import ch.archilogic.math.vector.Vector3D;
import ch.archilogic.solver.intersection.IEdgeSegment;

public class Edge {
	enum EdgeType {
		LINE, 
		CIRCULAR
	}
	
	private EdgeType type;
	private List<EdgeSegment> segmentList = null;

	public EdgeType getType() {
		return type;
	}

	public List<EdgeSegment> getSegmentList() {
		return segmentList;
	}
	
	public void createFromObject(ObjectDef obj, Vector3D startPoint) {
		this.type = EdgeType.LINE;
		this.segmentList = new ArrayList<EdgeSegment>();
		
		Vector3D refPoint = startPoint;
		boolean found;
		do {
			found = false;
			for (Face face : obj.getFaces()) {
				if (face.hasEdges() && face.hasVertice(refPoint)) {
					for (int i = 0; i<face.getEdgeCount(); i++) {
						int idx = face.getEdge(i);
						Line l = face.getSideLine(idx);
						if (l.getPoint(0).equals(refPoint)) {
							refPoint = l.getPoint(1);							
							this.segmentList.add( new EdgeSegment(face, idx) );
							if ( !startPoint.equals(refPoint) ) {
								found = true;
							} else {
								this.type = EdgeType.CIRCULAR;
							}
						}
					}
				}
			}
		} while(found);
	}

	public double getLength() {
		if (segmentList != null) {
			double l = 0;
			for (EdgeSegment s : segmentList) {
				l += s.getLength();
			}
			return l;
		}
		return 0;
	}

	public IEdgeSegment getStartPoint() {
		if (segmentList != null) {
			return new IEdgeSegment(segmentList.get(0).getFace(), segmentList.get(0).getStartPoint());
		}
		return null;
	}
	
	public IEdgeSegment getPoint(Vector3D p, double edgeLen) {
		// find the segment on which p is
		for (EdgeSegment s : segmentList) {
			Line l = s.getLine(); 
			if (l.elementOf(p)) {
				double t = l.getT(p);
				if (t >= 0.0 && t <= 1.0) {
					double len = (1.0 - t) * l.getLength();
					if (edgeLen <= len) 
					{ // end point on segment
						Vector3D pE = Vector3D.add(p, l.getDir().normalize().mult(edgeLen));
						return new IEdgeSegment(s.getFace(), pE); 
					} else 
					{ // walk into the next segment
						return walkNextSegment(segmentList.indexOf(s)+1, edgeLen - len);
					}
				}
			}
		}
		return null;
	}
	
	private IEdgeSegment walkNextSegment(int segmentId, double edgeLen) {
		EdgeSegment s = segmentList.get(segmentId % segmentList.size());
		Line l = s.getLine();
		double len = l.getLength();
		if (edgeLen <= len) 
		{ // end point on segment
			Vector3D pE = Vector3D.add(s.getStartPoint(), l.getDir().normalize().mult(edgeLen));
			return new IEdgeSegment(s.getFace(), pE); 
		} else 
		{ // walk into the next segment
			return walkNextSegment(segmentId+1, edgeLen - len);
		}
	}
}
