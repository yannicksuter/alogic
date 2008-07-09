package ch.archilogic.object.geom;

import javax.media.j3d.Geometry;

import ch.archilogic.object.ObjectDef;
import ch.archilogic.object.ObjectType;

public class Grid extends ObjectDef {
	public Grid() {
		setType(ObjectType.OBJ_GRID);
	}

	@Override
	public Geometry createWireframe() {
		return null;
	}

	@Override
	public Geometry createSolid() {
		return null;
	}	
}
