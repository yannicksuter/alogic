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
import ch.archilogic.object.Edge;
import ch.archilogic.object.EdgeSegment;
import ch.archilogic.object.ObjectDef;
import ch.archilogic.object.ObjectGraph;
import ch.archilogic.object.ObjectVector;
import ch.archilogic.object.geom.BBoxObj;
import ch.archilogic.object.geom.PointShapeObj;
import ch.archilogic.object.geom.RefModelObj;
import ch.archilogic.object.helper.BoxHelper;
import ch.archilogic.object.helper.ObjHelper;
import ch.archilogic.runtime.exception.FaceException;
import ch.archilogic.solver.intersection.IEdgeSegment;
import ch.archilogic.solver.intersection.IObject;

public class SimpleRandomPatternSolver implements Solver {
	private SolverState status = SolverState.INITIALIZING;
	private ObjectGraph objGraph = null;

//	private static String refObjPath = "file:c:\\tmp\\loadme_simple.obj";
	private static String refObjPath = "file:c:\\tmp\\loadme.obj";
	
	private ObjectDef objReference;
	private ObjectDef objBoundingBox;
	private ObjectDef objEnvelope;
	private ObjectDef objFaceEvaluated;
	
	private PointShapeObj objPoints = new PointShapeObj();
	
	private List<Edge> edges;
	
	private boolean doThinking = true;
	private boolean doJittering = true;
	private boolean doShowLockedVertices = true;
	private boolean doTriangulateEdge = true;
	private boolean doShowLockedPoints = true;
	
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
		BBoxObj box = null;
		Scene s = null;
		try {
			Point3d lower = new Point3d(); 
			Point3d upper = new Point3d();
			s = ObjHelper.loadRefObject(refObjPath);
			Hashtable<String,Shape3D> table = s.getNamedObjects();
			for (String key : table.keySet()) {
				Shape3D o = table.get(key);					
//					ObjHelper.printInfo(o);
				if (o.getBounds() instanceof javax.media.j3d.BoundingBox) {
					javax.media.j3d.BoundingBox b = (javax.media.j3d.BoundingBox) o.getBounds();
					b.getUpper(upper);
					b.getLower(lower);
					Logger.info(b.toString());
				}
				
				// create new model to be shown
				if ( !key.equalsIgnoreCase("box") ) {
					objReference = new RefModelObj((Shape3D)o.cloneTree(), 1);
					
					// create bounding box
					box = new BBoxObj(BoxHelper.FRONT|BoxHelper.BACK|BoxHelper.LEFT|BoxHelper.RIGHT);
					box.setUpper(new Vector3D(upper.getX(), upper.getY(), upper.getZ()));
					box.setLower(new Vector3D(lower.getX(), lower.getY(), lower.getZ()));
					objBoundingBox = box;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		Logger.info(String.format("number of faces: %d", objReference.getFaces().size()));
		
		// initialize reference object
		Logger.info("create face normals");
		objReference.createNormals();

		// create edge object
		Logger.info("face: neighbour & edges detection (can take some seconds)");
		objReference.detectEdges();
		
		// compute & visualize edges
		Logger.info("object: edges detection");
		ObjectDef objEdge = new ObjectDef();		
		
		edges = objReference.computeEdges();
		for (Edge edge : edges) {
			// visualization
			for (EdgeSegment segment : edge.getSegmentList()) {
				objEdge.addFace(segment.createFace());
			}			
		}
				
		objEdge.addAppearance(createAppearance(Color.yellow, 3, LineAttributes.PATTERN_SOLID));
		objReference.addAppearance(createAppearance(Color.white, 1, LineAttributes.PATTERN_DOT));

		// evaluation visualizer
		objFaceEvaluated = new ObjectDef();
		objFaceEvaluated.addAppearance(createAppearance(Color.green, 2, LineAttributes.PATTERN_SOLID));		
		
		// create envelop object
		Logger.info("creating new envelop");	
		objEnvelope = new ObjectDef(true, true);
		objEnvelope.addAppearance(createAppearance(Color.red, 1, LineAttributes.PATTERN_SOLID));		
	
		// add to scene graph
		if (objReference != null) {			
//			objGraph.addChild(objFaceEvaluated);
			objGraph.addChild(objEnvelope);
//			objGraph.addChild(objReference);				
			objGraph.addChild(objEdge);			
			
			if (doShowLockedVertices) {
				objGraph.addChild(objPoints);
			}
			
			if (box != null){
				box.create();
				objGraph.addChild(box);
			}
		}
		
		// center the objects
		if (box != null) {
			objGraph.setTranslation(box.getCenter());
			Logger.info(String.format("translation: %s", objGraph.getTranslation()));
		} else {
			objGraph.setTranslation(new Vector3D());
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

		if (!doThinking) {
			status = SolverState.IDLE;
			return;
		}

		double edgeLen = 0;
		
		Logger.info("thinking..");		
		if (edges != null && edges.size() > 0) {
			int numSegments = 120;
			Edge edge = edges.get(0);
			edgeLen = edge.getLength() / numSegments;
			IEdgeSegment start = edge.getStartPoint();
			
			Vector3D dir = null;

			for (int i = 0; i<numSegments; i++) {
				Logger.debug(String.format("segment #%d", i));
				IEdgeSegment s0 = edge.getPoint(start.point, edgeLen*i);
				IEdgeSegment s1 = edge.getPoint(start.point, edgeLen*(i+1));
				if (s0 != null && s1 != null) {
					Logger.debug(String.format("%d, s0 %s -> s1 %s", i, s0.point, s1.point));
										
					IObject u0 = new IObject(s0.face, s0.point, true, true);
					IObject u1 = new IObject(s1.face, s1.point, true, true);
					
					// define general direction for propagation
					if (i == 0 && edges.size() != 1) {
						Vector3D edgeVec = Vector3D.sub(u1.point, u0.point).normalize();
						dir = Vector3D.cross(u0.face.getFaceNormal(), edgeVec).normalize();				
					}					
					
					if (i == numSegments-1) {
//						Logger.setDebugVerbose(true);
						createSegment(objEnvelope, u0, u1, edgeLen, dir);						
					} else {
						createSegment(objEnvelope, u0, u1, edgeLen, dir);
					}
				}
			}
		}
		
		if (doJittering ) {
			for (ObjectVector v : objEnvelope.getVertices()) {
				if (!v.isLocked() && v.getFace() != null) {
					Vector3D vRnd = Vector3D.random();
					IObject newPoint = objReference.catwalk(v, vRnd, edgeLen*0.2, null, v.getFace());
					Logger.debug(String.format("old: %s new: ", v, newPoint.point.toString()));
					v.set(newPoint.face, newPoint.point, false);
				}
			}
		}
		
		if (doTriangulateEdge) {
			Logger.info("triangulation of edge vertices");
			objEnvelope.triangulate(true);
		}
		
		if (doShowLockedPoints) {
			for (ObjectVector v : objEnvelope.getVertices()) {
				if (v.isLocked()) {
					objPoints.addPoint(v);
				}
			}
		}
		
		status = SolverState.IDLE;
	}

	private void createSegment(ObjectDef obj, IObject u0, IObject u1, double edgeLen, Vector3D dir) throws FaceException {
		int facenNb = 0;
		List<ObjectVector> l = new ArrayList<ObjectVector>();
		IObject p0, p1;
		
		IEdgeSegment fRefFirst = obj.getFaceWithVertice(u0.point, 1);
		IEdgeSegment fRefSecond = obj.getFaceWithVertice(u1.point, 0);
		
		// compute downwards vector
		if (dir == null) {
			Vector3D edgeVec = Vector3D.sub(u1.point, u0.point).normalize();
			dir = Vector3D.cross(u0.face.getFaceNormal(), edgeVec).normalize();
		}
		
		do {
			if (fRefFirst == null) {
				p0 = objReference.catwalk(u0.point, dir, edgeLen, null, u0.face);
			} else 
			{
				ObjectVector oVert = fRefFirst.face.getVertices().get(2);
				p0 = new IObject(oVert);
				p0.found = true;
				p0.edge = oVert.isLocked();
			}
	
			if (fRefSecond == null) {
				p1 = objReference.catwalk(u1.point, dir, edgeLen, null, u1.face);
			} else 
			{
				ObjectVector oVert = fRefSecond.face.getVertices().get(3);
				p1 = new IObject(oVert);
				p1.found = true;
				p1.edge = oVert.isLocked();
			}
			
			// create the face
			l.clear();			
			l.add(new ObjectVector(u0.face, u0.point, u0.edge));
			l.add(new ObjectVector(u1.face, u1.point, u1.edge));
			l.add(new ObjectVector(p1.face, p1.point, p1.edge));
			l.add(new ObjectVector(p0.face, p0.point, p0.edge));
			obj.createFaceOV(l);				
				
			u0 = p0;
			u1 = p1;

			fRefFirst = obj.getFaceWithVertice(p0.point, 1);
			fRefSecond = obj.getFaceWithVertice(p1.point, 0);
			
			facenNb++;
			if (facenNb == 10)
				break;
			
			if (!p0.found && !p1.found) {
				break;
			}		
		} while(! (p0.edge && p1.edge) );
	}
	
	public void export(Exporter exporter, String filename) {
		exporter.write(filename, objEnvelope);
	}
}
