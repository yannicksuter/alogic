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
}
