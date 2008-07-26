package ch.archilogic.object;

import java.util.ArrayList;
import java.util.List;

import ch.archilogic.object.geom.Grid;

public class ObjectGraph {
	List<ObjectDef> childs = new ArrayList<ObjectDef>();
	
	public void addChild(ObjectDef obj) {
		childs.add(new Grid());		
	}
	
	public ObjectDef getObject(int i) {
		if (i < childs.size()) {
			return childs.get(i);
		}
		return null;
	}
}
