package ch.archilogic.object;

import java.util.ArrayList;
import java.util.List;

import ch.archilogic.object.geom.Grid;

public class ObjectGraph {
	List<ObjectDef> childs = new ArrayList<ObjectDef>();

	public void create() {
		childs.add(new Grid());
	}
}
