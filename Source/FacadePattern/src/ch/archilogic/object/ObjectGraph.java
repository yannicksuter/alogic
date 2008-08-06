package ch.archilogic.object;

import java.util.ArrayList;
import java.util.List;

public class ObjectGraph {
	List<ObjectDef> childs = new ArrayList<ObjectDef>();
	
	public void addChild(ObjectDef obj) {
		childs.add(obj);		
	}
	
	public ObjectDef getObject(int i) {
		if (i < childs.size()) {
			return childs.get(i);
		}
		return null;
	}

	public List<ObjectDef> getObjects() {
		return childs;
	}
}
