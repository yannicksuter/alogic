package ch.archilogic.math.geom;

import ch.archilogic.math.vector.*;
import ch.archilogic.solver.intersection.ILine;

public class Plane {
    Vector3D x;
    Vector3D u;
    Vector3D v;
    Vector3D normal;
    
	public Plane (Vector3D x, Vector3D u, Vector3D v) {
		createFromPointUV(x, u, v);
	}

	public void createFromPointUV(Vector3D x, Vector3D u, Vector3D v) {
		this.x = new Vector3D(x);
		this.u = new Vector3D(u);
		this.v = new Vector3D(v);
		this.normal = Vector3D.cross(this.u, this.v).normalize();
	}
	
	public void createFromPoints(Vector3D a, Vector3D b, Vector3D c) {
		createFromPointUV(a, Vector3D.sub(b, a), Vector3D.sub(c, a));
	}
	
	public Vector3D getNormal() {
		return normal.copy();
	}
	
    public double getDistanceToPoint(Vector3D p) {
		Vector3D vC = Vector3D.sub(p, x); 		
		Vector3D CrossC = Vector3D.cross(u, v);

		Vector3D c = new Vector3D();
		c.x = u.y * v.z - u.z * v.y;
		c.y = -(u.x * v.z - u.z * v.x);
		c.z = u.x * v.y - u.y * v.x;

		double r = Math.abs(- u.y * ((v.x * vC.z) - (v.z * vC.x)) 
							+ v.y	* ((u.x * vC.z) - (u.z * vC.x)) 
							- vC.y	* ((u.x * v.z) - (u.z * v.x)))
				  / CrossC.length();

		return r;
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
