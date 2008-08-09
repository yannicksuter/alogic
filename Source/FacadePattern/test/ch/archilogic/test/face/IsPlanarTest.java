package ch.archilogic.test.face;

import javax.vecmath.Point3f;

import ch.archilogic.object.Face;
import junit.framework.TestCase;

public class IsPlanarTest extends TestCase {

	public void testIsPlanar() {
		Face f = new Face();
		f.addVertice(new Point3f(0, 0, 0));
		f.addVertice(new Point3f(1, 0, 0));
		f.addVertice(new Point3f(1, 1, 200));
		f.addVertice(new Point3f(0, 1, 0));

		boolean planar = f.isPlanar();
		float dist = f.getDistance(f.getVertices().get(2));
		
		System.out.println(String.format("planar: %s", planar));
		System.out.println(String.format("dist: %f", dist));

		assertEquals(false, planar);
		assertEquals(200f, dist);
	}
	
	public void testIsPlanar2() {
		Face f = new Face();
		f.addVertice(new Point3f(0, 5, 0));
		f.addVertice(new Point3f(0, 0, 5));
		f.addVertice(new Point3f(0, 0, 0));
		f.addVertice(new Point3f(5, 0, 0));

		boolean planar = f.isPlanar();
		float dist = f.getDistance(f.getVertices().get(2));
		
		System.out.println(String.format("planar: %s", planar));
		System.out.println(String.format("dist: %f", dist));

		assertEquals(false, planar);
		assertEquals(2.8867514f, dist, 0.00001);
	}
	
	
}
