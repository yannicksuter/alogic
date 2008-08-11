package ch.archilogic.math.vector;

import javax.vecmath.Vector3f;

public class Vector3D {
	public final static double EPSILON = 0.00000000001;
	public final static Vector3D ZERO = new Vector3D(0.0);
	public final static Vector3D X = new Vector3D(1.0, 0.0, 0.0);
	public final static Vector3D Y = new Vector3D(0.0, 1.0, 0.0);
	public final static Vector3D Z = new Vector3D(0.0, 0.0, 1.0);
	
	private double x;
	private double y;
	private double z;
	
	public Vector3D(double x, double y, double z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public Vector3D(double scalar) {
		this(scalar, scalar, scalar);
	}
	
	public Vector3D(Vector3D vector) {
		this(vector.x, vector.y, vector.z);
	}

	public Vector3D(Vector3f vector) {
		this(vector.x, vector.y, vector.z);
	}
	
	public Vector3D copy() {
		return new Vector3D(this.x, this.y, this.z);
	}
	
	public double getX() {
		return this.x;
	}
	
	public double getY() {
		return this.y;
	}
	
	public double getZ() {
		return this.z;
	}
	
	public double length() {
		return Math.sqrt(Vector3D.dot(this, this));
	}
	
	public Vector3D normalize() {
		double magnitude = this.length();
		if (magnitude > Vector3D.EPSILON) {
			return new Vector3D(
					this.x / magnitude,
					this.y / magnitude,
					this.z / magnitude);
		} else {
			return Vector3D.ZERO;
		}
	}
	
	public Vector3D getReflectedAt(Vector3D n) {
		return Vector3D.sub(
				this,
				Vector3D.mult(2.0 * Vector3D.dot(n, this), n));
	}
	
	public String toString() {
		return "[" + this.x + ", " + this.y + ", " + this.z + "]";
	}
	
	public static double det(Vector3D a, Vector3D b, Vector3D c) {
		return
			+ a.x * b.y * c.z
			+ b.x * c.y * a.z
			+ c.x * a.y * b.z
			- c.x * b.y * a.z
			- b.x * a.y * c.z
			- a.x * c.y * b.z;
	}
	
	public static double dot(Vector3D v1, Vector3D v2) {
		return v1.x * v2.x + v1.y * v2.y + v1.z * v2.z;
	}
	
	public static Vector3D cross(Vector3D v1, Vector3D v2) {
		return new Vector3D(
				v1.y * v2.z - v1.z * v2.y,
				v1.z * v2.x - v1.x * v2.z,
				v1.x * v2.y - v1.y * v2.x);
	}
	
	public static Vector3D add(Vector3D v1, Vector3D v2) {
		return new Vector3D(v1.x + v2.x, v1.y + v2.y, v1.z + v2.z);
	}
	
	public static Vector3D sub(Vector3D v1, Vector3D v2) {
		return new Vector3D(v1.x - v2.x, v1.y - v2.y, v1.z - v2.z);
	}
	
	public static Vector3D mult(double scalar, Vector3D v) {
		return new Vector3D(scalar * v.x, scalar * v.y, scalar * v.z);
	}
	
	public static Vector3D mult(Vector3D v1, Vector3D v2) {
		return new Vector3D(v1.x * v2.x, v1.y * v2.y, v1.z * v2.z);
	}
}
