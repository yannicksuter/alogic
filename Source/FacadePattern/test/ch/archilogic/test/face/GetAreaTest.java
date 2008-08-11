package ch.archilogic.test.face;

import ch.archilogic.math.vector.Vector3D;
import ch.archilogic.object.Face;
import junit.framework.TestCase;

public class GetAreaTest extends TestCase {
	public void testGetArea() {
		Face f = new Face();
		f.addVertice(new Vector3D(0,0,2.5f));
		f.addVertice(new Vector3D(1,0,2.5f));
		f.addVertice(new Vector3D(1,1,2.5f));
		f.addVertice(new Vector3D(0,1,2.5f));
		
		double area = f.getArea();
		System.out.println(String.format("area: %f", area));
		
		assertEquals(1.0, area, 0.0f);
	}
}
