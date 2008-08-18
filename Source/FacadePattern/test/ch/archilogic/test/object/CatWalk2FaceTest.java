package ch.archilogic.test.object;

import ch.archilogic.log.Logger;
import ch.archilogic.math.vector.Vector3D;
import ch.archilogic.object.Face;
import ch.archilogic.object.ObjectDef;
import ch.archilogic.object.ObjectVector;
import ch.archilogic.runtime.exception.FaceException;
import ch.archilogic.solver.intersection.IObject;
import junit.framework.TestCase;

public class CatWalk2FaceTest extends TestCase {
	public static ObjectDef createObject1Face() throws FaceException {
		ObjectDef obj = new ObjectDef();
		
		Face f1 = new Face();
		f1.addVertice(new ObjectVector(10,0,0));
		f1.addVertice(new ObjectVector(0,0,0));
		f1.addVertice(new ObjectVector(0,10,0));
		obj.addFace(f1);

		// prepare object
		obj.detectEdges();
		obj.createNormals();
		
		return obj;
	}
	
	public static ObjectDef createObject2Face() throws FaceException {
		ObjectDef obj = new ObjectDef();
		
		Face f1 = new Face();
		f1.addVertice(new ObjectVector(10,0,0));
		f1.addVertice(new ObjectVector(0,0,0));
		f1.addVertice(new ObjectVector(0,10,0));
		obj.addFace(f1);

		Face f2 = new Face();
		f2.addVertice(new ObjectVector(10,0,0));
		f2.addVertice(new ObjectVector(0,10,0));
		f2.addVertice(new ObjectVector(10,10,0));
		obj.addFace(f2);
		
		// prepare object
		obj.detectEdges();
		obj.createNormals();
		
		return obj;
	}

	public void testIntersection1_1Face() throws FaceException{
		Logger.info("----------------------------");
		
		ObjectDef o = createObject1Face();
		Face f = o.getFace(0);
		Logger.setDebugVerbose(false);
		IObject res1 = o.catwalk(new Vector3D(0,0,0), new Vector3D(1,1,0), 8, null, f);
		Logger.info(String.format("endPoint: %s", res1.point));
		
		assertTrue(res1.point.equals(new Vector3D(5,5,0)));

		IObject res2 = o.catwalk(new Vector3D(0,0,0), new Vector3D(-1,-1,0), 8, null, f);
		Logger.info(String.format("endPoint: %s", res2.point));

		assertTrue(res2.point.equals(new Vector3D(0,0,0)));
	}
	
	public void testIntersection1_2Face() throws FaceException{
		Logger.info("----------------------------");
		
		ObjectDef o = createObject2Face();
		Face f = o.getFace(0);
		Logger.setDebugVerbose(false);
		IObject res = o.catwalk(f.getVertices().get(0), new Vector3D(0,1,0), 8, null, f);

		Logger.info(String.format("endPoint: %s", res.point));

		assertTrue(res.point.equals(new Vector3D(10,8,0)));
	}

	public void testIntersection2_2Face() throws FaceException{
		Logger.info("----------------------------");
		
		ObjectDef o = createObject2Face();
		Face f = o.getFace(0);
		Logger.setDebugVerbose(false);
		IObject res = o.catwalk(f.getVertices().get(1), new Vector3D(0,1,0), 8, null, f);

		Logger.info(String.format("endPoint: %s", res.point));

		assertTrue(res.point.equals(new Vector3D(0,8,0)));
	}

	public void testIntersection3_2Face() throws FaceException{
		Logger.info("----------------------------");
		
		ObjectDef o = createObject2Face();
		Face f = o.getFace(0);
		Logger.setDebugVerbose(false);
		IObject res = o.catwalk(new Vector3D(5,1,0), new Vector3D(0,1,0), 8, null, f);
		
		Logger.info(String.format("endPoint: %s", res.point));

		assertTrue(res.point.equals(new Vector3D(5,9,0)));
	}
}
