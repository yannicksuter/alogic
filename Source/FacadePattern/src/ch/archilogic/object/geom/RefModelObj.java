package ch.archilogic.object.geom;

import java.util.Enumeration;

import javax.media.j3d.Shape3D;
import javax.media.j3d.TriangleArray;

import ch.archilogic.object.ObjectDef;
import ch.archilogic.runtime.exception.ObjectException;

public class RefModelObj extends ObjectDef {
	
	@SuppressWarnings("unchecked")
	public RefModelObj(Shape3D shape) throws ObjectException {
		super();

		Enumeration en = shape.getAllGeometries();
		while (en.hasMoreElements()) {
			Object obj = en.nextElement();		
			if (obj instanceof TriangleArray) {
				TriangleArray a = (TriangleArray)obj;
				for (int i = 0; i < a.getVertexCount(); i++) {
				}
			} else {
				throw new ObjectException("object could not be parsed.");
			}
		}
	}
}
