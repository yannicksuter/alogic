package ch.archilogic.test.line;

import ch.archilogic.math.geom.Line;
import ch.archilogic.math.vector.Vector3D;
import junit.framework.TestCase;

public class InLineTest extends TestCase {
	public void testPlaneGetDistance() {
		Line l = new Line(new Vector3D(0,0,0),new Vector3D(2,2,0));
		assertTrue(l.isIn(new Vector3D(1,1,0)));		
		System.out.println(l.getT(new Vector3D(1,1,0)));
		
		assertTrue(!l.isIn(new Vector3D(0,1,0)));
		
		assertTrue(!l.isIn(new Vector3D(1,0.99,0)));
		
		assertTrue(l.isIn(new Vector3D(0,0,0)));
		System.out.println(l.getT(new Vector3D(0,0,0)));
		
		assertTrue(l.isIn(new Vector3D(2,2,0)));
		System.out.println(l.getT(new Vector3D(2,2,0)));
	}
}
