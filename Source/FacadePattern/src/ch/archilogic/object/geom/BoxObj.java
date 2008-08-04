package ch.archilogic.object.geom;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point3f;
import ch.archilogic.object.ObjectDef;
import ch.archilogic.object.ObjectType;
import ch.archilogic.object.helper.BoxHelper;
import ch.archilogic.runtime.exception.FaceException;

public final class BoxObj extends ObjectDef {
	public BoxObj() {
		setType(ObjectType.OBJ_BOX);
	}
	
	@Override
	public void create() throws FaceException {
		createCube(0.5f);
	}
	
	private void createCube(float scale) throws FaceException {
		createFace(BoxHelper.getFace(0, scale));
		createFace(BoxHelper.getFace(12, scale));
		createFace(BoxHelper.getFace(24, scale));
		createFace(BoxHelper.getFace(36, scale));
		createFace(BoxHelper.getFace(48, scale));
		createFace(BoxHelper.getFace(60, scale));
	}
}
