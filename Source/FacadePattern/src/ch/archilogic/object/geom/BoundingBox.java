package ch.archilogic.object.geom;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;

import javax.media.j3d.Bounds;
import javax.media.j3d.Geometry;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TriangleArray;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;

import ch.archilogic.object.ObjectDef;
import ch.archilogic.object.helper.BoxBuilder;
import ch.archilogic.object.helper.ObjHelper;
import ch.archilogic.runtime.exception.FaceException;

import com.sun.j3d.loaders.Scene;

public class BoundingBox extends ObjectDef  {
	private static String refObjPath = "file:c:\\tmp\\loadme.obj";
			
	@Override
	public void create() throws FaceException {
		
		try {
			Point3d lower = new Point3d(); 
			Point3d upper = new Point3d();
			Scene s = ObjHelper.loadRefObject(refObjPath);
			Hashtable<String,Shape3D> table = s.getNamedObjects();
			for (String key : table.keySet()) {
				Shape3D object = table.get(key);
				ObjHelper.printInfo(object);

				System.out.println("Bounds : " + object.getBounds());	
				if (object.getBounds() instanceof javax.media.j3d.BoundingBox) {
					javax.media.j3d.BoundingBox b = (javax.media.j3d.BoundingBox) object.getBounds();
					b.getUpper(upper);
					b.getLower(lower);
				}
				System.out.println("Bounds U: " + upper + " L: " + lower);	
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		createCube(0.5f);
	}
	
	private void createCube(float scale) throws FaceException {
		createFace(BoxBuilder.getFace(0, scale));
		createFace(BoxBuilder.getFace(12, scale));
		createFace(BoxBuilder.getFace(24, scale));
		createFace(BoxBuilder.getFace(36, scale));
		createFace(BoxBuilder.getFace(48, scale));
		createFace(BoxBuilder.getFace(60, scale));
	}
}
