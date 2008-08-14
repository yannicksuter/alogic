package ch.archilogic.solver;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;

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
import ch.archilogic.math.geom.Line;
import ch.archilogic.math.vector.Vector3D;
import ch.archilogic.object.Edge;
import ch.archilogic.object.EdgeSegment;
import ch.archilogic.object.Face;
import ch.archilogic.object.ObjectDef;
import ch.archilogic.object.ObjectGraph;
import ch.archilogic.object.geom.BBoxObj;
//import ch.archilogic.object.geom.ModelObj;
import ch.archilogic.object.geom.RefModelObj;
import ch.archilogic.object.helper.BoxHelper;
import ch.archilogic.object.helper.ObjHelper;
import ch.archilogic.runtime.exception.FaceException;
import ch.archilogic.solver.intersection.IEdgeSegment;
import ch.archilogic.solver.intersection.IObject;

public class SimpleRandomPatternSolver implements Solver {
	private SolverState status = SolverState.INITIALIZING;
	private ObjectGraph objGraph = null;

	private static String refObjPath = "file:c:\\tmp\\loadme.obj";
	private ObjectDef objReference;
	private ObjectDef objBoundingBox;
	private ObjectDef objEnvelope;
	private ObjectDef objFaceEvaluated;
	
	private Edge edge;	
	
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
		Logger.info("face: neighbour & edges detection (can take some seconds)");
		objReference.detectEdges();
		
		// visualize edges
		Logger.info("object: edges detection");
		ObjectDef objEdge = new ObjectDef();		
		edge = new Edge();
		for (Face f : objReference.getFaces()) {
			if (f.hasEdges()) {
				int id = f.getEdge(0);
				Line l = f.getSideLine(id);
				edge.createFromObject(objReference, l.getAPoint());		
				
				// visualization
				for (EdgeSegment segment : edge.getSegmentList()) {
					objEdge.addFace(segment.createFace());
				}
				
				break;
			}
		}
		objEdge.addAppearance(createAppearance(Color.yellow, 3, LineAttributes.PATTERN_SOLID));
		objReference.addAppearance(createAppearance(Color.white, 1, LineAttributes.PATTERN_DOT));

		// evaluation visualizer
		objFaceEvaluated = new ObjectDef();
		objFaceEvaluated.addAppearance(createAppearance(Color.green, 2, LineAttributes.PATTERN_SOLID));		
		
		// create envelop object
		Logger.info("creating new envelop");	
		objEnvelope = new ObjectDef();
		objEnvelope.addAppearance(createAppearance(Color.red, 2, LineAttributes.PATTERN_SOLID));		
	
		// add to scene graph
		if (objReference != null) {			
			objGraph.addChild(objFaceEvaluated);
			objGraph.addChild(objEnvelope);
			objGraph.addChild(objReference);				
			objGraph.addChild(objEdge);			
			objGraph.addChild(box);
		}
	}
	
	private Appearance createAppearance(Color col, int lineWidth, int linePattern) {
		Appearance app = new Appearance();
		ColoringAttributes catt = new ColoringAttributes();
		catt.setColor(new Color3f(col));
		LineAttributes latt = new LineAttributes();
		latt.setLineWidth(lineWidth);
		latt.setLinePattern(linePattern);
		app.setColoringAttributes(catt);
		app.setLineAttributes(latt);
		
		return app;
	}

	public void think() throws FaceException {
		status = SolverState.THINKING;
				
		Logger.info("head banging..");
		if (edge != null) {
			int numSegments = 50;
			double edgeLen = edge.getLength() / numSegments;
			IEdgeSegment start = edge.getStartPoint();

			for (int i = 0; i<numSegments; i++) {
				IEdgeSegment s0 = edge.getPoint(start.point, edgeLen*i);
				IEdgeSegment s1 = edge.getPoint(start.point, edgeLen*(i+1));
				if (s0 != null && s1 != null) {
					Logger.debug(String.format("%d, s0 %s -> s1 %s", i, s0.point, s1.point));
					createSegment(objEnvelope, s0, s1, edgeLen);
				}
			}
		}
				
		status = SolverState.IDLE;
	}

	private void createSegment(ObjectDef obj, IEdgeSegment s0, IEdgeSegment s1, double edgeLen) throws FaceException {
		List<Vector3D> l = new ArrayList<Vector3D>();
		IObject p0, p1;

		// compute downwards vector
		Vector3D edgeVec = Vector3D.sub(s1.point, s0.point).normalize();
		Vector3D v0 = Vector3D.cross(s0.face.getFaceNormal(), edgeVec).normalize();
		Vector3D v1 = Vector3D.cross(s1.face.getFaceNormal(), edgeVec).normalize();
		
		do {
			l.clear();			
	
			// find new points
			p0 = objReference.catwalk(s0.point, v0, edgeLen, null, s0.face);
			p1 = objReference.catwalk(s1.point, v1, edgeLen, null, s1.face);
			
			if (p0.found && p1.found) {
				// create the face
				l.add(s0.point);
				l.add(s1.point);
				l.add(p1.point);
				l.add(p0.point);
				obj.createFace(l);				

				v0 = Vector3D.sub(p0.point, s0.point).normalize();
				v1 = Vector3D.sub(p1.point, s1.point).normalize();				
				s0 = new IEdgeSegment(p0.face, p0.point);
				s1 = new IEdgeSegment(p1.face, p1.point);				
			}
		} while(p0.found && p1.found);
	}

	public void export(Exporter exporter, String filename) {
		exporter.write(filename, objBoundingBox);
	}
}
