package ch.archilogic.object.geom;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point3f;
import ch.archilogic.object.ObjectDef;
import ch.archilogic.object.ObjectType;
import ch.archilogic.runtime.exception.FaceException;

public final class BoxObj extends ObjectDef {
	  private static final float[] verts = {
		  // front face
		  1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f,
		  // back face
		  -1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f,
		  // right face
		  1.0f, -1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, 1.0f,
		  // left face
		  -1.0f, -1.0f, 1.0f, -1.0f, 1.0f, 1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, -1.0f,
		  // top face
		  1.0f, 1.0f, 1.0f, 1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, 1.0f,
		  // bottom face
		  -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, -1.0f, 1.0f, -1.0f, -1.0f, 1.0f, -1.0f, 1.0f 
	  };

	public BoxObj() {
		setType(ObjectType.OBJ_BOX);
	}
	
	@Override
	public void create() throws FaceException {
		createCube(0.5f);
	}

	private List<Point3f> getFace(int i, float scale) {
		List<Point3f> l = new ArrayList<Point3f>();
		for (int u=0;u<4;u++) {
			l.add(new Point3f(verts[i+(u*3)]*scale, verts[i+(u*3)+1]*scale, verts[i+(u*3)+2]*scale));
		}
		return l;
	}
	
	private void createCube(float scale) throws FaceException {
		createFace(getFace(0, scale));
		createFace(getFace(12, scale));
		createFace(getFace(24, scale));
		createFace(getFace(36, scale));
		createFace(getFace(48, scale));
		createFace(getFace(60, scale));
	}
}
