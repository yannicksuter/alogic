package ch.archilogic.object.helper;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point3d;
import javax.vecmath.Point3f;

public class BoxBuilder {
	public static enum FACE {
		FRONT,
		BACK,
		RIGHT,
		LEFT,
		TOP,
		BOTTOM
	};
	
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

	public static List<Point3f> getFace(int i, float scale) {
		List<Point3f> l = new ArrayList<Point3f>();
		for (int u = 0; u < 4; u++) {
			l.add(new Point3f(verts[i + (u * 3)] * scale, verts[i + (u * 3) + 1] * scale, verts[i + (u * 3) + 2] * scale));
		}
		return l;
	}

	public static List<Point3f> getFace(FACE front, Point3d upper, Point3d lower) {
		return null;
	}
}
