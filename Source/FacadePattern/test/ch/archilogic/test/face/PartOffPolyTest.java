package ch.archilogic.test.face;

import javax.vecmath.Point3f;

import ch.archilogic.object.Face;
import junit.framework.TestCase;

public class PartOffPolyTest extends TestCase {
	
	public void testIsPartOff() {
		Face f = new Face();
		f.addVertice(new Point3f(0, 0, 0));
		f.addVertice(new Point3f(1, 0, 0));
		f.addVertice(new Point3f(1, 1, 0));
		f.addVertice(new Point3f(0, 1, 0));

		boolean partOff = f.isPartOff(new Point3f(0.5f,0.5f,0));
				
		System.out.println(String.format("part: %s", partOff));
		
		assertEquals(true, partOff);	
	}
	

}
