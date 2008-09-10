package ch.archilogic.object;

import java.util.ArrayList;
import java.util.List;

import ch.archilogic.log.Logger;
import ch.archilogic.math.Rnd;
import ch.archilogic.math.geom.Line;
import ch.archilogic.math.geom.Plane;
import ch.archilogic.math.vector.VecHelper;
import ch.archilogic.math.vector.Vector3D;
import ch.archilogic.solver.intersection.IFace;
import ch.archilogic.solver.intersection.ILine;
import ch.archilogic.solver.intersection.IObject;

public class Face {
	private int id;

	private List<ObjectVector> vertices = new ArrayList<ObjectVector>();
	private List<Vector3D> normals = new ArrayList<Vector3D>();
	private List<Integer> indices = new ArrayList<Integer>();
	private Face [] neighbours = null;
	private Vector3D faceNormal = null;

	public Face() {
	}
	
	public Face(Vector3D p0, Vector3D p1, Vector3D p2, Vector3D p3) {
		addVertice(new ObjectVector(p0));
		addVertice(new ObjectVector(p1));
		addVertice(new ObjectVector(p2));
		addVertice(new ObjectVector(p3));
	}

	public Face(List<ObjectVector> verticeList) {
		for (ObjectVector v : verticeList) {
			addVertice(v);			
		}
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void addVertice(Vector3D p) {
		vertices.add(new ObjectVector(p));
	}
	
	public void addVertice(ObjectVector p) {
		vertices.add(p);
	}
	
	public void addIndex(Integer index) {
		indices.add(index);
	}

	public void addNormal(Vector3D n) {
		normals.add(n);
	}

	public List<ObjectVector> getVertices() {
		return vertices;
	}

	public ObjectVector getVertices(int i) {
		return vertices.get((i)%vertices.size());
	}

	public void setVertices(List<ObjectVector> vertices) {
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
	
	public int getSideCount() {
		return vertices.size();
	}
	
	public Vector3D getSideVec(int i) {
		Vector3D a = new Vector3D(vertices.get((i)%vertices.size()));
		Vector3D b = new Vector3D(vertices.get((i+1)%vertices.size()));		
		return Vector3D.sub(b, a);
	}

	public Line getSideLine(int i) {
		Vector3D a = new Vector3D(vertices.get((i)%vertices.size()));
		Vector3D b = new Vector3D(vertices.get((i+1)%vertices.size()));		
		Vector3D d = Vector3D.sub(b, a);
		return new Line(a, d);
	}
		
	public Face[] getNeighbours() {
		return neighbours;
	}

	public Vector3D getFaceNormal() {
		if (faceNormal == null) {
			createFaceNormal();
		}
		return faceNormal;
	}

	public void setFaceNormal(Vector3D faceNormal) {
		this.faceNormal = faceNormal;
	}

	public void createFaceNormal() {
		if (vertices.size() >= 3) {
			Vector3D a = Vector3D.sub(new Vector3D(vertices.get(1)), new Vector3D(vertices.get(0)));
			Vector3D b = Vector3D.sub(new Vector3D(vertices.get(2)), new Vector3D(vertices.get(0)));
			this.faceNormal = Vector3D.cross(a, b).normalize();
		}
	}
	
	public int isNeighbour(Face refFace) {
		if (refFace == this) 
			return -1;
		
		Vector3D u0, u1;
		Vector3D v0, v1;
		for (int i=0;i<getSideCount(); i++) {
			u0 = vertices.get(i % vertices.size());
			u1 = vertices.get((i+1) % vertices.size());
			
			for (int j=0;j<refFace.getSideCount(); j++) {
				v0 = refFace.vertices.get(j % refFace.vertices.size());
				v1 = refFace.vertices.get((j+1) % refFace.vertices.size());
				
				if ( (u0.equals(v0) && u1.equals(v1)) || (u0.equals(v1) && u1.equals(v0)) ) {
					return i;
				}
			}
		}
		return -1;
	}
	
	public boolean hasSidesWithNoNeighbours() {
		if (neighbours != null) {
			for (int i=0;i<neighbours.length; i++) {
				if (neighbours[i] == null) {
					return true;
				}
			}
		}
		return false;
	}

	public int getEdgeCount() {
		int count = 0;
		if (neighbours != null) {
			for (int i=0;i<neighbours.length; i++) {
				if (neighbours[i] == null) {
					count++;
				}
			}
		}
		return count;
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
	
	public void setNeighbour(Face face) {
		if (neighbours == null)  {
			neighbours = new Face[getSideCount()];
		}
		
		int idx = isNeighbour(face);
		if (idx != -1){
			neighbours[idx] = face;
		}
	}
	
	public void detectNeighbours(List<Face> faces) {
		neighbours = new Face[getSideCount()];	
		for (Face face : faces) {
			int neighbourIdx = isNeighbour(face);
			if (neighbourIdx != -1) {
				neighbours[neighbourIdx] = face;
				face.setNeighbour(this);
			}
		}
	}
	
	public List<Face> triangulate() {
		if (getVertices().size() == 4) {
			List<Face> newFaces = new ArrayList<Face>();
			if (Rnd.nextDouble()-0.5 > 0) {
				Face f1 = new Face();
				f1.addVertice(getVertices().get(0));
				f1.addVertice(getVertices().get(1));
				f1.addVertice(getVertices().get(3));
				newFaces.add(f1);
				Face f2 = new Face();
				f2.addVertice(getVertices().get(1));
				f2.addVertice(getVertices().get(2));
				f2.addVertice(getVertices().get(3));
				newFaces.add(f2);
			} else {
				Face f1 = new Face();
				f1.addVertice(getVertices().get(0));
				f1.addVertice(getVertices().get(1));
				f1.addVertice(getVertices().get(2));
				newFaces.add(f1);
				Face f2 = new Face();
				f2.addVertice(getVertices().get(0));
				f2.addVertice(getVertices().get(2));
				f2.addVertice(getVertices().get(3));
				newFaces.add(f2);				
			}
			return newFaces;
		}
		return null;
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
		Vector3D vA = Vector3D.sub(vertices.get(1), vertices.get(0));
		Vector3D vB = Vector3D.sub(vertices.get(3), vertices.get(0)); 
		Vector3D vC = Vector3D.sub(P, vertices.get(0)); 
		
		
		Vector3D CrossC = Vector3D.cross(vA, vB);

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
	
	public boolean isPartOf(Vector3D P){
		if (vertices.size() == 3) {
			return inTriangle(P, vertices.get(0), vertices.get(1), vertices.get(2));
		} 
		else if (vertices.size() == 4) {
			// r1 fuer ABD
			boolean r1 = inTriangle(P, vertices.get(0), vertices.get(1), vertices.get(3));
			// r2 fuer BCD - Missing Check für Dreick BCD von Viereck ABCD
			boolean r2 = inTriangle(P, vertices.get(1), vertices.get(2), vertices.get(3));		
			return r1 || r2;			
		}
		return false;
	}

	public boolean hasIndex(int index) {
		for (int i : indices) {
			if (i == index) {
				return true;
			}
		}
		return false;
	}
	
	public boolean hasVertice(Vector3D ref) {
		for (Vector3D p : vertices) {
			if (p.epsilonEquals(ref, Vector3D.EPSILON)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean validateAngle(double angle) {
		if ( Double.isNaN(angle) || Math.abs(angle) <= Math.PI*0.5 || angle >= Math.PI*1.5) {
			return true;
		} 
		return false;
	}
	
	public IFace intersectPlane(Plane plane, Vector3D p, Vector3D dir) {
		IFace res = new IFace();
		
		for (int i=0; i<this.getSideCount(); i++) {
			ILine r = plane.getIntersect(this.getSideLine(i));
			
			if (r != null && r.p != null) {
				if (r.t >= 0 && r.t <= 1) 
				{ // regular intersection
					if (this.hasVertice(r.p)) 
					{ // intersection ends in point
						Logger.debug(String.format("edge[%d] intersection ends in point (t: %f p: %s)", i, r.t, r.p));
						if (res.type != IFace.IsecType.ON_EDGE) {
							res.sideIdx = i;
							res.point = new Vector3D(r.p);
							if (r.p.epsilonEquals(p, Vector3D.EPSILON)) {
								res.type = IFace.IsecType.ON_STARTINGCORNER;
							} else  
							{ // corner, but not the starting point
								res.type = IFace.IsecType.ON_CORNER;
							}
						}
					} else
					{ // intersection on the edge					
						Vector3D newDir = Vector3D.sub(r.p, p);
						double angle = Vector3D.angle(dir, newDir);
						if ( !p.epsilonEquals(r.p, Vector3D.EPSILON) && validateAngle(angle) ) {
							Logger.debug(String.format("edge[%d] front intersection on edge (t: %f p: %s)", i, r.t, r.p));
							res.sideIdx = i;
							res.point = new Vector3D(r.p);
							res.type = IFace.IsecType.ON_EDGE;
						} else {
							Logger.debug(String.format("edge[%d] behind intersection on edge (t: %f p: %s)", i, r.t, r.p));							
						}
					}
				} else 
				{ // intersection outside edges
					Logger.debug(String.format("edge[%d] intersects outside the face", i));
				}				
			} else  {
				Logger.debug(String.format("edge[%d] has no intersection", i));
			}
		}
		return res;
	}

    private boolean inTriangle(Vector3D P, Vector3D A, Vector3D B, Vector3D C){
    	// Compute vectors        
    	Vector3D v0 = Vector3D.sub(C,A);
    	Vector3D v1 = Vector3D.sub(B,A);
    	Vector3D v2 = Vector3D.sub(P,A);

    	// Compute dot products
    	double dot00 = Vector3D.dot(v0, v0);
    	double dot01 = Vector3D.dot(v0, v1);
    	double dot02 = Vector3D.dot(v0, v2);
    	double dot11 = Vector3D.dot(v1, v1);
    	double dot12 = Vector3D.dot(v1, v2);

    	// Compute barycentric coordinates
    	double invDenom = 1 / (dot00 * dot11 - dot01 * dot01);
    	double u = (dot11 * dot02 - dot01 * dot12) * invDenom;
    	double v = (dot00 * dot12 - dot01 * dot02) * invDenom;

    	// Check if point is in triangle
    	return (u > 0) && (v > 0) && (u + v < 1);    	
    }
	
	public IObject intersectLine(Line line) {
		Vector3D p = new Vector3D(line.P.x, 0, line.P.z);
		Vector3D a = new Vector3D(vertices.get(0).x, 0, vertices.get(0).z);
		Vector3D b = new Vector3D(vertices.get(1).x, 0, vertices.get(1).z);
		Vector3D c = new Vector3D(vertices.get(2).x, 0, vertices.get(2).z);
		
		if ( inTriangle(p,a,b,c) ) {
			Plane plane = new Plane(vertices.get(0), getSideLine(0).D, getSideLine(1).D);
			ILine i = plane.getIntersect(line);
			if (i != null) {
				Logger.debug(String.format("face: %s", toString()));
				Logger.debug(String.format("i.p: %s", i.p.toString()));			
				IObject res = new IObject(this);
				res.point = i.p;
				return res;
			}
		}
		
		return null;
	}	

	public boolean hasLockedVerts() {
		for (ObjectVector v : vertices) {
			if (v.getFlag(ObjectVectorFlag.LOCKED)) {
				return true;
			}
		}
		return false;
	}

	public boolean hasEdgeVerts() {
		for (ObjectVector v : vertices) {
			if (v.getFlag(ObjectVectorFlag.EDGE)) {
				return true;
			}
		}
		return false;
	}

	private ILine getSegmentIntersection(Line line, Edge edge, Vector3D normal) {
		for (EdgeSegment s : edge.getSegmentList()) {
			Plane p = new Plane(line.getStart(), line.getDir(), normal);
			Logger.debug("line: " + s.getLine().toString() + " plane: " + p.toString());
			ILine iLine = p.getIntersect(s.getLine());			
			if (iLine != null && iLine.t >= 0 && iLine.t <= 1 && line.getT(iLine.p) >= 0 && line.getT(iLine.p) <= 1) {
				Logger.debug(String.format("segment/intersection found: %s", iLine.p));
				iLine.ref = s;
				return iLine;
			}
		}		
		return null;
	}
	
	public List<ObjectVector> cutEdge(Edge edge) {
		List<ObjectVector> newVertices = new ArrayList<ObjectVector>();		
		for (int i=0; i<getSideCount(); i++) {
			boolean vA_inside = getVertices(i).getFlag(ObjectVectorFlag.INSIDE);
			boolean vB_inside = getVertices(i+1).getFlag(ObjectVectorFlag.INSIDE);
			
			if (vA_inside) {
				newVertices.add(vertices.get(i));
			}
			
			if (vA_inside != vB_inside) {
				Logger.debug(String.format("A:%s -> B:%s inside/out switch", getVertices(i), getVertices(i+1)));			
				ILine res = getSegmentIntersection(getSideLine(i), edge, getFaceNormal());
				ObjectVector objVect = new ObjectVector(this, res.p);
				objVect.setFlag(ObjectVectorFlag.EDGE, true);
				objVect.setFlag(ObjectVectorFlag.INSIDE, true);
				objVect.setFlag(ObjectVectorFlag.LOCKED, true);
				newVertices.add(objVect);
			}
		}	
		return newVertices;
	}	
	
	public String toString() {
		String s = String.format("f[%d]:\n", id);
		for (Vector3D v : vertices) {
			s = String.format("%s %s\n", s, v.toString());
		}
		return s;
	}

	public boolean hasObjectVectorFlag(ObjectVectorFlag flag, boolean value) {
		for (ObjectVector v : vertices) {
			if (v.getFlag(flag) == value) {
				return true;
			}
		}
		return false;
	}
}
