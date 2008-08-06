package ch.archilogic.object;

import java.util.ArrayList;
import java.util.List;

import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.LineStripArray;
import javax.media.j3d.Shape3D;
import javax.vecmath.Point3f;

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
	
	public void createFace(List<Point3f> points) throws FaceException {
		if (points == null) {
			throw new FaceException("no points to define a face.");
		}
		
		Face face = new Face();
		for (Point3f p : points) {
			if (containsEqual(p) == null) {
				vertices.add(new Point3f(p));
			}
			int index = getIndexOf(p);
			if (index > -1) {
				face.addVertice(vertices.get(index));
				face.addIndex(new Integer(index));
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
	
	public void subdivide(Face face) throws FaceException {
		List<Face> newFaces = face.subdivide();
		for (Face f : newFaces) {
			createFace(f.getVertices());
		}
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
