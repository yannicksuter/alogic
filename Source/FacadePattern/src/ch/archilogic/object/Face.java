package ch.archilogic.object;

import java.util.ArrayList;
import java.util.List;

import ch.archilogic.math.geom.Line;
import ch.archilogic.math.vector.VecHelper;
import ch.archilogic.math.vector.Vector3D;

public class Face {
	private List<Vector3D> vertices = new ArrayList<Vector3D>();
	private List<Vector3D> normals = new ArrayList<Vector3D>();
	private List<Integer> indices = new ArrayList<Integer>();
	private Face [] neighbours = null;
	private Vector3D faceNormal = null;

	public Face() {
	}
	
	public Face(Vector3D p0, Vector3D p1, Vector3D p2, Vector3D p3) {
		addVertice(p0);
		addVertice(p1);
		addVertice(p2);
		addVertice(p3);
	}
	
	public void addVertice(Vector3D p) {
		vertices.add(p);
	}
	
	public void addIndex(Integer index) {
		indices.add(index);
	}

	public void addNormal(Vector3D n) {
		normals.add(n);
	}

	public List<Vector3D> getVertices() {
		return vertices;
	}

	public void setVertices(List<Vector3D> vertices) {
		this.vertices = vertices;
	}

	public List<Integer> getIndices() {
		return indices;
	}

	public void setIndices(List<Integer> indices) {
		this.indices = indices;
	}

	public List<Vector3D> getNormals() {
		return normals;
	}

	public void setNormals(List<Vector3D> normals) {
		this.normals = normals;
	}
	
	public int getEdgeCount() {
		return vertices.size();
	}
	
	public Line getEdgeLine(int i) {
		Vector3D a = new Vector3D(vertices.get((i)%vertices.size()));
		Vector3D b = new Vector3D(vertices.get((i+1)%vertices.size()));		
		Vector3D d = Vector3D.sub(b, a);
		return new Line(a, d);
	}

	public Face[] getNeighbours() {
		return neighbours;
	}

	public Vector3D getFaceNormal() {
		return faceNormal;
	}

	public void setFaceNormal(Vector3D faceNormal) {
		this.faceNormal = faceNormal;
	}

	public void createFaceNormal() {
		Vector3D a = Vector3D.sub(new Vector3D(vertices.get(1)), new Vector3D(vertices.get(0)));
		Vector3D b = Vector3D.sub(new Vector3D(vertices.get(2)), new Vector3D(vertices.get(0)));
		this.faceNormal = Vector3D.cross(a, b).normalize();
	}
	
	public int isNeighbour(Face refFace) {
		if (refFace == this) 
			return -1;
		
		Vector3D u0, u1;
		Vector3D v0, v1;
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

	public Vector3D getEdgeVec(int idx) {
		if (vertices == null || vertices.size() == 0) {
			return null;
		}
		
		Vector3D u0 = vertices.get(idx % vertices.size());
		Vector3D u1 = vertices.get((idx+1) % vertices.size());
		return new Vector3D(u1.getX()-u0.getX(), u1.getY()-u0.getY(), u1.getZ()-u0.getZ());
	}
	
	public List<Face> subdivide() {
		List<Face> newFaces = new ArrayList<Face>();
		if (vertices.size() == 4) {
			Vector3D [] m = new Vector3D[4];
			// calc new points on the side
			m[0] = VecHelper.mid(vertices.get(0),vertices.get(1), 0.5f);
			m[1] = VecHelper.mid(vertices.get(1),vertices.get(2), 0.5f);
			m[2] = VecHelper.mid(vertices.get(2),vertices.get(3), 0.5f);
			m[3] = VecHelper.mid(vertices.get(3),vertices.get(0), 0.5f);

			// calc center
			Vector3D c = VecHelper.mid(m[0], m[2], 0.5f);
			
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
	
	public double getAreaTriangle(Vector3D A, Vector3D B, Vector3D C) {
//		Vector3f vA = new Vector3f(B.x - A.x, B.y - A.y, B.z - A.z);
//		Vector3f vB = new Vector3f(C.x - B.x, C.y - B.y, C.z - B.z);
//		Vector3f vC = new Vector3f(A.x - C.x, A.y - C.y, A.z - C.z);
		Vector3D vA = Vector3D.sub(B, A);
		Vector3D vB = Vector3D.sub(C, B); 
		Vector3D vC = Vector3D.sub(A, C); 

		double a = vA.length();
		double b = vB.length();
		double c = vC.length();

		// Heron Formula
		double f = 2 * (b * b * c * c + c * c * a * a + a * a * b * b)
				- (a * a * a * a + b * b * b * b + c * c * c * c);

		if (f != 0) {
			return 0.25 * Math.sqrt(f);
		}
		return 0;
	}
		
	/**
	 * gibt die Fläche des aufgespannten Vierecks ABCD zurück
	 * @return
	 */
	public double getArea(){
		double f1 = getAreaTriangle(vertices.get(0), vertices.get(1), vertices.get(3));		
		double f2 = getAreaTriangle(vertices.get(1), vertices.get(2), vertices.get(3));
		return f1+f2;		
	}
			
	/**
	 * testet ob Punkt C vom Vierreck ABCD auf der Ebene von AB und AD ist
	 * @return
	 */
	public boolean isPlanar() {
		double d = getDistance(vertices.get(2));

		if (Math.abs(d) > 0.01) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * berechnet die Distanz von C zur Ebene von AB und AD
	 * @param P
	 * @return
	 */
	public double getDistance(Vector3D P) {
//		Vector3D A = vertices.get(0);
//		Vector3D B = vertices.get(1);
//		Vector3D D = vertices.get(3);
//		Vector3f vA = new Vector3f(B.x - A.x, B.y - A.y, B.z - A.z);
//		Vector3f vB = new Vector3f(D.x - A.x, D.y - A.y, D.z - A.z);
//		Vector3f vC = new Vector3f(P.x - A.x, P.y - A.y, P.z - A.z);

		Vector3D vA = Vector3D.sub(vertices.get(1), vertices.get(0));
		Vector3D vB = Vector3D.sub(vertices.get(3), vertices.get(0)); 
		Vector3D vC = Vector3D.sub(P, vertices.get(0)); 
		
		
		Vector3D CrossC = Vector3D.cross(vA, vB);
//		CrossC.cross(vA, vB);

		Vector3D c = new Vector3D();
		c.x = vA.y * vB.z - vA.z * vB.y;
		c.y = -(vA.x * vB.z - vA.z * vB.x);
		c.z = vA.x * vB.y - vA.y * vB.x;

		double r = Math.abs(- vA.y * ((vB.x * vC.z) - (vB.z * vC.x)) 
							+ vB.y	* ((vA.x * vC.z) - (vA.z * vC.x)) 
							- vC.y	* ((vA.x * vB.z) - (vA.z * vB.x)))
				  / CrossC.length();

		return r;
	}	

	/**
	 * findet heraus ob ein Punkt, wenn er auf der Ebene eines dreicks AB
	 * und AD ist, innerhalb des dreickes liegt
	 * @param P
	 * @return
	 */
	public boolean insideTriangle(Vector3D P, Vector3D A, Vector3D B, Vector3D C){
//		Vector3D vAP = new Vector3f(A.x - P.x, A.y - P.y, A.z - P.z);
//		Vector3D vBP = new Vector3f(B.x - P.x, B.y - P.y, B.z - P.z);
//		Vector3D vDP = new Vector3f(C.x - P.x, C.y - P.y, C.z - P.z);
		Vector3D vAP = Vector3D.sub(A, P);
		Vector3D vBP = Vector3D.sub(B, P);
		Vector3D vDP = Vector3D.sub(C, P);

		double w1 = (float) Math.sqrt(((vAP.x*vAP.x)+(vAP.y*vAP.y)+(vAP.z*vAP.z))*((vBP.x*vBP.x)+(vBP.y*vBP.y)+(vBP.z*vBP.z)));
		double w2 = (float) Math.sqrt(((vBP.x*vBP.x)+(vBP.y*vBP.y)+(vBP.z*vBP.z))*((vDP.x*vDP.x)+(vDP.y*vDP.y)+(vDP.z*vDP.z)));
		double w3 = (float) Math.sqrt(((vDP.x*vDP.x)+(vDP.y*vDP.y)+(vDP.z*vDP.z))*((vAP.x*vAP.x)+(vAP.y*vAP.y)+(vAP.z*vAP.z)));
		
		// segmente zwischen den Geraden
		double a1 = (vAP.x * vBP.x + vAP.y * vBP.y + vAP.z * vBP.z)/w1;
		double a2 = (vBP.x * vDP.x + vBP.y * vDP.y + vBP.z * vDP.z)/w2;
		double a3 = (vDP.x * vAP.x + vDP.y * vAP.y + vDP.z * vAP.z)/w3;

		// winkelsumme
		double total = (Math.acos(a1) + Math.acos(a2) + Math.acos(a3)) * 57.29578;
		//System.out.println(String.format("winkel: %f", total));
		
		// ist die summe nicht 360, dann ist der Punkt ausserhalb des dreiecks
		if (Math.abs(total - 360) > 0.001)
			return false;
		
		return true;
	}
	
	public boolean isPartOf(Vector3D P){
		if (vertices.size() == 3) {
			return insideTriangle(P, vertices.get(0), vertices.get(1), vertices.get(2));
		} 
		else if (vertices.size() == 4) {
			// r1 fuer ABD
			boolean r1 = insideTriangle(P, vertices.get(0), vertices.get(1), vertices.get(3));
			// r2 fuer BCD - Missing Check für Dreick BCD von Viereck ABCD
			boolean r2 = insideTriangle(P, vertices.get(1), vertices.get(2), vertices.get(3));		
			return r1 || r2;			
		}
		return false;
	}

	public boolean hasVertice(Vector3D ref) {
		for (Vector3D p : vertices) {
			if (p.equals(ref)) {
				return true;
			}
		}
		return false;
	}
}
