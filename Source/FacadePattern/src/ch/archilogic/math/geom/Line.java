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
		double dif_x = p.getX() - P.getX();
		double dif_y = p.getY() - P.getY();
		double dif_z = p.getZ() - P.getZ();	

		if ( Math.abs(D.getX()) < EPSILON && Math.abs(dif_x) > EPSILON) {
			return false;
		}		
		if ( Math.abs(D.getY()) < EPSILON && Math.abs(dif_y) > EPSILON) {
			return false;
		}		
		if ( Math.abs(D.getZ()) < EPSILON && Math.abs(dif_z) > EPSILON) {
			return false;
		}		
		
		if (!testOnline(dif_x,dif_y,dif_z, D.getX(),D.getY(),D.getZ())) return false;
		if (!testOnline(dif_x,dif_z,dif_y, D.getX(),D.getZ(),D.getY())) return false;
		if (!testOnline(dif_y,dif_x,dif_z, D.getY(),D.getX(),D.getZ())) return false;
		if (!testOnline(dif_y,dif_z,dif_x, D.getY(),D.getZ(),D.getX())) return false;
		if (!testOnline(dif_z,dif_x,dif_y, D.getZ(),D.getX(),D.getY())) return false;
		if (!testOnline(dif_z,dif_y,dif_x, D.getZ(),D.getY(),D.getX())) return false;
	
		return true;
	}

	private boolean testOnline(double dif_a, double dif_b, double dif_c, double a, double b, double c) {
		if (a != 0 && b != 0) {
			double k1 = dif_a / a;
			double k2 = dif_b / b;
	
			if (!(Math.abs(k1 - k2) >= 0.0 && Math.abs(k1 - k2) < EPSILON))
				return false;
			if (c != 0) {
				k2 = dif_c / c;
				if (!(Math.abs(k1 - k2) < 0.0 && Math.abs(k1 - k2) > EPSILON))
					return false;
			}
		}		
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
		return new Vector3D(P.getX() + t * D.getX(), P.getY() + t * D.getY(), P.getZ() + t * D.getZ());
	}

	public boolean elementOf(Vector3D r) {
		Vector3D dir = Vector3D.sub(r, P);
		if (Vector3D.cross(dir, D).length() <= Vector3D.EPSILON) {
			return true;
		}
		return false;
	}

	public Vector3D getStart() {
		return getPoint(0);
	}

	public Vector3D getEnd() {
		return getPoint(1);
	}

	@Override
	public String toString() {
		return String.format("A:%s -> B:%s", getPoint(0), getPoint(1));
	}
}
