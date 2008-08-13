package ch.archilogic.solver;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.LineAttributes;
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
import ch.archilogic.solver.intersection.IObject;

public class SimpleRandomPatternSolver implements Solver {
	private SolverState status = SolverState.INITIALIZING;
	private ObjectGraph objGraph = null;

	private static String refObjPath = "file:c:\\tmp\\loadme.obj";
	private ObjectDef objReference;
	private ObjectDef objBoundingBox;
	private ObjectDef objEnvelope;
	private ObjectDef objFaceEvaluated;
	
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
		edgeObj.addAppearance(createAppearance(Color.yellow, 1));

		// evaluation vizualizer
		objFaceEvaluated = new ObjectDef();
		objFaceEvaluated.addAppearance(createAppearance(Color.green, 2));		
		
		// create envelop object
		Logger.info("creating new envelop");	
		objEnvelope = new ObjectDef();
		objEnvelope.addAppearance(createAppearance(Color.red, 2));		
	
		// add to scene graph
		if (objReference != null) {			
			objGraph.addChild(objFaceEvaluated);
			objGraph.addChild(objEnvelope);
			objGraph.addChild(objReference);				
//			objGraph.addChild(edgeObj);			
			objGraph.addChild(box);
		}
	}
	
	private Appearance createAppearance(Color col, int lineWidth) {
		Appearance app = new Appearance();
		ColoringAttributes catt = new ColoringAttributes();
		catt.setColor(new Color3f(col));
		LineAttributes latt = new LineAttributes();
		latt.setLineWidth(lineWidth);
		app.setColoringAttributes(catt);
		app.setLineAttributes(latt);
		
		return app;
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
				
		Logger.info("head banging..");	

		List<Vector3D> f1 = createFirstSegment(refFace, refIndex);
		objEnvelope.createFace(f1);
				
		status = SolverState.IDLE;
	}

	private List<Vector3D> createFirstSegment(Face face, int idx) throws FaceException {
		Vector3D p0 = new Vector3D(face.getVertices().get(idx));
		Vector3D refEdgeVec = face.getEdgeVec(idx);

		Logger.setDebugVerbose(false);
		IObject p1 = objReference.catwalk(p0, refEdgeVec, 0.5, null, face);

		List<Vector3D> l = new ArrayList<Vector3D>();
		l.add(p0);
		l.add(p1.point);

		// visualize visited faces
		for (Face f : p1.visited) {
			objFaceEvaluated.addFace(f);
		}
		
		// compute downwards vector
		Vector3D v = Vector3D.cross(face.getFaceNormal(), refEdgeVec.normalize()).normalize();
		
		IObject p2 = objReference.catwalk(p1.point, v, 1.5, null, p1.face);
		l.add(p2.point);

		// visualize visited faces
		for (Face f : p2.visited) {
			objFaceEvaluated.addFace(f);
		}
		
//		Logger.setDebugVerbose(true);
		IObject p3 = objReference.catwalk(p0, v, 1.5, null, face);
		l.add(p3.point);

		// visualize visited faces
		for (Face f : p3.visited) {
			objFaceEvaluated.addFace(f);
		}

		return l;
	}

	public void export(Exporter exporter, String filename) {
		exporter.write(filename, objBoundingBox);
	}
}
