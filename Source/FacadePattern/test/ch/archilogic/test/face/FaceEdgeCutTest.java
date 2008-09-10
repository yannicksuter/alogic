package ch.archilogic.test.face;

import java.util.ArrayList;
import java.util.List;

import ch.archilogic.log.Logger;
import ch.archilogic.math.vector.Vector3D;
import ch.archilogic.object.Edge;
import ch.archilogic.object.Face;
import ch.archilogic.object.ObjectVector;
import ch.archilogic.object.ObjectVectorFlag;
import ch.archilogic.object.Edge.EdgeType;
import junit.framework.TestCase;

public class FaceEdgeCutTest extends TestCase{
	private Face createFace() {
		ObjectVector v; 
		Face f = new Face();
		
		v = new ObjectVector(new Vector3D(0,0,0)); 
		v.setFlag(ObjectVectorFlag.INSIDE, false);
		f.addVertice(v);
		v = new ObjectVector(new Vector3D(1,0,0)); 
		v.setFlag(ObjectVectorFlag.INSIDE, true);
		f.addVertice(v);
		v = new ObjectVector(new Vector3D(1,1,0)); 
		v.setFlag(ObjectVectorFlag.INSIDE, true);
		f.addVertice(v);
		v = new ObjectVector(new Vector3D(0,1,0)); 
		v.setFlag(ObjectVectorFlag.INSIDE, false);
		f.addVertice(v);
		return f;
	}
	
	private Edge createEdge() {
		Edge e = new Edge();
		List<Vector3D> l = new ArrayList<Vector3D>();
		l.add(new Vector3D(0.5,0.5,0));
		l.add(new Vector3D(1,-1,0));
		l.add(new Vector3D(2,-1,0));
		l.add(new Vector3D(2,2,0));
		l.add(new Vector3D(1,2,0));
		e.createFromPoints(l, EdgeType.CIRCULAR);
		return e;
	}
	
	public void testFaceEdgeCut() {
		Face f = createFace();
		Edge e = createEdge();
		
		Logger.setDebugVerbose(true);
		List<ObjectVector> l = f.cutEdge(e);
		for (ObjectVector v : l) {
			Logger.debug(v.toString());			
		}
	}
}
