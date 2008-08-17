package ch.archilogic.math.vector;

import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

public class Vector3D {
	public final static double EPSILON = 0.0000000001;
	public final static Vector3D ZERO = new Vector3D(0.0);
	public final static Vector3D X = new Vector3D(1.0, 0.0, 0.0);
	public final static Vector3D Y = new Vector3D(0.0, 1.0, 0.0);
	public final static Vector3D Z = new Vector3D(0.0, 0.0, 1.0);
	
	public double x;
	public double y;
	public double z;

	public Vector3D() {
		super();
		this.x = 0;
		this.y = 0;
		this.z = 0;
	}

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

	public Vector3D(Point3f vector) {
		this(vector.x, vector.y, vector.z);
	}
	
	public Vector3D(Vector3f vector) {
		this(vector.x, vector.y, vector.z);
	}

	public Vector3D abs() {
		return new Vector3D(Math.abs(this.x), Math.abs(this.y), Math.abs(this.z));		
	}
	
	public boolean equals(Vector3D ref) {
		return (this.x == ref.getX() && 
				this.y == ref.getY() && 
				this.z == ref.getZ());
	}
	
	public boolean epsilonEquals(Vector3D ref, double epsilon) {
		Vector3D diff = Vector3D.sub(ref, this).abs();
		return (diff.getX() <= epsilon && 
				diff.getY() <= epsilon && 
				diff.getZ() <= epsilon );
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

	public double length(Vector3D a, Vector3D b) {
		return Vector3D.sub(b,a).length();
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
	
	public static double angle(Vector3D a, Vector3D b) {
		return Math.acos(dot(a,b)/(a.length()*b.length()));
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

	public Vector3D mult(double scalar) {
		return new Vector3D(scalar * this.x, scalar * this.y, scalar * this.z);
	}
	
	// converter
	public Point3f getPoint3f() {
		return new Point3f((float)x, (float)y, (float)z);
	}

	public static Point3f getPoint3f(Vector3D v) {
		return new Point3f((float)v.getX(), (float)v.getY(), (float)v.getZ());
	}

	public Vector3f getVector3f() {
		return new Vector3f((float)x, (float)y, (float)z);
	}
	
	public Vector3f getVector3f(Vector3D v) {
		return new Vector3f((float)v.getX(), (float)v.getY(), (float)v.getZ());
	}
}
