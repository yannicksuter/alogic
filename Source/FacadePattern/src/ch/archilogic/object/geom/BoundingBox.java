package ch.archilogic.object.geom;

import javax.vecmath.Point3d;

import ch.archilogic.object.ObjectDef;
import ch.archilogic.object.helper.BoxBuilder;
import ch.archilogic.runtime.exception.FaceException;

public class BoundingBox extends ObjectDef  {			
	private Point3d upper;
	private Point3d lower;
	
	@Override
	public void create() throws FaceException {
		createFace(BoxBuilder.getFace(BoxBuilder.FACE.FRONT, upper, lower));
		createFace(BoxBuilder.getFace(BoxBuilder.FACE.BACK, upper, lower));
		createFace(BoxBuilder.getFace(BoxBuilder.FACE.LEFT, upper, lower));
		createFace(BoxBuilder.getFace(BoxBuilder.FACE.RIGHT, upper, lower));
		createFace(BoxBuilder.getFace(BoxBuilder.FACE.TOP, upper, lower));
		createFace(BoxBuilder.getFace(BoxBuilder.FACE.BOTTOM, upper, lower));
	}
	
	public void setUpper(Point3d upper) {
		this.upper = upper;
	}

	public void setLower(Point3d lower) {
		this.lower = lower;
	}
}
