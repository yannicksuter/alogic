package ch.archilogic.object.geom;

import java.awt.Color;
import java.util.List;
import java.util.ArrayList;

import javax.media.j3d.Appearance;
import javax.media.j3d.PointArray;
import javax.media.j3d.PointAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TransparencyAttributes;
import javax.vecmath.Color4f;
import javax.vecmath.Point3f;

import ch.archilogic.math.vector.Vector3D;
import ch.archilogic.object.ObjectDef;
import ch.archilogic.runtime.exception.FaceException;

public class PointShapeObj extends ObjectDef {
	final static int CUBESIZE = 5;

	List<Vector3D> verts = new ArrayList<Vector3D>();

	private Shape3D createPoints() {
		// set up the appearance to make large, transparent, antialiased points
		Appearance app = new Appearance();
		// increase the size of each point and turn on AA
		PointAttributes pa = new PointAttributes(10.0f, true);
		app.setPointAttributes(pa);
		// set up transparency
		TransparencyAttributes ta = new TransparencyAttributes();
		ta.setTransparencyMode(ta.NICEST);
		ta.setTransparency(0.2f); // used if color is not COLOR_4
		app.setTransparencyAttributes(ta);

		// create a cube of points to display
		int volumesq = CUBESIZE * CUBESIZE;
		Point3f coords[] = new Point3f[CUBESIZE * CUBESIZE * CUBESIZE];
		Color4f colors[] = new Color4f[CUBESIZE * CUBESIZE * CUBESIZE];
		for (int x = 0; x < CUBESIZE; x++) {
			for (int y = 0; y < CUBESIZE; y++) {
				for (int z = 0; z < CUBESIZE; z++) {
					// coord is x,y,z normalized and centered
					coords[volumesq * x + CUBESIZE * y + z] = new Point3f((float) x / (CUBESIZE - 1) - 0.5f, (float) y / (CUBESIZE - 1) - 0.5f, (float) z / (CUBESIZE - 1) - 0.5f);
					// color varies by coords, r = x, g = y, b=1, a = z
					colors[volumesq * x + CUBESIZE * y + z] = new Color4f((float) x / (CUBESIZE - 1), (float) y / (CUBESIZE - 1), 1.0f, (float) z / (CUBESIZE - 1));
				}
			}
		}
		PointArray pointArray = new PointArray(CUBESIZE * CUBESIZE * CUBESIZE, PointArray.COORDINATES | PointArray.COLOR_4);
		pointArray.setCoordinates(0, coords);
		pointArray.setColors(0, colors);

		Shape3D shape = new Shape3D(pointArray, app);
		return shape;
	}

	public void addPoint(Vector3D v) {
		verts.add(v);
	}

	@Override
	public Shape3D getShape(boolean asWireframe, boolean asSolid) throws FaceException {
		if (verts.size() == 0)
			return null;
			
		// set up the appearance to make large, transparent, antialiased points
		Appearance app = new Appearance();
		// increase the size of each point and turn on AA
		PointAttributes pa = new PointAttributes(8.0f, true);
		app.setPointAttributes(pa);
		// set up transparency
		TransparencyAttributes ta = new TransparencyAttributes();
		ta.setTransparencyMode(ta.NICEST);
		ta.setTransparency(0.2f); // used if color is not COLOR_4
		app.setTransparencyAttributes(ta);

		Color4f col = new Color4f(Color.CYAN.getRed(), Color.CYAN.getGreen(), Color.CYAN.getBlue(), Color.CYAN.getAlpha());
		PointArray pointArray = new PointArray(verts.size(), PointArray.COORDINATES | PointArray.COLOR_4);
		for (int i = 0; i < verts.size(); i++) {
			Vector3D v = verts.get(i);
			pointArray.setCoordinate(i, v.getPoint3f());
			pointArray.setColor(i, col);
		}

		Shape3D shape = new Shape3D(pointArray, app);
		return shape;
	}
}
