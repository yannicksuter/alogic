package ch.archilogic.math.vector;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public class VecHelper {
	public static Point3f mid(Point3f p1, Point3f p2, float f) {
		Vector3f v = new Vector3f(p2);
		v.sub(p1);
		v.scale(f);
		Point3f m = new Point3f(p1);
		m.add(v);
		
		return m;
	}

	public static Point3f intersect(Point3f p0, Point3f p1, Point3f q0, Point3f q1) {
		Vector3f u = new Vector3f(p1);
		u.sub(p0);		
		Vector3f v = new Vector3f(q1);
		v.sub(q0);
		return null;
	}
}
