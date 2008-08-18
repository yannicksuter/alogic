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
import ch.archilogic.math.geom.Line;
import ch.archilogic.math.vector.Vector3D;
import ch.archilogic.object.Edge;
import ch.archilogic.object.EdgeSegment;
import ch.archilogic.object.Face;
import ch.archilogic.object.ObjectDef;
import ch.archilogic.object.ObjectGraph;
import ch.archilogic.object.geom.BBoxObj;
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
	
	private List<Edge> edges;
	
	private boolean doThinking = true;
	
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
		objEnvelope.addAppearance(createAppearance(Color.red, 2, LineAttributes.PATTERN_SOLID));		
	
		// add to scene graph
		if (objReference != null) {			
//			objGraph.addChild(objFaceEvaluated);
			objGraph.addChild(objEnvelope);
//			objGraph.addChild(objReference);				
			objGraph.addChild(objEdge);			
			
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

		if (doThinking) {
			Logger.info("head banging..");		
			if (edges != null && edges.size() > 0) {
				int numSegments = 120;
				Edge edge = edges.get(0);
				double edgeLen = edge.getLength() / numSegments;
				IEdgeSegment start = edge.getStartPoint();
	
				for (int i = 0; i<numSegments; i++) {
//					Logger.info(String.format("segment #%d", i));
					IEdgeSegment s0 = edge.getPoint(start.point, edgeLen*i);
					IEdgeSegment s1 = edge.getPoint(start.point, edgeLen*(i+1));
					if (s0 != null && s1 != null) {
						Logger.debug(String.format("%d, s0 %s -> s1 %s", i, s0.point, s1.point));
	//					createFirstSegment(objEnvelope, s0, s1, edgeLen);						
						createSegmentJittered(objEnvelope, s0, s1, edgeLen);
//						createSegment(objEnvelope, s0, s1, edgeLen);
					}
				}
			}
		}
		
//		if (doJittering) {
//			for (Face face : objEnvelope.getFaces()) {
//				
//			}
//		}
				
		status = SolverState.IDLE;
	}

	private void createFirstSegment(ObjectDef obj, IEdgeSegment s0, IEdgeSegment s1, double edgeLen) throws FaceException {
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
	
	private void createSegment(ObjectDef obj, IEdgeSegment s0, IEdgeSegment s1, double edgeLen) throws FaceException {
		int facenNb = 0;
		List<Vector3D> l = new ArrayList<Vector3D>();
		IObject p0, p1;
		
		IEdgeSegment fRefFirst = obj.getFaceWithVertice(s0.point, 1);
		IEdgeSegment fRefSecond = obj.getFaceWithVertice(s1.point, 0);
		
		// compute downwards vector
		Vector3D edgeVec = Vector3D.sub(s1.point, s0.point).normalize();
		Vector3D v0 = Vector3D.cross(s0.face.getFaceNormal(), edgeVec).normalize();
//		Vector3D v1 = Vector3D.cross(s1.face.getFaceNormal(), edgeVec).normalize();
		
		do {
			if (fRefFirst == null) {
				p0 = objReference.catwalk(s0.point, v0, edgeLen, null, s0.face);
			} else 
			{
				p0 = new IObject(fRefFirst.face, fRefFirst.face.getVertices().get(2));
				p0.found = true;
			}
	
			if (fRefSecond == null) {
				p1 = objReference.catwalk(s1.point, v0, edgeLen, null, s1.face);
			} else 
			{
				p1 = new IObject(fRefSecond.face, fRefSecond.face.getVertices().get(3));
				p1.found = true;
			}
			
			if (p0.found && p1.found) {
				// create the face
				l.clear();			
				l.add(s0.point.copy());
				l.add(s1.point.copy());
				l.add(p1.point.copy());
				l.add(p0.point.copy());
				obj.createFace(l);				
				
//				v0 = Vector3D.sub(p0.point, s0.point).normalize();
//				v1 = Vector3D.sub(p1.point, s1.point).normalize();		
				
				s0 = new IEdgeSegment(p0.face, p0.point);
				s1 = new IEdgeSegment(p1.face, p1.point);

				fRefFirst = obj.getFaceWithVertice(p0.point, 1);
				fRefSecond = obj.getFaceWithVertice(p1.point, 0);
				
//				facenNb++;
//				if (facenNb == 2) {
//					break;
//				}
			} else {
				break;
			}
//		} while(p0.found && p1.found);
		} while(!p0.edge || !p1.edge);
	}

	private void createSegmentJittered(ObjectDef obj, IEdgeSegment s0, IEdgeSegment s1, double edgeLen) throws FaceException {
		List<Vector3D> l = new ArrayList<Vector3D>();
		IObject p0, p1;
		IObject pj0, pj1;
		
		IEdgeSegment fRefFirst = obj.getFaceWithVertice(s0.point, 1);
		IEdgeSegment fRefSecond = obj.getFaceWithVertice(s1.point, 0);
		
		// compute downwards vector
		Vector3D edgeVec = Vector3D.sub(s1.point, s0.point).normalize();
		Vector3D v0 = Vector3D.cross(s0.face.getFaceNormal(), edgeVec).normalize();
//		Vector3D v1 = Vector3D.cross(s1.face.getFaceNormal(), edgeVec).normalize();
		
		IEdgeSegment sj0 = new IEdgeSegment(s0.face, s0.point);
		IEdgeSegment sj1 = new IEdgeSegment(s1.face, s1.point);
		
		do {
			if (fRefFirst == null) {
				p0 = objReference.catwalk(s0.point, v0, edgeLen, null, s0.face);
				if (!p0.edge) {
					Vector3D vRnd = Vector3D.random();
					pj0 = objReference.catwalk(p0.point, vRnd, edgeLen*0.2, null, p0.face);
				} else {
					pj0 = p0;
				}
			} else 
			{
				p0 = new IObject(fRefFirst.face, fRefFirst.face.getVertices().get(2));
				pj0 = p0;
				p0.found = true;
			}
	
			if (fRefSecond == null) {
				p1 = objReference.catwalk(s1.point, v0, edgeLen, null, s1.face);
				if (!p1.edge) {
					Vector3D vRnd = Vector3D.random();
					pj1 = objReference.catwalk(p1.point, vRnd, edgeLen*0.2, null, p1.face);
				} else {
					pj1 = p1;
				}
			} else 
			{
				p1 = new IObject(fRefSecond.face, fRefSecond.face.getVertices().get(3));
				pj1 = p1;
				p1.found = true;
			}
			
			if (p0.found && p1.found) {
				// create the face
				l.clear();			
				l.add(sj0.point);
				l.add(sj1.point);
				l.add(pj1.point);
				l.add(pj0.point);
				obj.createFace(l);				

				v0 = Vector3D.sub(p0.point, s0.point).normalize();
//				v1 = Vector3D.sub(p1.point, s1.point).normalize();		
				
				s0 = new IEdgeSegment(p0.face, p0.point);
				s1 = new IEdgeSegment(p1.face, p1.point);
				sj0 = new IEdgeSegment(pj0.face, pj0.point);
				sj1 = new IEdgeSegment(pj1.face, pj1.point);

				fRefFirst = obj.getFaceWithVertice(p0.point, 1);
				fRefSecond = obj.getFaceWithVertice(p1.point, 0);
			} else {
				break;
			}
		} while(p0.found && p1.found);
	}
	
	public void export(Exporter exporter, String filename) {
		exporter.write(filename, objEnvelope);
	}
}
