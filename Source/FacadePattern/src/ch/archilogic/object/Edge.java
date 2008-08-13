package ch.archilogic.object;

import java.util.ArrayList;
import java.util.List;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;
import ch.archilogic.math.geom.Line;
import ch.archilogic.math.vector.Vector3D;

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
}
