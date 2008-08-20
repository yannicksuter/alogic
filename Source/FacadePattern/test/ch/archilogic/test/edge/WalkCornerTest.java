package ch.archilogic.test.edge;

import java.awt.Color;

import junit.framework.TestCase;
import ch.archilogic.log.Logger;
import ch.archilogic.math.vector.Vector3D;
import ch.archilogic.object.Edge;
import ch.archilogic.object.EdgeSegment;
import ch.archilogic.object.Face;
import ch.archilogic.object.ObjectDef;
import ch.archilogic.object.ObjectVector;
import ch.archilogic.object.Edge.EdgeType;
import ch.archilogic.runtime.exception.FaceException;
import ch.archilogic.solver.intersection.IEdgeSegment;

public class WalkCornerTest extends TestCase {
	public static ObjectDef createObject(double sidelen) throws FaceException {
		ObjectDef obj = new ObjectDef();
		
		Face f1 = new Face();
		f1.addVertice(new ObjectVector(0,0,0));
		f1.addVertice(new ObjectVector(sidelen,0,0));
		f1.addVertice(new ObjectVector(0,sidelen,0));
		obj.addFace(f1);

		Face f2 = new Face();
		f2.addVertice(new ObjectVector(sidelen,0,0));
		f2.addVertice(new ObjectVector(sidelen,sidelen,0));
		f2.addVertice(new ObjectVector(0,sidelen,0));
		obj.addFace(f2);
		
		// prepare object
		obj.detectEdges();
		obj.createNormals();
		
		return obj;
	}
	
	public void testWalkWithCornerCircularTest() throws FaceException {
		ObjectDef obj = createObject(10);
		
		Edge edge = new Edge();
		edge.createFromObject(obj, Vector3D.ZERO);
		
		assertTrue(edge.getLength() == 40);
		assertTrue(edge.getType() == Edge.EdgeType.CIRCULAR);
		
		for (EdgeSegment s : edge.getSegmentList()) {
			Logger.debug(String.format("%s -> %s", s.getStartPoint(), s.getEndPoint()));
		}
		
		double segmentLen = 3;
		IEdgeSegment s = edge.getStartPoint();
		do {
			Logger.info(String.format("%s : %s", s.type.name(), s.point));		
			s = edge.getPoint(s.point, segmentLen, true);
		} while (s.type != IEdgeSegment.IType.ENDPOINT);		
		Logger.info(String.format("%s : %s", s.type.name(), s.point));		
	}
}
