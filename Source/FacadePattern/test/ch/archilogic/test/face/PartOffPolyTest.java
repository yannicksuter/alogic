package ch.archilogic.test.face;

import ch.archilogic.math.vector.Vector3D;
import ch.archilogic.object.Face;
import junit.framework.TestCase;

public class PartOffPolyTest extends TestCase {
	
	public void testIsPartOf() {
		
		Face f = new Face();
		
		f.addVertice(new Vector3D(0, 0, 10));
		f.addVertice(new Vector3D(100, 0, 10));
		f.addVertice(new Vector3D(100, 100, 10));
		f.addVertice(new Vector3D(0, 100, 10));

		boolean partOf = f.isPartOf(new Vector3D(49.22f,49.88f,10));
	
		System.out.println(String.format("part: %s", partOf));
		
		assertEquals(true, partOf);
	}
	

}
