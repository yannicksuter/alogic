package ch.archilogic.solver;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Shape3D;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;

import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;

import ch.archilogic.export.Exporter;
import ch.archilogic.log.Logger;
import ch.archilogic.math.vector.Vector3D;
import ch.archilogic.object.Face;
import ch.archilogic.object.ObjectDef;
import ch.archilogic.object.ObjectGraph;
import ch.archilogic.object.geom.BBoxObj;
//import ch.archilogic.object.geom.ModelObj;
import ch.archilogic.object.geom.RefModelObj;
import ch.archilogic.object.helper.BoxHelper;
import ch.archilogic.object.helper.ObjHelper;
import ch.archilogic.runtime.exception.FaceException;
import ch.archilogic.solver.intersection.IFace;

public class SimpleRandomPatternSolver implements Solver {
	private SolverState status = SolverState.INITIALIZING;
	private ObjectGraph objGraph = null;

	private static String refObjPath = "file:c:\\tmp\\loadme.obj";
	private ObjectDef objReference;
	private ObjectDef objBoundingBox;
	private ObjectDef objEnvelope;
	
	public ObjectDef getObjEnvelope() {
		return objBoundingBox;
	}

	public String getDescription() {
		return String.format("Simple Random Pattvern Solver");
	}

	public ObjectGraph getObjectGraph() {
		return objGraph;
	}
		
	public SolverState getStatus() {
		return status;
	}

	public void addReference(ObjectFile obj) {
	}
	
	@SuppressWarnings("unchecked")
	public void initialize() throws FaceException {
		objGraph = new ObjectGraph();

//		ModelObj object = null;
		
		Logger.info("load reference object");
		BBoxObj box = new BBoxObj(BoxHelper.FRONT|BoxHelper.BACK|BoxHelper.LEFT|BoxHelper.RIGHT);
		Scene s = null;
		try {
			try {
				Point3d lower = new Point3d(); 
				Point3d upper = new Point3d();
				s = ObjHelper.loadRefObject(refObjPath);
				Hashtable<String,Shape3D> table = s.getNamedObjects();
				for (String key : table.keySet()) {
					Shape3D o = table.get(key);

					if (o.getBounds() instanceof javax.media.j3d.BoundingBox) {
						javax.media.j3d.BoundingBox b = (javax.media.j3d.BoundingBox) o.getBounds();
						b.getUpper(upper);
						b.getLower(lower);
						
						box.setUpper(new Vector3D(upper.getX(), upper.getY(), upper.getZ()));
						box.setLower(new Vector3D(lower.getX(), lower.getY(), lower.getZ()));
					}
					// create new model to be shown
//					object = new ModelObj((Shape3D)o.cloneTree());
					objReference = new RefModelObj((Shape3D)o.cloneTree());
					objBoundingBox = box;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			box.create();
		} catch (FaceException e) {
			e.printStackTrace();
		}
		
		// initialize reference object
		Logger.info("create face normals");
		objReference.createNormals();

		// create edge object
		Logger.info("neighbour & edges detection (can take some seconds)");
		objReference.detectEdges();
		
		// visualize edges
		ObjectDef edgeObj = new ObjectDef();		
		for (Face f : objReference.getFaces()) {
			if (f.hasEdges()) {
				edgeObj.addFace(f);
			}
		}

		Appearance app = new Appearance();
		ColoringAttributes catt = new ColoringAttributes();
		catt.setColor(new Color3f(Color.yellow));
		app.setColoringAttributes(catt);		
		edgeObj.addAppearance(app);
	
		Logger.info("creating new envelop");	
		objEnvelope = new ObjectDef();
		app = new Appearance();
		catt = new ColoringAttributes();
		catt.setColor(new Color3f(Color.red));
		app.setColoringAttributes(catt);		
		objEnvelope.addAppearance(app);		
	
		// add to scene graph
		if (objReference != null) {			
//			objGraph.addChild(object);

			objGraph.addChild(objReference);					
			objGraph.addChild(edgeObj);
			objGraph.addChild(objEnvelope);
			
			objGraph.addChild(box);
		}
	}
	
	public void think() throws FaceException {
		status = SolverState.THINKING;
		
		Logger.info("searching for the reference point, normal and edge");
		Face refFace = null;
		int refIndex = -1;
		for (Face face : objReference.getFaces()) {
			if (face.hasEdges()) {
				refIndex = face.getEdge(0);
				refFace = face;
				break;
			}
		}
				
//		List<Point3f> f1 = createFirstSegment(refPoint, refNormal, refEdgeVec);
//		objEnvelope.createFace(f1);

		Logger.err("head banging..");	

		List<Vector3D> f1 = createFirstSegment(refFace, refIndex);
		objEnvelope.createFace(f1);
		
		
//		List<Point3f> f2 = new ArrayList<Point3f>();
//		f2.add(new Point3f((float)refPoint.getX(), (float)refPoint.getY(), (float)refPoint.getZ()));
//		f2.add(new Point3f((float)refPointEnd.point.getX(), (float)refPointEnd.point.getY(), (float)refPointEnd.point.getZ()));		
//		objEnvelope.createFace(f2);
		
		status = SolverState.IDLE;
	}

//	private List<Point3f> createFirstSegment(Vector3D refPoint, Vector3D refNormal, Vector3D refEdgeVec) {
//		Vector3D v = Vector3D.cross(refNormal, refEdgeVec.normalize()).normalize();
//		
//		float size = 0.1f;
//		v = v.mult(size);
//		
//		List<Point3f> l = new ArrayList<Point3f>();
//		// 1.
//		l.add(new Point3f((float)refPoint.getX(), (float)refPoint.getY(), (float)refPoint.getZ()));
//		// 2.
//		Vector3D B = Vector3D.add(refPoint, refEdgeVec.normalize().mult(size));
//		l.add(new Point3f((float)B.getX(), (float)B.getY(), (float)B.getZ()));
//		// 3.
//		Vector3D C = Vector3D.add(B, v);
//		l.add(new Point3f((float)C.getX(), (float)C.getY(), (float)C.getZ()));
//		// 4.
//		Vector3D D = Vector3D.add(refPoint, v);
//		l.add(new Point3f((float)D.getX(), (float)D.getY(), (float)D.getZ()));
//		
//		return l;
//	}

	private List<Vector3D> createFirstSegment(Face face, int idx) {
		Vector3D refPoint = new Vector3D(face.getVertices().get(idx));
		Vector3D refEdgeVec = face.getEdgeVec(idx);

		IFace refPointEnd = objReference.w(refPoint, refEdgeVec, 0.5, null, face);

		List<Vector3D> l = new ArrayList<Vector3D>();
		l.add(refPoint);
		l.add(refPointEnd.point);
		
		return l;
	}

	public void export(Exporter exporter, String filename) {
		exporter.write(filename, objBoundingBox);
	}
}
