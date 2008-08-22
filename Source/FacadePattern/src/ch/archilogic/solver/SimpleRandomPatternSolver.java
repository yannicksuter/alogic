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
import ch.archilogic.object.Edge.CornerType;
import ch.archilogic.object.geom.BBoxObj;
import ch.archilogic.object.geom.PointShapeObj;
import ch.archilogic.object.geom.RefModelObj;
import ch.archilogic.object.helper.BoxHelper;
import ch.archilogic.object.helper.ObjHelper;
import ch.archilogic.runtime.exception.FaceException;
import ch.archilogic.solver.intersection.IEdgeSegment;
import ch.archilogic.solver.intersection.IObject;
import ch.archilogic.solver.intersection.IEdgeSegment.IType;

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
	private PointShapeObj objCornerPoints = new PointShapeObj();
	
	private List<Edge> edges;
	
	private boolean doThinking = true;
	private boolean doJittering = false;
	private boolean doShowLockedVertices = false;
	private boolean doShowCornersOnEdge = true;
	private boolean doShowReferenceObj = true;
	private boolean doShowEdges = true;
	private boolean doTriangulateEdge = false;
	
	// these are defaults, check below setTweakParam(int objNb) to optimize for specific objects 
	private double scale = 1.0;
	private int useEdgeId = 1;
	private Vector3D useEdgeDir = new Vector3D(0,1,0);
	private boolean considerCorner = false;
	private boolean evaluateCorner = false;
	private int findMaxNbEdges = 2;
	
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

	public double getScale() {
		return this.scale;
	}
	
	public void addReference(ObjectFile obj) {
	}	
	
	@SuppressWarnings("unchecked")
	public void initialize() throws FaceException {
		objGraph = new ObjectGraph();
		
		setTweakParam(3);

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
					
					// compute scaling factor
					this.scale = 1 / (upper.getX() - lower.getX());
					Logger.info(String.format("scaling factor: %f", scale));
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
		
		edges = objReference.computeEdges(findMaxNbEdges);		
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
			
			if (doShowReferenceObj) {
				objGraph.addChild(objReference);
			}

			if (doShowEdges) {
				objGraph.addChild(objEdge);
			}

			if (doShowCornersOnEdge) {
				objGraph.addChild(objCornerPoints);
			}
			
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

		int segNb = 0;
		double segmentLen = 0;
		
		Logger.info("thinking..");		
		if (edges != null && edges.size() > 0) {
			Edge edge = edges.get(useEdgeId);
			segmentLen = 0.9;//edge.getLength() / 160.0;
			
			Vector3D dir = null;
			IEdgeSegment s = edge.getStartPoint();
			// define general direction for propagation
			if (useEdgeDir == null) {
				dir = Vector3D.cross(s.face.getFaceNormal(), s.line.getDir()).normalize();
			} 
			else {
				dir = useEdgeDir;
			}
			
			boolean bCornerBefore = false;
			do {
				segNb++;
				if (!bCornerBefore && s.type == IEdgeSegment.IType.CORNER && edge.evaluateCorner(evaluateCorner, s, dir, false) == CornerType.OPENING) {
					Logger.debug(String.format("%d corner %s", segNb, s.point));
					s = createSegmentOnOpeningEdge(objEnvelope, s, segmentLen, dir, edge);
				} else 
				{
					bCornerBefore = false;
					IEdgeSegment n = edge.getPoint(s.point, segmentLen, considerCorner);
					if (n.type == IEdgeSegment.IType.CORNER && edge.evaluateCorner(evaluateCorner, n, dir, false) == CornerType.CLOSING) 
					{ // create a segment in a closing edge
						Logger.debug(String.format("%d corner %s : %s", segNb, n.point, CornerType.CLOSING.name()));
						IObject u0 = new IObject(s.face, s.point, true, true);
						n = createSegmentOnClosingEdge(objEnvelope, u0, n, segmentLen, dir, edge);
						if (n.type == IType.CORNER) {
							bCornerBefore = true;
						}
					} else 
					{ // normal "on line" segment creation
						takeNextPointOnEdgeThreshold(n, edge, segmentLen, dir);												
						IObject u0 = new IObject(s.face, s.point, true, true);
						IObject u1 = new IObject(n.face, n.point, true, true);
						createSegment(objEnvelope, u0, u1, segmentLen, dir);						
					}
					s = n;
				}
			} while (s.type != IEdgeSegment.IType.ENDPOINT);
		}
		
		if (doJittering ) {
			for (ObjectVector v : objEnvelope.getVertices()) {
				if (!v.isLocked() && v.getFace() != null) {
					Vector3D vRnd = Vector3D.random();
					IObject newPoint = objReference.catwalk(v, vRnd, segmentLen*0.2, null, v.getFace());
					Logger.debug(String.format("old: %s new: ", v, newPoint.point.toString()));
					v.set(newPoint.face, newPoint.point, false);
				}
			}
		}
		
		if (doTriangulateEdge) {
			Logger.info("triangulation of edge vertices");
			objEnvelope.triangulate(true);
		}
		
		if (doShowLockedVertices) {
			for (ObjectVector v : objEnvelope.getVertices()) {
				if (v.isLocked()) {
					objPoints.addPoint(v);
				}
			}
		}
	
		status = SolverState.IDLE;
	}
	
	private boolean takeNextPointOnEdgeThreshold(IEdgeSegment n, Edge edge, double segmentLen, Vector3D dir) {
		IEdgeSegment next = edge.getPoint(n.point, segmentLen, considerCorner);
		if (Vector3D.length(next.point, n.point) < (segmentLen*0.5) && next.type != IEdgeSegment.IType.ENDPOINT) {
			CornerType cornerType = edge.evaluateCorner(evaluateCorner, next, dir, false);
			if (cornerType != CornerType.CLOSING) { 
				n = next;
				return true;
			}
		}
		return false;
	}

	private IEdgeSegment createSegmentOnOpeningEdge(ObjectDef obj, IEdgeSegment s, double edgeLen, Vector3D dir, Edge edge) throws FaceException {
		IEdgeSegment nextCorner = edge.getPoint(s.point, 100000, true);
		IEdgeSegment secondPointOnEdge = null;

		Vector3D edgeDir = Vector3D.sub(nextCorner.point, s.point);
		double len = edgeDir.length() < edgeLen ? edgeDir.length() : edgeLen;
		
		if (nextCorner != null && nextCorner.type == IType.CORNER) 
		{
			List<ObjectVector> l = new ArrayList<ObjectVector>();
			secondPointOnEdge = edge.getPoint(nextCorner.point, edgeLen, true);
			takeNextPointOnEdgeThreshold(secondPointOnEdge, edge, edgeLen, dir);			
			
			IEdgeSegment point0 = nextCorner;
			IObject point1 = new IObject(secondPointOnEdge.face, secondPointOnEdge.point, true, true); 
			IObject point2 = null; 
			IEdgeSegment fRefSecond = obj.getFaceWithVertice(point1.point, 0);
			do {
				IEdgeSegment point3 = edge.getPoint(point0.point, -edgeLen, true);
				
				if (fRefSecond == null) {
					point2 = objReference.catwalk(point1.point, dir, len, null, point1.face);
				} else 
				{
					ObjectVector oVert = fRefSecond.face.getVertices().get(3);
					point2 = new IObject(oVert);
					point2.found = true;
					point2.edge = oVert.isLocked();
				}				

				l.clear();			
				l.add(new ObjectVector(point0.face, point0.point, true));
				l.add(new ObjectVector(point1.face, point1.point, point1.edge));
				l.add(new ObjectVector(point2.face, point2.point, point2.edge));
				l.add(new ObjectVector(point3.face, point3.point, true));
				obj.createFaceOV(l);				
				
				point0 = point3;
				point1 = point2;
				
				fRefSecond = obj.getFaceWithVertice(point1.point, 0);
			} while (point0.type != IEdgeSegment.IType.CORNER);

			// segment need to be finished
			IObject i = new IObject(s.face, s.point, true, true);
			createSegment(objEnvelope, i, point1, edgeLen, dir);														
		} 

		return secondPointOnEdge;
	}

	private IEdgeSegment createSegmentOnClosingEdge(ObjectDef obj, IObject point0, IEdgeSegment firstPointOnEdge, double edgeLen, Vector3D dir, Edge edge) throws FaceException {
		List<ObjectVector> l = new ArrayList<ObjectVector>();
		IEdgeSegment fRefFirst = obj.getFaceWithVertice(point0.point, 1);
		IObject point4 = null; 
		IEdgeSegment secondPointOnEdge = null;
		do {
			secondPointOnEdge = edge.getPoint(firstPointOnEdge.point, edgeLen, true);
			if (fRefFirst == null) {
				point4 = objReference.catwalk(point0.point, dir, edgeLen, null, point0.face);
			} else 
			{
				ObjectVector oVert = fRefFirst.face.getVertices().get(2);
				point4 = new IObject(oVert);
				point4.found = true;
				point4.edge = oVert.isLocked();
			}				

			l.clear();			
			l.add(new ObjectVector(point0.face, point0.point, point0.edge));
			l.add(new ObjectVector(firstPointOnEdge.face, firstPointOnEdge.point, true));
			l.add(new ObjectVector(secondPointOnEdge.face, secondPointOnEdge.point, true));
			l.add(new ObjectVector(point4.face, point4.point, point4.edge));
			obj.createFaceOV(l);				
			
			firstPointOnEdge = secondPointOnEdge;
			point0 = point4;

			fRefFirst = obj.getFaceWithVertice(point4.point, 1);
			
		} while ((secondPointOnEdge.type != IEdgeSegment.IType.ENDPOINT) && (secondPointOnEdge.type != IEdgeSegment.IType.CORNER));

		if (secondPointOnEdge.type == IEdgeSegment.IType.CORNER && edge.evaluateCorner(evaluateCorner, secondPointOnEdge, dir, false) == CornerType.OPENING) 
		{ // segment need to be finished
			IObject i = new IObject(secondPointOnEdge.face, secondPointOnEdge.point, true, true);
			createSegment(objEnvelope, point4, i, edgeLen, dir);														
		}
		
		return secondPointOnEdge;
	}

	private void createSegment(ObjectDef obj, IObject u0, IObject u1, double edgeLen, Vector3D dir) throws FaceException {
		int facenNb = 0;
		List<ObjectVector> l = new ArrayList<ObjectVector>();
		IObject p0, p1;
		
		IEdgeSegment fRefFirst = obj.getFaceWithVertice(u0.point, 1);
		IEdgeSegment fRefSecond = obj.getFaceWithVertice(u1.point, 0);
		
		// compute direction vector
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
	
	private void setTweakParam(int objNb) {
		if (objNb == 3) {
			scale = 1.0;
			useEdgeId = 1;
			useEdgeDir = new Vector3D(0,1,0);
			considerCorner = false;
			evaluateCorner = false;
			findMaxNbEdges = 2;			
		}
	}
	
	public void export(Exporter exporter, String filename) {
		exporter.write(filename, objEnvelope);
	}
}
