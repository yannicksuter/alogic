package ch.archilogic.test.object;

import ch.archilogic.log.Logger;
import ch.archilogic.math.vector.Vector3D;
import ch.archilogic.object.Face;
import ch.archilogic.object.ObjectDef;
import ch.archilogic.runtime.exception.FaceException;
import ch.archilogic.solver.intersection.IObject;
import junit.framework.TestCase;

public class CatWalk4FaceTest extends TestCase {
	public static ObjectDef createObject4Face(Vector3D p) throws FaceException {
		ObjectDef obj = new ObjectDef();
		
		Face f1 = new Face();
		f1.addVertice(new Vector3D(10,0,0));
		f1.addVertice(new Vector3D(0,0,0));
		f1.addVertice(new Vector3D(0,10,0));
		obj.addFace(f1);

		Face f2 = new Face();
		f2.addVertice(new Vector3D(10,0,0));
		f2.addVertice(new Vector3D(0,10,0));
		f2.addVertice(p);
		obj.addFace(f2);

		Face f3 = new Face();
		f3.addVertice(new Vector3D(10,0,0));
		f3.addVertice(p);
		f3.addVertice(new Vector3D(20,10,0));
		obj.addFace(f3);

		Face f4 = new Face();
		f4.addVertice(new Vector3D(10,0,0));
		f4.addVertice(new Vector3D(20,10,0));
		f4.addVertice(new Vector3D(20,0,0));
		obj.addFace(f4);

		// prepare object
		obj.detectEdges();
		obj.createNormals();
		
		return obj;
	}

	public void testIntersection1_4Face() throws FaceException{
		Logger.info("----------------------------");
				
		Vector3D p = new Vector3D(10,10,0);
		ObjectDef o = createObject4Face(p);
		
		Face f = o.getFace(0);
		Logger.setDebugVerbose(false);
		IObject res = o.catwalk(f.getVertices().get(0), new Vector3D(0,1,0), 8, null, f);
		
		Logger.info(String.format("endPoint: %s faceId: %d", res.point, res.face.getId()));

		assertTrue(res.point.equals(new Vector3D(10,8,0)));
	}

	public void testIntersection2_4Face() throws FaceException{
		Logger.info("----------------------------");
				
		Vector3D p = new Vector3D(11,10,0);
		ObjectDef o = createObject4Face(p);
		
		Face f = o.getFace(0);
		Logger.setDebugVerbose(false);
		IObject res = o.catwalk(f.getVertices().get(0), new Vector3D(0,1,0), 8, null, f);
		
		Logger.info(String.format("endPoint: %s faceId: %d", res.point, res.face.getId()));

		assertTrue(res.point.equals(new Vector3D(10,8,0)));
	}

	public void testIntersection3_4Face() throws FaceException{
		Logger.info("----------------------------");
				
		Vector3D p = new Vector3D(9,10,0);
		ObjectDef o = createObject4Face(p);
		
		Face f = o.getFace(0);
		Logger.setDebugVerbose(false);
		IObject res = o.catwalk(f.getVertices().get(0), new Vector3D(0,1,0), 8, null, f);
		
		Logger.info(String.format("endPoint: %s faceId: %d", res.point, res.face.getId()));

		assertTrue(res.point.equals(new Vector3D(10,8,0)));
	}
}
