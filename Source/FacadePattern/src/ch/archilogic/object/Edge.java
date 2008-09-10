package ch.archilogic.object;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import ch.archilogic.log.Logger;
import ch.archilogic.math.geom.Line;
import ch.archilogic.math.vector.Vector3D;
import ch.archilogic.object.geom.PointShapeObj;
import ch.archilogic.solver.intersection.IEdgeSegment;

public class Edge {
	private static final double MAX_ANGLE = Math.PI * (70.0 / 180.0);   
	public enum EdgeType {
		LINE, 
		CIRCULAR
	}
	
	public enum CornerType {
		UNDEFINED,
		CLOSING,
		OPENING
	}
		
	private EdgeType type;
	private List<EdgeSegment> segmentList = null;
	private List<EdgeSegment> segmentListReverse = null;
	private PointShapeObj objCornerPoints;

	public EdgeType getType() {
		return type;
	}

	public List<EdgeSegment> getSegmentList() {
		return segmentList;
	}

	public List<EdgeSegment> getSegmentListReverse() {
		return segmentListReverse;
	}

	public void createFromPoints(List<Vector3D> l, EdgeType type) {
		this.type = type;
		this.segmentList = new ArrayList<EdgeSegment>();
		
		Vector3D refStart = l.get(0);
		for (int i=1;i<l.size();i++) {			
			Line line = new Line(refStart, Vector3D.sub(l.get(i), refStart));
			this.segmentList.add( new EdgeSegment(line) );
			refStart = l.get(i);
		}
		
		if (type == EdgeType.CIRCULAR) {
			Line line = new Line(refStart, Vector3D.sub(l.get(0), refStart));
			this.segmentList.add( new EdgeSegment(line) );			
		}
	}

	public void createFromObject(ObjectDef obj, Vector3D startPoint) {
		this.type = EdgeType.LINE;
		this.segmentList = new ArrayList<EdgeSegment>();
		
		Vector3D refPoint = startPoint;
		boolean found;
		do {
			found = false;
			for (Face face : obj.getFaces()) {
				if (face.hasSidesWithNoNeighbours() && face.hasVertice(refPoint)) {
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
		
		// create reverse edge
		createReverse();
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
	
	private void createReverse() {
		this.segmentListReverse = new ArrayList<EdgeSegment>();
		for (int i=segmentList.size()-1; i>=0; i--) {
			EdgeSegment e = segmentList.get(i);
			Line line = new Line(e.getEndPoint(), Vector3D.sub(e.getStartPoint(), e.getEndPoint()));
			segmentListReverse.add( new EdgeSegment(e.getFace(), e.getSideIdx(), line) );
		}
	}

	public IEdgeSegment getPoint(Vector3D p, double edgeLen, boolean withCornerDetection) {
		if (edgeLen >= 0) {
			return getPoint(segmentList, p, edgeLen, withCornerDetection);
		} else
		{
			return getPoint(segmentListReverse, p, -edgeLen, withCornerDetection);
		}
	}

	private IEdgeSegment getPoint(List<EdgeSegment> list, Vector3D p, double edgeLen, boolean withCornerDetection) {
		// find the segment on which p is
		for (EdgeSegment s : list) {
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
						return walkNextSegment(list, list.indexOf(s)+1, edgeLen - len, withCornerDetection);
					}
				}
			}
		}
		return null;
	}
	
	private IEdgeSegment walkNextSegment(List<EdgeSegment> list, int segmentId, double edgeLen, boolean withCornerDetection) {
		segmentId = getSegmentId(segmentId);
		EdgeSegment s = list.get(segmentId);
		
		Logger.debug(String.format("Rev Seg: id=%d, s=%s e=%s", segmentId, s.getStartPoint(), s.getEndPoint()));
		if (objCornerPoints != null) {
			objCornerPoints.addPoint(new ObjectVector(s.getStartPoint(), Color.GREEN));
		}
		
		if ((withCornerDetection && checkAngleBetweenSegments(list, segmentId-1, segmentId)) || !withCornerDetection) 
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
					return walkNextSegment(list, segmentId+1, edgeLen - len, withCornerDetection);
				}
			}
		} else 
		{ // line is broken
			IEdgeSegment res = new IEdgeSegment(s.getFace(), s.getStartPoint(), IEdgeSegment.IType.CORNER);
			if (s.getStartPoint().epsilonEquals(getStartPoint().point, Vector3D.EPSILON)) {
				res.type = IEdgeSegment.IType.ENDPOINT;
			} else 
			{ // keep information for later evaluation
				res.setPrevSegmentId(getSegmentId(segmentId-1));
				res.setCurSegmentId(getSegmentId(segmentId));
			}
			res.setLenRemaining(edgeLen);			
			return res;
		}
	}

	private boolean checkAngleBetweenSegments(List<EdgeSegment> list, int firstSegmentId, int secondSegmentId) {
		EdgeSegment firstSegment = list.get(getSegmentId(firstSegmentId));
		EdgeSegment secondSegment = list.get(getSegmentId(secondSegmentId));
		
		double angle = Vector3D.angle(firstSegment.getLine().getDir(), secondSegment.getLine().getDir());
		Logger.debug(String.format("Angle between: %f", angle));
		
		if (Double.isNaN(angle) || Math.abs(angle) < MAX_ANGLE) {
			return true;
		}
		
		return false;
	}
	
	public CornerType evaluateCorner(boolean evaluate, IEdgeSegment e, Vector3D dir, boolean useReverseList) {
		if (!evaluate) {
			return CornerType.UNDEFINED;
		}
		
		EdgeSegment segment = segmentList.get(getSegmentId(e.getCurSegmentId()));
		if (useReverseList) {
			segment = segmentListReverse.get(getSegmentId(e.getCurSegmentId()));
		}

		double angle = Vector3D.angle(segment.getLine().getDir(), dir);
		if (Double.isNaN(angle) || Math.abs(angle) <= (Math.PI / 2)) {
			return CornerType.CLOSING;
		}
		
		return CornerType.OPENING;
	}

	public void setObject(PointShapeObj objCornerPoints) {
		this.objCornerPoints = objCornerPoints;		
	}
}