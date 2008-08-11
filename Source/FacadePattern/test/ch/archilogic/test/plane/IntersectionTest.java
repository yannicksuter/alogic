package ch.archilogic.test.plane;

import ch.archilogic.math.geom.Line;
import ch.archilogic.math.geom.Plane;

import ch.archilogic.math.vector.Vector3D;
import ch.archilogic.solver.intersection.ILine;

import junit.framework.TestCase;

public class IntersectionTest extends TestCase {
	public void testPlaneLineIntersection() {
		Plane plane = new Plane(new Vector3D(5,0,0), new Vector3D(-1,0,1), new Vector3D(-1,1,0));

		Vector3D d = new Vector3D(1,1,1);
		d.normalize();
		
		Line line = new Line(Vector3D.ZERO, d);
		
		ILine r = plane.getIntersect(line);
		Vector3D p = line.getPoint(r.t);
		
		System.out.println(String.format("t: %f p: %s d: %f", r.t, p,p.length()));
	}

	public void testPlaneLineIntersection2() {
		Plane plane = new Plane(new Vector3D(5,0,0), new Vector3D(0,0,1), new Vector3D(0,1,0));

		Vector3D d = new Vector3D(1,1,1);
		d.normalize();
		
		Line line = new Line(Vector3D.ZERO, d);
		
		ILine r = plane.getIntersect(line);
		Vector3D p = line.getPoint(r.t);
		
		System.out.println(String.format("t: %f p: %s d: %f", r.t, p,p.length()));
		assertEquals(Math.sqrt(75), p.length(), Vector3D.EPSILON);
	}
}
