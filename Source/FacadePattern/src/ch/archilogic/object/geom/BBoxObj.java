package ch.archilogic.object.geom;

import ch.archilogic.math.geom.Line;
import ch.archilogic.math.vector.Vector3D;
import ch.archilogic.object.ObjectDef;
import ch.archilogic.object.helper.BoxHelper;
import ch.archilogic.runtime.exception.FaceException;

public class BBoxObj extends ObjectDef  {		
	private int flags = 0;
	private Vector3D upper;
	private Vector3D lower;
	
	private static boolean bInA(int b, int a) {
		return (a & b) == b;
	}
	
	public BBoxObj(int flags) {
		super();
		this.flags = flags;
	}
	
	@Override
	public void create() throws FaceException {
		if ( flags == 0 || bInA(BoxHelper.FRONT, flags) ) {
			createFace(BoxHelper.getFace(BoxHelper.FRONT, upper, lower));
		}		
		if ( flags == 0 || bInA(BoxHelper.BACK, flags) ) {
			createFace(BoxHelper.getFace(BoxHelper.BACK, upper, lower));
		}
		if ( flags == 0 || bInA(BoxHelper.LEFT, flags) ) {
			createFace(BoxHelper.getFace(BoxHelper.LEFT, upper, lower));
		}
		if ( flags == 0 || bInA(BoxHelper.RIGHT, flags) ) {
			createFace(BoxHelper.getFace(BoxHelper.RIGHT, upper, lower));
		}
		if ( flags == 0 || bInA(BoxHelper.TOP, flags) ) {
			createFace(BoxHelper.getFace(BoxHelper.TOP, upper, lower));
		}
		if ( flags == 0 || bInA(BoxHelper.BOTTOM, flags) ) {
			createFace(BoxHelper.getFace(BoxHelper.BOTTOM, upper, lower));
		}
	}
	
	public void setUpper(Vector3D upper) {
		this.upper = upper;
	}

	public void setLower(Vector3D lower) {
		this.lower = lower;
	}
	
	public Vector3D getCenter() {
		Line l = new Line(upper.copy(), Vector3D.sub(lower, upper));		
		return l.getPoint(0.5);
	}
}
