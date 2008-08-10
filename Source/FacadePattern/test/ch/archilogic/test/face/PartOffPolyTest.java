package ch.archilogic.test.face;

import javax.vecmath.Point3f;

import ch.archilogic.object.Face;
import junit.framework.TestCase;

public class PartOffPolyTest extends TestCase {
	
	public void testIsPartOf() {
		
		Face f = new Face();
		
		f.addVertice(new Point3f(0, 0, 10));
		f.addVertice(new Point3f(100, 0, 10));
		f.addVertice(new Point3f(100, 100, 10));
		f.addVertice(new Point3f(0, 100, 10));

		boolean partOf = f.isPartOf(new Point3f(49.22f,49.88f,10));
	
		System.out.println(String.format("part: %s", partOf));
		
		assertEquals(true, partOf);
	}
	

}
