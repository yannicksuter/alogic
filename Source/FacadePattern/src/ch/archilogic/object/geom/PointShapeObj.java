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

import ch.archilogic.math.vector.Vector3D;
import ch.archilogic.object.ObjectDef;
import ch.archilogic.object.ObjectVector;
import ch.archilogic.runtime.exception.FaceException;

public class PointShapeObj extends ObjectDef {
	final static int CUBESIZE = 5;

	private Color pointColor = Color.CYAN;
	private List<ObjectVector> verts = new ArrayList<ObjectVector>();

	public void setColor(Color col) {
		this.pointColor = col;
	}
	
	public void addPoint(ObjectVector v) {
		verts.add(v);
	}

	private Color4f convertColor(Color c) {
		if (c == null) {
			return new Color4f(pointColor.getRed(), pointColor.getGreen(), pointColor.getBlue(), pointColor.getAlpha());
		}
		return new Color4f(c.getRed(), c.getGreen(), c.getBlue(), c.getAlpha());		
	}
	
	@SuppressWarnings("static-access")
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

		PointArray pointArray = new PointArray(verts.size(), PointArray.COORDINATES | PointArray.COLOR_4);
		for (int i = 0; i < verts.size(); i++) {
			ObjectVector v = verts.get(i);
			pointArray.setCoordinate(i, v.getPoint3f());
			pointArray.setColor(i, convertColor(v.getColor()));
		}

		Shape3D shape = new Shape3D(pointArray, app);
		return shape;
	}
}
