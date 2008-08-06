package ch.archilogic.object;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import ch.archilogic.math.vector.VecHelper;

public class Face {
	private List<Point3f> vertices = new ArrayList<Point3f>();
	private List<Vector3f> normals = new ArrayList<Vector3f>();
	private List<Integer> indices = new ArrayList<Integer>();
	private Face [] neighbours = null;
	private Vector3f faceNormal = null;

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

	public void addNormal(Vector3f n) {
		normals.add(n);
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

	public List<Vector3f> getNormals() {
		return normals;
	}

	public void setNormals(List<Vector3f> normals) {
		this.normals = normals;
	}
	
	public int getEdgeCount() {
		return vertices.size();
	}

	public Face[] getNeighbours() {
		return neighbours;
	}

	public Vector3f getFaceNormal() {
		return faceNormal;
	}

	public void setFaceNormal(Vector3f faceNormal) {
		this.faceNormal = faceNormal;
	}

	public void createFaceNormal() {
		int last = vertices.size()-1;
		Vector3f a = new Vector3f(vertices.get(1).x - vertices.get(0).x, vertices.get(1).y - vertices.get(0).y, vertices.get(1).z - vertices.get(0).z);
		Vector3f b = new Vector3f(vertices.get(last).x - vertices.get(0).x, vertices.get(last).y - vertices.get(0).y, vertices.get(last).z - vertices.get(0).z);
		this.faceNormal = new Vector3f();
		this.faceNormal.cross(b, a);		
	}
	
	public int isNeighbour(Face refFace) {
		if (refFace == this) 
			return -1;
		
		Point3f u0, u1;
		Point3f v0, v1;
		for (int i=0;i<getEdgeCount(); i++) {
			u0 = vertices.get(i % vertices.size());
			u1 = vertices.get((i+1) % vertices.size());
			
			for (int j=0;j<refFace.getEdgeCount(); j++) {
				v0 = refFace.vertices.get(j % refFace.vertices.size());
				v1 = refFace.vertices.get((j+1) % refFace.vertices.size());
				
				if ( (u0.equals(v0) && u1.equals(v1)) || (u0.equals(v1) && u1.equals(v0)) ) {
					return i;
				}
			}
		}
		return -1;
	}

	public boolean hasEdges() {
		if (neighbours != null) {
			for (int i=0;i<neighbours.length; i++) {
				if (neighbours[i] == null) {
					return true;
				}
			}
		}
		return false;
	}

	public int getEdge(int idx) {
		int edgeIdx = 0;
		for (int i=0;i<neighbours.length; i++) {
			if (neighbours[i] == null) {
				if (edgeIdx == idx) {
					return i;
				}
				edgeIdx++;
			}
		}
		return -1;
	}
	
	public void detectNeighbours(List<Face> faces) {
		neighbours = new Face[getEdgeCount()];
		for (Face face : faces) {
			int neighbourIdx = isNeighbour(face);
			if (neighbourIdx != -1) {
				neighbours[neighbourIdx] = face;
			}
		}
	}

	public Vector3f getEdgeVec(int idx) {
		if (vertices == null || vertices.size() == 0) {
			return null;
		}
		
		Point3f u0 = vertices.get(idx % vertices.size());
		Point3f u1 = vertices.get((idx+1) % vertices.size());
		return new Vector3f(u1.x-u0.x, u1.y-u0.y, u1.z-u0.z);
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
