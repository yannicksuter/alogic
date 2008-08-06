package ch.archilogic.object.geom;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.media.j3d.Shape3D;
import javax.media.j3d.TriangleArray;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

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
				List<Point3f> p = new ArrayList<Point3f>();
				Point3f p1 = new Point3f();
				Point3f p2 = new Point3f();
				Point3f p3 = new Point3f();
				List<Vector3f> n = new ArrayList<Vector3f>();
				Vector3f n1 = new Vector3f();
				Vector3f n2 = new Vector3f();
				Vector3f n3 = new Vector3f();
				for (int i = 0; i < a.getVertexCount()/3; i++) {
					p.clear();
					n.clear();
					a.getCoordinate(i*3+0, p1);
					a.getNormal(i*3+0, n1);
					p.add(p1);
					n.add(n1);
					a.getCoordinate(i*3+1, p2);
					p.add(p2);
					n.add(n2);
					a.getCoordinate(i*3+2, p3);
					p.add(p3);
					n.add(n3);
					createFace(p, n);
				}
			} else {
				throw new ObjectException("object could not be parsed.");
			}
		}
	}
}
