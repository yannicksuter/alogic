package ch.archilogic.object.helper;

import java.util.ArrayList;
import java.util.List;

import ch.archilogic.math.geom.Line;
import ch.archilogic.math.vector.Vector3D;
import ch.archilogic.object.Face;
import ch.archilogic.object.ObjectDef;
import ch.archilogic.object.ObjectVector;
import ch.archilogic.runtime.exception.FaceException;
import ch.archilogic.solver.intersection.IEdgeSegment;
import ch.archilogic.solver.intersection.IObject;

public class GridHelper {
	private ObjectDef grid;
	private Vector3D normal;

	public ObjectDef getGrid() {
		return grid;
	}

	public void setGrid(ObjectDef grid) {
		this.grid = grid;
	}
	
	public GridHelper(Face face, double len) {
		try {
			createGrid(face, len);
		} catch (FaceException e) {
			e.printStackTrace();
		}
	}

	private void createGrid(Face face, double len) throws FaceException {
		grid = new ObjectDef(false, true);
		
		Line side0 = face.getSideLine(0);
		int numSegX = (int)((side0.getLength() / len) + 0.5);
			
		Line side1 = face.getSideLine(1);
		int numSegY = (int)((side1.getLength() / len) + 0.5);
		
		Vector3D d0 = side0.getDir();
		Vector3D d1 = side1.getDir();
		Vector3D[][] points = new Vector3D[numSegX+1][numSegY+1];
		for (int x = 0; x < numSegX+1; x++) {
			for (int y = 0; y < numSegY+1; y++) {
				double t0 = (x*len) / side0.getLength();
				double t1 = (y*len) / side1.getLength();
				Vector3D vP = Vector3D.add(d0.mult(t0), d1.mult(t1));
				points[x][y] = Vector3D.add(side0.getAPoint(), vP);				
			}
		}

		for (int x = 0; x < numSegX; x++) {
			for (int y = 0; y < numSegY; y++) {
				List<Vector3D> l = new ArrayList<Vector3D>();
				l.add(points[x][y]);
				l.add(points[x][y+1]);
				l.add(points[x+1][y+1]);
				l.add(points[x+1][y]);
				grid.createFace(l);
			}
		}
		
		face.createFaceNormal();
		normal = face.getFaceNormal();
	}

	public void projection(ObjectDef objReference) {
		for (ObjectVector v : grid.getVertices()) {
			Line line = new Line(v, normal.neg());
			IObject res = objReference.raycast(line);
			if (res != null) {
				v.set(res.point);
				v.setFace(res.face);
				v.setLocked(true);
			} else
			{
				v.setLocked(false);
			}
		}
	}
	
	public void removeUnlockedVertices() throws FaceException {
		List<ObjectVector> oldList = new ArrayList<ObjectVector>();
		oldList.addAll(grid.getVertices());
		for (ObjectVector v : oldList) {
			if (!v.isLocked()) {
				grid.deleteVertice(v);
			}
		}
	}

	public void unlockAll() {
		for (ObjectVector v : grid.getVertices()) {
			v.setLocked(false);
		}
	}

	public void createBorderFace(IEdgeSegment s, IEdgeSegment n) throws FaceException {
		List<ObjectVector> l = new ArrayList<ObjectVector>();
		ObjectVector p0 = new ObjectVector(s.face, s.point, true);
		p0.setEdge(true);
		ObjectVector p1 = new ObjectVector(n.face, n.point, true);
		p1.setEdge(true);

		ObjectVector r = getNearestVertice(s.point, n.point);		
		ObjectVector p2 = new ObjectVector(null, r, true);
		
		l.add(p0);
		l.add(p1);
		l.add(p2);
		
		grid.createFaceOV(l);
	}

	private ObjectVector getNearestVertice(Vector3D ref1, Vector3D ref2) {
		ObjectVector res = null;
		double len = Double.MAX_VALUE;
		for (ObjectVector v : grid.getVertices()) {
			if (!v.isEdge()) {
				double l1 = v.to(ref1).length();
				if (l1 > 0 && l1 < len) {
					len = l1;
					res = v;
				}
				double l2 = v.to(ref2).length();
				if (l2 > 0 && l2 < len) {
					len = l2;
					res = v;
				}
			}
		}
		return res;
	}
	
	private ObjectVector getNearestEdge(ObjectVector ref1, ObjectVector ref2) {
		ObjectVector res = null;
		double len = Double.MAX_VALUE;
		for (ObjectVector v : grid.getVertices()) {
			if (v.isEdge()) {
				double l1 = v.to(ref1).length();
				if (l1 > 0 && l1 < len) {
					len = l1;
					res = v;
				}
				double l2 = v.to(ref2).length();
				if (l2 > 0 && l2 < len) {
					len = l2;
					res = v;
				}
			}
		}
		return res;
	}
	
	public void fillEdge() throws FaceException {
		grid.detectEdges();
		
		List<Face> oldList = new ArrayList<Face>();
		oldList.addAll(grid.getFaces());
		
		for (Face f : oldList) {
			if (f.hasSidesWithNoNeighbours()) {			
				for (int idx = 0; idx < f.getEdgeCount(); idx++) {
					int sideId = f.getEdge(idx);
					Line l = f.getSideLine(sideId);
					ObjectVector v0 = grid.containsEqual(l.getPoint(0));
					ObjectVector v1 = grid.containsEqual(l.getPoint(1));
					if (!v0.isEdge() && !v1.isEdge() && !f.hasEdgeVerts()) {
						List<ObjectVector> vList = new ArrayList<ObjectVector>();
						ObjectVector v2 = getNearestEdge(v0, v1);		
						vList.add(v0);
						vList.add(v1);
						vList.add(v2);						
						grid.createFaceOV(vList);
					}
				}
			}
		}
	}
}