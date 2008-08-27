package ch.archilogic.object;

import java.util.ArrayList;
import java.util.List;

import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.LineStripArray;
import javax.media.j3d.Shape3D;

import ch.archilogic.log.Logger;
import ch.archilogic.math.geom.Line;
import ch.archilogic.math.geom.Plane;
import ch.archilogic.math.vector.Vector3D;
import ch.archilogic.runtime.exception.FaceException;
import ch.archilogic.solver.intersection.IEdgeSegment;
import ch.archilogic.solver.intersection.IFace;
import ch.archilogic.solver.intersection.IObject;

public class ObjectDef {
	private ObjectType type;
	private List<ObjectVector> vertices = new ArrayList<ObjectVector>();
	private List<Face> faces = new ArrayList<Face>();
	
	private List<Edge> edgeList = null;	
	
	private Appearance appearance = null;

	private boolean doCreateNormales;
	private boolean doDetectNeighbours;
	
	public ObjectDef() {
		this.doCreateNormales = false;
		this.doDetectNeighbours = false;				
	}
	
	public ObjectDef(boolean doCreateNormales, boolean doDetectNeighbours) {
		this.doCreateNormales = doCreateNormales;
		this.doDetectNeighbours = doDetectNeighbours;		
	}
	
	public ObjectType getType() {
		return type;
	}

	public void setType(ObjectType type) {
		this.type = type;
	}
	
	public void create() throws FaceException {
	}

	public List<ObjectVector> getVertices() {
		return vertices;
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

	public ObjectVector containsEqual(Vector3D ref) {
		for (ObjectVector p : vertices) {
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
		createFaceOV(f.getVertices());
	}
	
	public void createFace(List<Vector3D> verts) throws FaceException {
		List<ObjectVector> objVerts = new ArrayList<ObjectVector>();
		for (Vector3D v : verts) {
			objVerts.add(new ObjectVector(v));
		}
		createFace(objVerts, null);
	}

	public void createFaceOV(List<ObjectVector> verts) throws FaceException {
		createFace(verts, null);
	}
	
	public void createFace(List<ObjectVector> verticeList, List<Vector3D> normalList) throws FaceException {
		if (verticeList == null) {
			throw new FaceException("no points to define a face.");
		}
		
		Face face = new Face();
		for (int i=0; i< verticeList.size(); i++) {
			ObjectVector p = verticeList.get(i);

			if (containsEqual(p) == null) {
				vertices.add(new ObjectVector(p));
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
		face.setId(faces.size());
		if (doCreateNormales) {
			face.createFaceNormal();
		}
		if (doDetectNeighbours) {
			face.detectNeighbours(faces);
		}
		
		Logger.debug(String.format("adding face: %s", face.toString()));
		faces.add(face);
	}

	public void deleteVertice(Vector3D v) throws FaceException {
		List<Face> oldFaces = new ArrayList<Face>();
		oldFaces.addAll(getFaces());
		for (Face f : oldFaces) {
			if (f.hasVertice(v)) {
				deleteFace(f);
			}
		}
		vertices.remove(v);
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
			createFaceOV(f.getVertices());
		}
	}

	public void triangulate(boolean onlyEgdeFaces) throws FaceException {
		List<Face> oldFaces = new ArrayList<Face>();
		oldFaces.addAll(getFaces());
		for (Face face : oldFaces) {
			if ((onlyEgdeFaces && face.hasSidesWithNoNeighbours()) || !onlyEgdeFaces) {
				List<Face> newFaces = face.triangulate();
				if (newFaces != null) {
					deleteFace(face);
					for (Face f : newFaces) {
						createFaceOV(f.getVertices());
					}					
				}
			}
		}
	}

	public IObject catwalk(Vector3D p, Vector3D dir, double l, Face previousFace, Face currentFace) {
		Plane plane = new Plane(p, dir, currentFace.getFaceNormal());
		return w(p, dir, l, previousFace, currentFace, plane);
	}
	
	public IObject w(Vector3D p, Vector3D dir, double l, Face previousFace, Face currentFace, Plane plane) {
		Logger.debug(String.format("---[l: %f P: %s, D:%s]", l, p, dir));
		
		// get intersection data
		IFace faceIsec = currentFace.intersectPlane(plane, p, dir);
		
		// evaluate intersection and do follow up intersections
		IObject endPoint = new IObject(currentFace);
		if (faceIsec.sideIdx != -1) {
			Logger.debug(faceIsec.toString());
			Logger.debug(String.format("analysing edge[%d]", faceIsec.sideIdx));
			Vector3D newDir = Vector3D.sub(faceIsec.point, p);
			if (l <= newDir.length()) {
				// new point is in the face
				endPoint.found = true;
				endPoint.point = Vector3D.add(p, newDir.normalize().mult(l));
				endPoint.dir = newDir;
				Logger.debug(String.format("*** in-face end"));
			} else {
				if (currentFace.hasVertice(faceIsec.point)) {
					List<Face> shareVertice = getFacesWithVertice(faceIsec.point, currentFace);
					if (shareVertice.size() > 0) {
						Logger.debug(String.format("number of candidates: %d", shareVertice.size()));
						for (Face f : shareVertice) {
							if (f != previousFace) {
								IFace faceIsecTemp = f.intersectPlane(plane, faceIsec.point, newDir);
								if (faceIsecTemp.hasOngoingIntersection()) 
								{ // only evaluate neighbor faces which have interesting intersections (not resulting in the same point again)
									IObject i = w(faceIsec.point, newDir, l-newDir.length(), currentFace, f, plane);
									if (i.found) {
										endPoint.set(i);
										break;
									}
									endPoint.visited.addAll(i.visited);
								}
							}
						}
						if (!endPoint.found)
						{ // no interesting candidates  
							endPoint.found = true;
							endPoint.point = faceIsec.point;					
							endPoint.dir = newDir;
							Logger.debug(String.format("*** edge end in corder [%s]", currentFace.hasVertice(faceIsec.point)));						
						}
					} else 
					{ // face has no more neighbours
						endPoint.found = false;
						endPoint.point = faceIsec.point;					
						endPoint.dir = newDir;
						Logger.debug(String.format("*** edge end in corder [%s]", currentFace.hasVertice(faceIsec.point)));						
					}
				} else {
					Face nextFace = currentFace.getNeighbours()[faceIsec.sideIdx];
					if (nextFace == null) 
					{ // intersecting an edge of the triangle
						endPoint.found = true;
						endPoint.edge = true;
						endPoint.point = faceIsec.point;					
						endPoint.dir = newDir;
						Logger.debug(String.format("*** edge end [%s]", currentFace.hasVertice(faceIsec.point)));
					} else 
					{ // simple edge
						endPoint = w(faceIsec.point, newDir, l-newDir.length(), currentFace, nextFace, plane);
					}
				}
			}			
		} else {
			// no solution found
			endPoint.found = false;
			endPoint.point = p;
			endPoint.dir = dir;
			Logger.debug(String.format("*** dead end"));
		}
		
		// track the faces evaluated
		endPoint.visited.add(currentFace);
		
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

	public IEdgeSegment getFaceWithVertice(Vector3D point, int i) {
		for (Face face : faces) {
			if (face.getVertices().get(i).epsilonEquals(point, Vector3D.EPSILON)) {
				return new IEdgeSegment(face, point.copy());
			}
		}
		return null;
	}
	
	/**
	 * analyse object and create a list of all edges(-lines)
	 * @param findMaxNbEdges 
	 * @return List of all edges of the object
	 */	
	public List<Edge> computeEdges(int findMaxNbEdges) {
		edgeList = new ArrayList<Edge>();
		
		for (Face f : getFaces()) {
			if (f.hasSidesWithNoNeighbours()) {
				if ( getEdgeWithFaceElement(f) == null ) {
					Edge edge = new Edge();
					int id = f.getEdge(0);
					Line l = f.getSideLine(id);
					edge.createFromObject(this, l.getAPoint());		
					edgeList.add(edge);
					
					Logger.info(String.format("edge found, length = %f", edge.getLength()));
					if (findMaxNbEdges != -1 && edgeList.size() == findMaxNbEdges) 
					{ // todo: why does it break here with f3
						break;
					}
				}
			}
		}
		
		return edgeList;
	}	

	/**
	 * return an existing edge where a face is part of
	 * @return Edge
	 */
	public Edge getEdgeWithFaceElement(Face f) {
		for (Edge e : edgeList) {
			for (EdgeSegment segment : e.getSegmentList()) {
				if (segment.getFace() == f) {
					return e;
				}
			}
		}
		return null;
	}

	public double getAvgFaceSize(int vertCount) {
		double area = 0;
		int faceCnt = 0;
		for (Face f:faces) {
			if (f.getVertices().size() == vertCount) {
				faceCnt++;
				area += f.getArea();
			}
		}
		
		if (faceCnt != 0) {
			return (area / faceCnt);
		}
		
		return 0;
	}

	public IObject raycast(Line line) {
		for (Face f : getFaces()) {
			IObject res = f.intersectLine(line);
			if (res != null) {
				return res;
			}
		}
		return null;
	}
}
