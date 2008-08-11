package ch.archilogic.object;

import java.util.ArrayList;
import java.util.List;

import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.LineStripArray;
import javax.media.j3d.Shape3D;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import ch.archilogic.log.Logger;
import ch.archilogic.math.geom.Plane;
import ch.archilogic.math.vector.Vector3D;
import ch.archilogic.runtime.exception.FaceException;
import ch.archilogic.solver.intersection.IFace;
import ch.archilogic.solver.intersection.ILine;

public class ObjectDef {
	private ObjectType type;
	private List<Vector3D> vertices = new ArrayList<Vector3D>();
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
	
	public Vector3D getVertice(int i) {
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

	public Vector3D containsEqual(Vector3D ref) {
		for (Vector3D p : vertices) {
			if (p.equals(ref)) {
				return p;
			}
		}
		return null;
	}
	
	public int getIndexOf(Vector3D ref) {
		Vector3D p = containsEqual(ref);
		if (p != null) {
			return vertices.indexOf(p);
		}
		return -1;
	}

	public void addFace(Face f) throws FaceException {
		createFace(f.getVertices(), null);
	}
	
	public void createFace(List<Vector3D> verts) throws FaceException {
		createFace(verts, null);
	}
	
	public void createFace(List<Vector3D> pointList, List<Vector3D> normalList) throws FaceException {
		if (pointList == null) {
			throw new FaceException("no points to define a face.");
		}
		
		Face face = new Face();
		for (int i=0; i< pointList.size(); i++) {
			Vector3D p = pointList.get(i);

			if (containsEqual(p) == null) {
				vertices.add(new Vector3D(p));
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
			createFace(f.getVertices(), null);
		}
	}
	
	public IFace w(Vector3D p, Vector3D dir, double l, Face previousFace, Face currentFace) {
		Plane plane = new Plane(p, dir, currentFace.getFaceNormal());
		
		int refIndex = -1;
		double refAngle = 2*Math.PI;
		Vector3D refPoint = null;
		
		for (int i=0; i<currentFace.getEdgeCount(); i++) {
			ILine r = plane.getIntersect(currentFace.getEdgeLine(i));
			
			if (r != null && r.p != null) {
				if ( currentFace.isPartOf(r.p) ) {
					
					Vector3D newDir = Vector3D.sub(r.p, p);
					double angle = Vector3D.angle(dir, newDir);

					if (refAngle >= angle || Double.isNaN(angle)) {
						refIndex = i;
						if (!Double.isNaN(angle)) {
							refAngle = angle;
						}
						refPoint = r.p;
						break;
					}
				} else {
					Logger.info(String.format("edge[%d] intersects outside the face", i));
				}
			} else  {
				Logger.info(String.format("edge[%d] has no intersection", i));
			}
		}
		
		IFace endPoint = new IFace(currentFace);
		if (refIndex != -1) {
			Logger.info(String.format("analysing edge[%d]", refIndex));
			Vector3D newDir = Vector3D.sub(refPoint, p);
			if (l <= newDir.length()) {
				// new point is in the face
				endPoint.found = true;
				endPoint.point = Vector3D.add(p, newDir.normalize().mult(l));
				endPoint.dir = newDir;
			} else {
				Face nextFace = currentFace.getNeighbours()[refIndex];
				if (nextFace == null) 
				{ // intersecting an edge of the triangle
					endPoint.found = true;
					endPoint.point = refPoint;					
					endPoint.dir = newDir;
				} else 
				{ // look into the next face
					if (currentFace.hasVertice(refPoint)) 
					{ // point is a corner.. so more than one edge face has to be checked
						List<Face> shareVertice = getFacesWithVertice(refPoint, currentFace);
						Logger.info(String.format("number of candidates: %d", shareVertice.size()));
						for (Face f : shareVertice) {
							endPoint = w(refPoint, newDir, l-newDir.length(), currentFace, f);
							if (endPoint.found) {
								break;
							}
						}
					} else
					{ // simple edge
						endPoint = w(refPoint, newDir, l-newDir.length(), currentFace, nextFace);
					}
				}
			}			
		} else {
			// no solution found
			endPoint.found = false;
			endPoint.point = p;
			endPoint.dir = dir;
		}
		
		return endPoint;
	}
	
	private List<Face> getFacesWithVertice(Vector3D refPoint, Face excludeFace) {
		List<Face> l = new ArrayList<Face>();
		for (Face f : faces) {
			if (f.hasVertice(refPoint) && (excludeFace == null || excludeFace != f)) {
				l.add(f);
			}
		}
		return l;
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
				for (Vector3D p : face.getVertices()) {
					grid.setCoordinate(t++, Vector3D.getPoint3f(p));				
				}
				grid.setCoordinate(t++, Vector3D.getPoint3f(face.getVertices().get(0)));
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
