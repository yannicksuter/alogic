package ch.archilogic.test.face;

import javax.vecmath.Point3f;

import ch.archilogic.object.Face;
import junit.framework.TestCase;

public class GetAreaTest extends TestCase {
	public void testGetArea() {
		Face f = new Face();
		f.addVertice(new Point3f(0,0,2.5f));
		f.addVertice(new Point3f(1,0,2.5f));
		f.addVertice(new Point3f(1,1,2.5f));
		f.addVertice(new Point3f(0,1,2.5f));
		
		float area = f.getArea();
		System.out.println(String.format("area: %f", area));
		
		assertEquals(1.0, area, 0.0f);
	}
}
