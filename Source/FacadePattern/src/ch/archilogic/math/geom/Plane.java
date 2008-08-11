package ch.archilogic.math.geom;

import javax.vecmath.Vector3f;
import ch.archilogic.math.vector.*;

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

	public Isect getIntersect(Line l) {
		Vector3D c = new Vector3D(l.getDir());
		Vector3D p = Vector3D.sub(this.x, new Vector3D(l.getAPoint()));

		double det3 = Vector3D.det(u, v, p);
		double detN = Vector3D.det(u, v, c);

		if (detN == 0.0) {
			return null;
		} else {
            Isect ip = new Isect();
            
            ip.t = (det3 / detN) - Vector3D.EPSILON;
            if (ip.t != -1.0E-11) {
            	ip.p = l.getPoint(ip.t);
            }
            
            return ip;
		}
	}    
}
