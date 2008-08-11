package ch.archilogic.object;

import java.util.ArrayList;
import java.util.List;

import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.LineStripArray;
import javax.media.j3d.Shape3D;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import ch.archilogic.log.Logger;
import ch.archilogic.math.geom.Isect;
import ch.archilogic.math.geom.Plane;
import ch.archilogic.math.vector.Vector3D;
import ch.archilogic.runtime.exception.FaceException;

public class ObjectDef {
	private ObjectType type;
	private List<Point3f> vertices = new ArrayList<Point3f>();
	private List<Face> faces = new ArrayList<Face>();
	private Appearance appearance = null;

	public ObjectType getType() {
		return type;
	}

	public void setType(ObjectType type) {
		this.type = type;
	}
	
	public void create() throws FaceException {
	}

	public int getVerticeNb() {
		return vertices.size();
	}
	
	public Point3f getVertice(int i) {
		return vertices.get(i);
	}
	
	public int getFaceNb() {
		return faces.size();
	}

	public Face getFace(int i) {
		return faces.get(i);
	}

	public List<Face> getFaces() {
		return faces;
	}

	public Point3f containsEqual(Point3f ref) {
		for (Point3f p : vertices) {
			if (p.equals(ref)) {
				return p;
			}
		}
		return null;
	}
	
	public int getIndexOf(Point3f ref) {
		Point3f p = containsEqual(ref);
		if (p != null) {
			return vertices.indexOf(p);
		}
		return -1;
	}

	public void addFace(Face f) throws FaceException {
		createFace(f.getVertices());
	}
	
	public void createFace(List<Point3f> pointList) throws FaceException {
		createFace(pointList, null);
	}
	
	public void createFace(List<Point3f> pointList, List<Vector3f> normalList) throws FaceException {
		if (pointList == null) {
			throw new FaceException("no points to define a face.");
		}
		
		Face face = new Face();
		for (int i=0; i< pointList.size(); i++) {
			Point3f p = pointList.get(i);

			if (containsEqual(p) == null) {
				vertices.add(new Point3f(p));
			}
			
			int index = getIndexOf(p);
			if (index > -1) {
				face.addVertice(vertices.get(index));
				face.addIndex(new Integer(index));
				if (normalList != null) {
					face.addNormal(normalList.get(i));					
				}
			} else {
				throw new FaceException("point not indexed.");
			}
		}
		
		// add face 
		faces.add(face);
	}

	public void deleteFace(Face face) throws FaceException {
		if (faces.contains(face)) {
			faces.remove(face);
		} else {
			throw new FaceException("face could not be deleted.");
		}
	}

	public void detectEdges() {
		for (Face f : faces) {
			f.detectNeighbours(faces);
		}		
	}

	public void createNormals() {
		for (Face f : faces) {
			f.createFaceNormal();
		}		
	}
	
	public void subdivide(Face face) throws FaceException {
		List<Face> newFaces = face.subdivide();
		for (Face f : newFaces) {
			createFace(f.getVertices());
		}
	}

	public Point3f walk(Point3f p, Vector3f dir, Face f) {
		Point3f pNew = new Point3f(p.x + dir.x, p.y + dir.y, p.z + dir.z);
		float dMax = Float.MAX_VALUE;
		Face nearestFace = null;
		for (Face face : getFaces()) {
			float d = 0;//face.getDistance(pNew);
			if (d < dMax) {
				dMax = d;
				nearestFace = face;
			}
		}
		
		if (nearestFace != null) {
			
		}
		
		return null;
	}
	
	public Vector3D w(Vector3D p, Vector3D dir, double l, Face previousFace, Face currentFace) {
		Plane plane = new Plane(p, dir, currentFace.getFaceNormal());
		
		int refIndex = -1;
		double refAngle = 2*Math.PI;
		Vector3D refPoint = null;
		
		for (int i=0; i<currentFace.getEdgeCount(); i++) {
			Isect r = plane.getIntersect(currentFace.getEdgeLine(i));
			
			if (r != null && r.p != null) {
				if ( currentFace.isPartOf(new Point3f((float)r.p.getX(), (float)r.p.getY(), (float)r.p.getZ()))) {
					
					Vector3D newDir = Vector3D.sub(r.p, p);
					double angle = Vector3D.angle(dir, newDir);

					if (refAngle > angle) {
						refIndex = i;
						refAngle = angle;
						refPoint = r.p;
					}
				} else {
					Logger.info(String.format("edge[%d] intersects outside the face", i));
				}
			} else  {
				Logger.info(String.format("edge[%d] has no intersection", i));
			}
		}
		
		Vector3D endPoint = null;
		if (refIndex != -1) {
			Logger.info(String.format("analysing edge[%d]", refIndex));
			Vector3D newDir = Vector3D.sub(refPoint, p);
			if (l <= newDir.length()) {
				// new point is in the face 
				endPoint = Vector3D.add(p, newDir.normalize().mult(l));
			} else {
				Face nextFace = currentFace.getNeighbours()[refIndex];
				if (nextFace == null) 
				{ // intersecting an edge of the triangle
					endPoint = refPoint;					
				} else 
				{ // look into the next face
					endPoint = w(refPoint, newDir, l-newDir.length(), currentFace, nextFace);
				}
			}			
		} else {
			// no solution found
			endPoint = p;
		}
		
		return endPoint;
	}
	
	public Geometry createWireframe() throws FaceException {
		int stripLen = 0; 
		int[] counts = new int [getFaceNb()];
		for (int i=0;i<getFaceNb();i++) {
			counts[i] = getFace(i).getVertices().size()+1;
			stripLen += counts[i];
		}
		
		if (stripLen > 0) {
			LineStripArray grid = new LineStripArray(stripLen, GeometryArray.COORDINATES, counts);
			int t = 0;
			for (Face face : faces) {
				for (Point3f p : face.getVertices()) {
					grid.setCoordinate(t++, p);				
				}
				grid.setCoordinate(t++, face.getVertices().get(0));
			}
			return grid;
		}
		return null;
	}
	
	public Geometry createSolid() throws FaceException {
		return null;
	}

	public void addAppearance(Appearance app) {
		this.appearance = app;
	}
	
	public Shape3D getShape(boolean asWireframe, boolean asSolid) throws FaceException {
		Shape3D shape = new Shape3D();
		if (asWireframe) {
			Geometry obj = createWireframe();
			if (obj != null) {
				shape.addGeometry(obj);
			}
		}
		if (asSolid) {
			Geometry obj = createSolid();
			if (obj != null) {
				shape.addGeometry(obj);
			}
		}
		
		if (appearance != null) {
			shape.setAppearance(appearance);
		}
		
		return shape;
	}

	@Override
	public String toString() {
		return String.format("v: %d f: %d", vertices.size(), faces.size());
	}
	
	
}
