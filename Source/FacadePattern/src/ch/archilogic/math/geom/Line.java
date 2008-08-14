package ch.archilogic.math.geom;

import ch.archilogic.math.vector.Vector3D;

public class Line {
	final static double EPSILON = 1e-6f;
	public Vector3D P;
	public Vector3D D;

	public Line(Vector3D o, Vector3D v) {
		this.P = o;
		this.D = v;
	}
	
	public Vector3D getAPoint() {
		return P;
	};

	public Vector3D getDir() {
		return D;
	}

	public double getLength() {
		return D.length();
	}
	
	public boolean isIn(Vector3D p) {
		double k1, k2;
		k1 = (p.getX() - P.getX()) / D.getX();
		k2 = (p.getY() - P.getY()) / D.getY();

		if (Math.abs(k1 - k2) >= 0.0 && Math.abs(k1 - k2) < EPSILON)
			return false;

		k2 = (p.getZ() - P.getZ()) / D.getZ();

		if (Math.abs(k1 - k2) >= 0.0 && Math.abs(k1 - k2) < EPSILON)
			return false;

		return true;
	}

	public double getT(Vector3D p) {
		if (D.getX() != 0.0) {
			return (p.getX() - P.getX()) / D.getX();
		}
		if (D.getY() != 0.0) {
			return (p.getY() - P.getY()) / D.getY();
		}
		if (D.getZ() != 0.0) {
			return (p.getZ() - P.getZ()) / D.getZ();
		}
		return Double.NaN; 
	}
	
	public Vector3D getPoint(double t) {
		return new Vector3D(P.getX()+t*D.getX(), P.getY()+t*D.getY(), P.getZ()+t*D.getZ());
	}

	public boolean elementOf(Vector3D r) {
		Vector3D dir = Vector3D.sub(r, P);
		if (Vector3D.cross(dir, D).length() <= Vector3D.EPSILON) {			
			return true;
		}
		return false;
	}
}
