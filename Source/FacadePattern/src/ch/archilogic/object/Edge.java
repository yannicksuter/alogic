package ch.archilogic.object;

import java.util.ArrayList;
import java.util.List;

import ch.archilogic.math.geom.Line;
import ch.archilogic.math.vector.Vector3D;
import ch.archilogic.solver.intersection.IEdgeSegment;

public class Edge {
	private static final double MAX_ANGLE = Math.PI * (20.0 / 180.0);   
	public enum EdgeType {
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
								found = false;
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

	private int getSegmentId(int segmentId) {
		segmentId = (segmentId+segmentList.size()) % segmentList.size();
		return segmentId;
	}
	
	public IEdgeSegment getStartPoint() {
		if (segmentList != null) {
			return new IEdgeSegment(segmentList.get(0).getFace(), segmentList.get(0).getStartPoint(), segmentList.get(0).getLine(), IEdgeSegment.IType.STARPOINT);
		}
		return null;
	}
	
	public IEdgeSegment getPoint(Vector3D p, double edgeLen, boolean withCornerDetection) {
		// find the segment on which p is
		for (EdgeSegment s : segmentList) {
			Line l = s.getLine(); 
			if (l.elementOf(p)) {
				double t = l.getT(p);
				if (t >= 0.0 && t < 1.0) {
					double len = (1.0 - t) * l.getLength();
					if (edgeLen <= len) 
					{ // end point on segment
						Vector3D pE = Vector3D.add(p, l.getDir().normalize().mult(edgeLen));
						return new IEdgeSegment(s.getFace(), pE, IEdgeSegment.IType.LINE); 
					} else 
					{ // walk into the next segment
						return walkNextSegment(segmentList.indexOf(s)+1, edgeLen - len, withCornerDetection);
					}
				}
			}
		}
		return null;
	}
	
	private IEdgeSegment walkNextSegment(int segmentId, double edgeLen, boolean withCornerDetection) {
		segmentId = getSegmentId(segmentId);
		EdgeSegment s = segmentList.get(segmentId);
		
		if ((withCornerDetection && checkAngleBetweenSegments(segmentId-1, segmentId)) || !withCornerDetection) 
		{ // line continuity is ok
			if (segmentId == 0) 
			{ // endpoint reached
				IEdgeSegment res = new IEdgeSegment(s.getFace(), s.getStartPoint(), IEdgeSegment.IType.ENDPOINT);
				res.setLenRemaining(edgeLen);			
				return res;				
			} else 
			{
				Line l = s.getLine();
				double len = l.getLength();
				if (edgeLen <= len) 
				{ // end point on segment
					Vector3D pE = Vector3D.add(s.getStartPoint(), l.getDir().normalize().mult(edgeLen));
					return new IEdgeSegment(s.getFace(), pE, IEdgeSegment.IType.LINE); 
				} else 
				{ // walk into the next segment
					return walkNextSegment(segmentId+1, edgeLen - len, withCornerDetection);
				}
			}
		} else 
		{ // line is broken
			IEdgeSegment res = new IEdgeSegment(s.getFace(), s.getStartPoint(), IEdgeSegment.IType.CORNER);
			if (s.getStartPoint().epsilonEquals(getStartPoint().point, Vector3D.EPSILON)) {
				res.type = IEdgeSegment.IType.ENDPOINT;
			}
			res.setLenRemaining(edgeLen);			
			return res;
		}
	}

	private boolean checkAngleBetweenSegments(int firstSegmentId, int secondSegmentId) {
		EdgeSegment firstSegment = segmentList.get(getSegmentId(firstSegmentId));
		EdgeSegment secondSegment = segmentList.get(getSegmentId(secondSegmentId));
		
		double angle = Vector3D.angle(firstSegment.getLine().getDir(), secondSegment.getLine().getDir());
		if (Double.isNaN(angle) || Math.abs(angle) < MAX_ANGLE) {
			return true;
		}
		return false;
	}
}
