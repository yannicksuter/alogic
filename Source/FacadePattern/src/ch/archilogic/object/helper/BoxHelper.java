package ch.archilogic.object.helper;

import java.util.ArrayList;
import java.util.List;

import ch.archilogic.math.vector.Vector3D;

public class BoxHelper {
	public static final int FRONT = 1;
	public static final int BACK = 2;
	public static final int RIGHT = 4;
	public static final int LEFT = 8;
	public static final int TOP = 16;
	public static final int BOTTOM = 32;
	
	private static final double[] verts = {
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

	public static List<Vector3D> getFace(int i, float scale) {
		List<Vector3D> l = new ArrayList<Vector3D>();
		for (int u = 0; u < 4; u++) {
			l.add(new Vector3D(verts[i + (u * 3)] * scale, verts[i + (u * 3) + 1] * scale, verts[i + (u * 3) + 2] * scale));
		}
		return l;
	}

	public static List<Vector3D> getFace(int face, Vector3D upper, Vector3D lower) {
		List<Vector3D> l = new ArrayList<Vector3D>();
		switch (face) {
		case FRONT:
			l.add(new Vector3D((float)upper.x, (float)upper.y, (float)upper.z));
			l.add(new Vector3D((float)lower.x, (float)upper.y, (float)upper.z));
			l.add(new Vector3D((float)lower.x, (float)lower.y, (float)upper.z));
			l.add(new Vector3D((float)upper.x, (float)lower.y, (float)upper.z));
			break;	
		case BACK:
			l.add(new Vector3D((float)upper.x, (float)upper.y, (float)lower.z));
			l.add(new Vector3D((float)lower.x, (float)upper.y, (float)lower.z));
			l.add(new Vector3D((float)lower.x, (float)lower.y, (float)lower.z));
			l.add(new Vector3D((float)upper.x, (float)lower.y, (float)lower.z));
			break;	
		case LEFT:
			l.add(new Vector3D((float)lower.x, (float)upper.y, (float)upper.z));
			l.add(new Vector3D((float)lower.x, (float)upper.y, (float)lower.z));
			l.add(new Vector3D((float)lower.x, (float)lower.y, (float)lower.z));
			l.add(new Vector3D((float)lower.x, (float)lower.y, (float)upper.z));
			break;	
		case RIGHT:
			l.add(new Vector3D((float)upper.x, (float)upper.y, (float)upper.z));
			l.add(new Vector3D((float)upper.x, (float)upper.y, (float)lower.z));
			l.add(new Vector3D((float)upper.x, (float)lower.y, (float)lower.z));
			l.add(new Vector3D((float)upper.x, (float)lower.y, (float)upper.z));
			break;	
		case TOP:
			l.add(new Vector3D((float)upper.x, (float)upper.y, (float)upper.z));
			l.add(new Vector3D((float)upper.x, (float)upper.y, (float)lower.z));
			l.add(new Vector3D((float)lower.x, (float)upper.y, (float)lower.z));
			l.add(new Vector3D((float)lower.x, (float)upper.y, (float)upper.z));
			break;	
		case BOTTOM:
			l.add(new Vector3D((float)upper.x, (float)lower.y, (float)upper.z));
			l.add(new Vector3D((float)upper.x, (float)lower.y, (float)lower.z));
			l.add(new Vector3D((float)lower.x, (float)lower.y, (float)lower.z));
			l.add(new Vector3D((float)lower.x, (float)lower.y, (float)upper.z));
			break;	
		};
		return l;
	}
}
