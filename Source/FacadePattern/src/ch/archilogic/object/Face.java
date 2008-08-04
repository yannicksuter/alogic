package ch.archilogic.object;

import java.util.ArrayList;
import java.util.List;

import javax.media.j3d.LineArray;
import javax.vecmath.Point3f;

import ch.archilogic.math.vector.VecHelper;

public class Face {
	private List<Point3f> vertices = new ArrayList<Point3f>();
	private List<Integer> indices = new ArrayList<Integer>();

	public Face() {
	}
	
	public Face(Point3f p0, Point3f p1, Point3f p2, Point3f p3) {
		addVertice(p0);
		addVertice(p1);
		addVertice(p2);
		addVertice(p3);
	}
	
	public void addVertice(Point3f p) {
		vertices.add(p);
	}
	
	public void addIndex(Integer index) {
		indices.add(index);
	}
	
	public List<Point3f> getVertices() {
		return vertices;
	}

	public void setVertices(List<Point3f> vertices) {
		this.vertices = vertices;
	}

	public List<Integer> getIndices() {
		return indices;
	}

	public void setIndices(List<Integer> indices) {
		this.indices = indices;
	}
	
	public List<Face> subdivide() {
		List<Face> newFaces = new ArrayList<Face>();
		if (vertices.size() == 4) {
			Point3f [] m = new Point3f[4];
			// calc new points on the side
			m[0] = VecHelper.mid(vertices.get(0),vertices.get(1), 0.5f);
			m[1] = VecHelper.mid(vertices.get(1),vertices.get(2), 0.5f);
			m[2] = VecHelper.mid(vertices.get(2),vertices.get(3), 0.5f);
			m[3] = VecHelper.mid(vertices.get(3),vertices.get(0), 0.5f);

			// calc center
			Point3f c = VecHelper.mid(m[0], m[2], 0.5f);
			
			// create new faces
			newFaces.add(new Face(m[0], vertices.get(1), m[1], c));
			newFaces.add(new Face(m[1], vertices.get(2), m[2], c));
			newFaces.add(new Face(m[2], vertices.get(3), m[3], c));
			newFaces.add(new Face(m[3], vertices.get(0), m[0], c));
		} else {
			newFaces.add(this);
		}
		return newFaces;
	}
}
