package ch.archilogic.test.face;

import javax.vecmath.Point3f;

import ch.archilogic.object.Face;
import junit.framework.TestCase;

public class IsPlanarTest extends TestCase {
	
	public void testIsPlanar(){
		Face f = new Face();
		f.addVertice(new Point3f(0,0,2.5f));
		f.addVertice(new Point3f(1,0,2.5f));
		f.addVertice(new Point3f(1,1,2.5f));
		f.addVertice(new Point3f(0,1,2.5f));
		
		boolean planar = f.isPlanar();
		System.out.println(String.format("planar: %s", planar));
		
		assertEquals(true, planar);		
				
	}
	

}
