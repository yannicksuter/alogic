package ch.archilogic.object;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineStripArray;
import javax.media.j3d.Shape3D;
import javax.vecmath.Point3f;

import ch.archilogic.runtime.exception.FaceException;

public class ObjectDef {
	private ObjectType type;
	private List<Point3f> vertices = new ArrayList<Point3f>();
	private List<Face> faces = new ArrayList<Face>();

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

	public void createFace(List<Point3f> points) throws FaceException {
		if (points == null) {
			throw new FaceException("no points to define a face.");
		}
		
		Face face = new Face();
		for (Point3f p : points) {
			if (!vertices.contains(p)) {
				vertices.add(p);
			}
			int index = vertices.indexOf(p);
			if (index > -1) {
				face.addVertice(p);
				face.addIndex(new Integer(index));
			} else {
				throw new FaceException("point not indexed.");
			}
		}
		
		// add face 
		faces.add(face);
	}
	
	public Geometry createWireframe() throws FaceException {
		int stripLen = 0; 
		int[] counts = new int [getFaceNb()];
		for (int i=0;i<getFaceNb();i++) {
			counts[i] = getFace(i).getVertices().size()+1;
			stripLen += counts[i];
		}
		
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
	
	public Geometry createSolid() throws FaceException {
		return null;
	}
	
	public Shape3D getShape(boolean asWireframe, boolean asSolid) throws FaceException {
		create();
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
		return shape;
	}
}
