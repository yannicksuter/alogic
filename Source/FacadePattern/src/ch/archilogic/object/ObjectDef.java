package ch.archilogic.object;

import javax.media.j3d.Geometry;
import javax.media.j3d.Shape3D;
import javax.vecmath.Point3f;

public class ObjectDef {
	private ObjectType type;

	public ObjectType getType() {
		return type;
	}

	public void setType(ObjectType type) {
		this.type = type;
	}
	
	public void create() {
	}

	public int getVerticeNb() {
		return 0;
	}
	
	public Point3f getVertice(int i) {
		return null;
	}
	
	public int getFaceNb() {
		return 0;
	}

	public int [] getFace(int i) {
		return null;
	}
	
	public Geometry createWireframe() {
		return null;
	}

	public Geometry createSolid() {
		return null;
	}
	
	public Shape3D getShape(boolean asWireframe, boolean asSolid) {
		Shape3D shape = new Shape3D();
		if (asWireframe) {
			Geometry obj = createWireframe();
			if (obj != null) {
				shape.addGeometry(obj);
			}
		}
		if (asSolid) {
			Geometry obj = createSolid();
			if (obj != null) {
				shape.addGeometry(obj);
			}
		}
		return shape;
	}
}
