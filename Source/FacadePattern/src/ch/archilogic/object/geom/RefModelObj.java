package ch.archilogic.object.geom;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.media.j3d.Shape3D;
import javax.media.j3d.TriangleArray;
import javax.vecmath.Point3f;
import javax.vecmath.Vector3f;

import ch.archilogic.math.vector.Vector3D;
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
				List<Vector3D> p = new ArrayList<Vector3D>();
				Point3f p1 = new Point3f();
				Point3f p2 = new Point3f();
				Point3f p3 = new Point3f();
				List<Vector3D> n = new ArrayList<Vector3D>();
				Vector3f n1 = new Vector3f();
				Vector3f n2 = new Vector3f();
				Vector3f n3 = new Vector3f();
				for (int i = 0; i < a.getVertexCount()/3; i++) {
					p.clear();
					n.clear();
					a.getCoordinate(i*3+0, p1);
					a.getNormal(i*3+0, n1);
					p.add(new Vector3D(p1));
					n.add(new Vector3D(n1));
					a.getCoordinate(i*3+1, p2);
					p.add(new Vector3D(p2));
					n.add(new Vector3D(n2));
					a.getCoordinate(i*3+2, p3);
					p.add(new Vector3D(p3));
					n.add(new Vector3D(n3));
					createFace(p, n);
				}
			} else {
				throw new ObjectException("object could not be parsed.");
			}
		}
	}
}
