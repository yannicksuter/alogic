package ch.archilogic.test.objloader;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.util.Hashtable;

import javax.media.j3d.Shape3D;

import junit.framework.TestCase;

import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.Scene;

import ch.archilogic.log.Logger;
import ch.archilogic.object.helper.ObjHelper;

public class ObjLoaderTest extends TestCase {
	private static String Box1mObjPath = "file:c:\\tmp\\1mbox.obj";
	private static String Box100mObjPath = "file:c:\\tmp\\100mbox.obj";
	
	public void testBoxLoader() throws FileNotFoundException, IncorrectFormatException, ParsingErrorException, MalformedURLException {
		Scene box1 = ObjHelper.loadRefObject(Box1mObjPath);
		Hashtable<String,Shape3D> table1 = box1.getNamedObjects();
		for (String key : table1.keySet()) {
			System.out.println("Object: " + key);
			Shape3D o = table1.get(key);			
			ObjHelper.printInfo(o);
			
			if (o.getBounds() instanceof javax.media.j3d.BoundingBox) {
				javax.media.j3d.BoundingBox b = (javax.media.j3d.BoundingBox) o.getBounds();
				Logger.info(b.toString());
			}
		}
		
		Scene box2 = ObjHelper.loadRefObject(Box100mObjPath);
		Hashtable<String,Shape3D> table2 = box2.getNamedObjects();
		for (String key : table2.keySet()) {
			System.out.println("Object: " + key);
			Shape3D o = table2.get(key);			
			ObjHelper.printInfo(o);

			if (o.getBounds() instanceof javax.media.j3d.BoundingBox) {
				javax.media.j3d.BoundingBox b = (javax.media.j3d.BoundingBox) o.getBounds();
				Logger.info(b.toString());
			}
		}		
	}
}
