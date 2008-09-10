package ch.archilogic.solver;

import java.util.ArrayList;
import java.util.List;

import ch.archilogic.log.Logger;
import ch.archilogic.math.geom.Line;
import ch.archilogic.math.geom.Plane;
import ch.archilogic.math.vector.Vector3D;
import ch.archilogic.object.Face;
import ch.archilogic.object.ObjectDef;
import ch.archilogic.object.ObjectVector;
import ch.archilogic.object.ObjectVectorFlag;
import ch.archilogic.solver.intersection.ILine;

public class PlanarSolver {
	private ObjectDef objRef = null;

	public PlanarSolver(ObjectDef objRef) {
		this.objRef = objRef;
	}
	
	public void setObjRef(ObjectDef objRef) {
		this.objRef = objRef;
	}
	
	private ObjectVector prepareVerticeList(List<ObjectVector> l) {
		for (ObjectVector v : l) {
			if (!v.getFlag(ObjectVectorFlag.LOCKED) && !v.getFlag(ObjectVectorFlag.EDGE)) {
				l.remove(v);
				return v;
			}
		}
		return null;
	}

	private boolean fixPlanarity(Face face) {
		List<ObjectVector> l = new ArrayList<ObjectVector>();
		l.addAll(face.getVertices());
		ObjectVector v = prepareVerticeList(l);
		
		Plane plane = new Plane(l.get(0), l.get(1), l.get(2));
		double d = plane.getDistanceToPoint(v);
		if (d > 1) {
			Line line = new Line(v, plane.getNormal());
			ILine res = plane.getIntersect(line);
			
			/*double d2 =*/ Vector3D.sub(v, res.p).length();
		
//			v.set(res.p);
			return true;			
		}
		return false;
	}
	
	public void fixObject() {
		int nbPlanarOk = 0;
		int nbPlanarFixed = 0;
		int nbTriangle = 0;
		for (Face f : objRef.getFaces()) {
			if (f.getVertices().size() <= 3) {
				nbTriangle++;
			} else
			{
				if ( !f.isPlanar() ) 
				{ // fix planarity
					if (fixPlanarity(f)) {
						return;
					}
					nbPlanarFixed++;
				} else 
				{ // lock vertices
					nbPlanarOk++;
				}
//				lockVertices(f);
//				return;
			}			
		}
		Logger.info(String.format("planarity - triangles: %d fixed: %d ok: %d", nbTriangle, nbPlanarFixed, nbPlanarOk));
	}
}
