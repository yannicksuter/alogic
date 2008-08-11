package ch.archilogic.math.vector;

public class VecHelper {
	public static Vector3D mid(Vector3D p1, Vector3D p2, double f) {
		Vector3D v = Vector3D.sub(p2,p1).mult(f);
		return Vector3D.add(p1, v);
	}
}
