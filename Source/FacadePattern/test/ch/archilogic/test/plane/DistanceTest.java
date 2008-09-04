package ch.archilogic.test.plane;

import ch.archilogic.math.geom.Plane;
import ch.archilogic.math.vector.Vector3D;
import junit.framework.TestCase;

public class DistanceTest extends TestCase {
	public void testPlaneGetDistance() {
		Plane plane = new Plane(new Vector3D(5,0,0), new Vector3D(-1,0,1), new Vector3D(-1,1,0));
		double d = plane.getDistanceToPoint(new Vector3D(0,5,0));
		assertEquals(0.0, d);
	}
}
