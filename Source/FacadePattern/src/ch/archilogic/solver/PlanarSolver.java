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

	private int nbPlanarOk = 0;
	private int nbPlanarFixed = 0;
	private int nbTriangle = 0;
	private double maxDiv = 0;

	public PlanarSolver(ObjectDef objRef) {
		this.objRef = objRef;
	}
	
	public void setObjRef(ObjectDef objRef) {
		this.objRef = objRef;
	}
	
	private ObjectVector prepareVerticeList(List<ObjectVector> l) {
		ObjectVector res = null;
		for (ObjectVector v : l) {
			if (!v.getFlag(ObjectVectorFlag.LOCKED) && !v.getFlag(ObjectVectorFlag.EDGE) && !v.getFlag(ObjectVectorFlag.PLANAR)) {
				res = v;
			}
		}
		if (res != null) {
			l.remove(res);
		}
		return res;
	}
	
	private boolean fixPlanarity(Face face) {
		List<ObjectVector> l = new ArrayList<ObjectVector>();
		l.addAll(face.getVertices());
		ObjectVector v = prepareVerticeList(l);
		
		if (v != null && l != null && l.size() >= 3) {
			Plane plane = Plane.createFromPoints(l.get(0), l.get(1), l.get(2));
			double d = plane.getDistanceToPoint(v);
			if (d > 0) {
				Line line = new Line(v, plane.getNormal());
				ILine res = plane.getIntersect(line);
				
				double d2 = Vector3D.sub(v, res.p).length();
				maxDiv = Math.max(maxDiv, d2);
				
				v.set(res.p);
				
				face.setObjectVectorFlag(ObjectVectorFlag.PLANAR, true);
			
				return true;			
			}
		}
		return false;
	}
	
	public void fixObject() {
		for (Face f : objRef.getFaces()) {
			if (f.getVertices().size() <= 3) {
				f.setObjectVectorFlag(ObjectVectorFlag.PLANAR, true);
				nbTriangle++;
			} else
			{
				if ( !f.isPlanar() ) 
				{ // fix planarity
					fixPlanarity(f);
					nbPlanarFixed++;
				} else 
				{ // lock vertices
					f.setObjectVectorFlag(ObjectVectorFlag.PLANAR, true);
					nbPlanarOk++;
				}
			}			
		}
		
		Logger.info(String.format("planarity - triangles: %d ok: %d fixed: %d (max div=%f)", nbTriangle, nbPlanarFixed, nbPlanarOk, maxDiv));
	}
}
