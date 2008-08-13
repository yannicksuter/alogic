package ch.archilogic.math.geom;

import javax.vecmath.Vector3f;
import ch.archilogic.math.vector.*;
import ch.archilogic.solver.intersection.ILine;

public class Plane {
    Vector3D x;
    Vector3D u;
    Vector3D v;
    Vector3D normal;
    
	public Plane (Vector3D x, Vector3D u, Vector3D v) {
		this.x = new Vector3D(x);
		this.u = new Vector3D(u);
		this.v = new Vector3D(v);
		
		this.normal = Vector3D.cross(this.u, this.v).normalize();
	}

    public Vector3f normal(Vector3f pnt) {
        return new Vector3f((float)normal.getX(), (float)normal.getY(), (float)normal.getZ());
    }

    public ILine getIntersect(Line l) {
    	Vector3D p = Vector3D.sub(this.x, l.getAPoint());
    	
    	double det3 = Vector3D.det(u, v, p);
    	double detN = Vector3D.det(u, v, l.getDir());
    	
    	if (detN == 0.0) {
    		// no intersection!
    		return null;
    	} else {
    		ILine ip = new ILine();            
    		ip.t = (det3 / detN);
    		ip.p = l.getPoint(ip.t);
    		return ip;
    	}
    }    
}
