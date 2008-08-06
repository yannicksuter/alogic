package ch.archilogic.object.geom;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.media.j3d.Shape3D;
import javax.media.j3d.TriangleArray;
import javax.vecmath.Point3f;

import ch.archilogic.object.ObjectDef;
import ch.archilogic.object.helper.ObjHelper;
import ch.archilogic.runtime.exception.FaceException;
import ch.archilogic.runtime.exception.ObjectException;

public class RefModelObj extends ObjectDef {
	
	@SuppressWarnings("unchecked")
	public RefModelObj(Shape3D shape) throws ObjectException, FaceException {
		super();

		ObjHelper.convert(shape, false, false, false, false, false, false);
		
		Enumeration en = shape.getAllGeometries();
		while (en.hasMoreElements()) {
			Object obj = en.nextElement();		
			if (obj instanceof TriangleArray) {
				TriangleArray a = (TriangleArray)obj;
				List<Point3f> l = new ArrayList<Point3f>();
				Point3f p1 = new Point3f();
				Point3f p2 = new Point3f();
				Point3f p3 = new Point3f();
				for (int i = 0; i < a.getVertexCount()/3; i++) {
					l.clear();
					a.getCoordinate(i*3+0, p1);
					l.add(p1);
					a.getCoordinate(i*3+1, p2);
					l.add(p2);
					a.getCoordinate(i*3+2, p3);
					l.add(p3);
					createFace(l);
				}
			} else {
				throw new ObjectException("object could not be parsed.");
			}
		}
	}
}
