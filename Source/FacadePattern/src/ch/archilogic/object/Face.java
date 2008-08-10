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
		Vector3f a = new Vector3f(vertices.get(1).x - vertices.get(0).x, vertices.get(1).y - vertices.get(0).y, vertices.get(1).z - vertices.get(0).z);
		Vector3f b = new Vector3f(vertices.get(2).x - vertices.get(0).x, vertices.get(2).y - vertices.get(0).y, vertices.get(2).z - vertices.get(0).z);
		this.faceNormal = new Vector3f();
		this.faceNormal.cross(a, b);
		this.faceNormal.normalize();
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
	
	public float getAreaTriangle(Point3f A, Point3f B, Point3f C) {
		Vector3f vA = new Vector3f(B.x - A.x, B.y - A.y, B.z - A.z);
		Vector3f vB = new Vector3f(C.x - B.x, C.y - B.y, C.z - B.z);
		Vector3f vC = new Vector3f(A.x - C.x, A.y - C.y, A.z - C.z);

		float a = vA.length();
		float b = vB.length();
		float c = vC.length();

		// Heron Formula
		float f = 2 * (b * b * c * c + c * c * a * a + a * a * b * b)
				- (a * a * a * a + b * b * b * b + c * c * c * c);

		if (f != 0) {
			return (float) (0.25f * Math.sqrt(f));
		}
		return 0;
	}
		
	public float getArea(){
		float f1 = getAreaTriangle(vertices.get(0), vertices.get(1), vertices.get(3));		
		float f2 = getAreaTriangle(vertices.get(1), vertices.get(2), vertices.get(3));
		return f1+f2;		
	}
			
	public boolean isPlanar() {

		// testet ob Punkt C vom Vierreck ABCD auf der Ebene von AB und AD ist
		float d = getDistance(vertices.get(2));

		if (Math.abs(d) > 0.01) {
			return false;
		}
		
		return true;
	}
	
	
	public float getDistance(Point3f P) {
		
		// berechnet die Distanz von C zur Ebene von AB und AD 
		
		Point3f A = vertices.get(0);
		Point3f B = vertices.get(1);
		Point3f D = vertices.get(3);
		Vector3f vA = new Vector3f(B.x - A.x, B.y - A.y, B.z - A.z);
		Vector3f vB = new Vector3f(D.x - A.x, D.y - A.y, D.z - A.z);
		Vector3f vC = new Vector3f(P.x - A.x, P.y - A.y, P.z - A.z);

		Vector3f CrossC = new Vector3f();
		CrossC.cross(vA, vB);

		Vector3f c = new Vector3f();
		c.x = vA.y * vB.z - vA.z * vB.y;
		c.y = -(vA.x * vB.z - vA.z * vB.x);
		c.z = vA.x * vB.y - vA.y * vB.x;

		float r = Math.abs(	- vA.y * ((vB.x * vC.z) - (vB.z * vC.x)) 
							+ vB.y	* ((vA.x * vC.z) - (vA.z * vC.x)) 
							- vC.y	* ((vA.x * vB.z) - (vA.z * vB.x)))
				  / CrossC.length();

		return r;
	}
	
	
	public boolean isPartOf(Point3f P){
				
		// findet heraus ob ein Punkt, wenn er auf der Ebene eines dreicks AB
		// und AD ist, innerhalb des dreickes liegt

		Point3f A = vertices.get(0);
		Point3f B = vertices.get(1);
		Point3f D = vertices.get(3);

		Vector3f vAP = new Vector3f(A.x - P.x, A.y - P.y, A.z - P.z);
		Vector3f vBP = new Vector3f(B.x - P.x, B.y - P.y, B.z - P.z);
		Vector3f vDP = new Vector3f(D.x - P.x, D.y - P.y, D.z - P.z);

		float w1 = (float) Math.sqrt(((vAP.x*vAP.x)+(vAP.y*vAP.y)+(vAP.z*vAP.z))*((vBP.x*vBP.x)+(vBP.y*vBP.y)+(vBP.z*vBP.z)));
		float w2 = (float) Math.sqrt(((vBP.x*vBP.x)+(vBP.y*vBP.y)+(vBP.z*vBP.z))*((vDP.x*vDP.x)+(vDP.y*vDP.y)+(vDP.z*vDP.z)));
		float w3 = (float) Math.sqrt(((vDP.x*vDP.x)+(vDP.y*vDP.y)+(vDP.z*vDP.z))*((vAP.x*vAP.x)+(vAP.y*vAP.y)+(vAP.z*vAP.z)));
		
		// segmente zwischen den Geraden
		float a1 = (vAP.x * vBP.x + vAP.y * vBP.y + vAP.z * vBP.z)/w1;
		float a2 = (vBP.x * vDP.x + vBP.y * vDP.y + vBP.z * vDP.z)/w2;
		float a3 = (vDP.x * vAP.x + vDP.y * vAP.y + vDP.z * vAP.z)/w3;

		// winkelsumme
		float total = (float) ((Math.acos(a1) + Math.acos(a2) + Math.acos(a3)) * 57.29578);
		//System.out.println(String.format("winkel: %f", total));
		
		// ist die summe nicht 360, dann ist der Punkt ausserhalb des dreiecks
		if (Math.abs(total - 360) > 0.001)
			return false;
		return true;

		// Missing Check für Dreick BCD von Viereck ABCD
	}

	private void Normalize(Vector3f vap) {
		// TODO Auto-generated method stub

	}
}
