package ch.archilogic.object;

import java.util.ArrayList;
import java.util.List;

import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.LineStripArray;
import javax.media.j3d.Shape3D;

import ch.archilogic.log.Logger;
import ch.archilogic.math.geom.Plane;
import ch.archilogic.math.vector.Vector3D;
import ch.archilogic.runtime.exception.FaceException;
import ch.archilogic.solver.intersection.IFace;
import ch.archilogic.solver.intersection.IObject;

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
		face.setId(faces.size());
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
	public IObject catwalk(Vector3D p, Vector3D dir, double l, Face previousFace, Face currentFace) {
		Plane plane = new Plane(p, dir, currentFace.getFaceNormal());
		return w(p, dir, l, previousFace, currentFace, plane);
	}
	
	public IObject w(Vector3D p, Vector3D dir, double l, Face previousFace, Face currentFace, Plane plane) {
		Logger.debug(String.format("---[l: %f P: %s, D:%s]", l, p, dir));
		
		// get intersection data
		IFace faceIsec = currentFace.intersectPlane(plane, p, dir);
		Logger.debug(faceIsec.toString());
		
		// evaluate intersection and do follow up intersections
		IObject endPoint = new IObject(currentFace);
		if (faceIsec.sideIdx != -1) {
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
						endPoint.found = true;
						endPoint.point = faceIsec.point;					
						endPoint.dir = newDir;
						Logger.debug(String.format("*** edge end in corder [%s]", currentFace.hasVertice(faceIsec.point)));						
					}
				} else {
					Face nextFace = currentFace.getNeighbours()[faceIsec.sideIdx];
					if (nextFace == null) 
					{ // intersecting an edge of the triangle
						endPoint.found = true;
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
}
