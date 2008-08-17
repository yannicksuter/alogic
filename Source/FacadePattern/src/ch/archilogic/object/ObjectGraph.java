package ch.archilogic.object;

import java.util.ArrayList;
import java.util.List;

import ch.archilogic.math.vector.Vector3D;

public class ObjectGraph {
	List<ObjectDef> childs = new ArrayList<ObjectDef>();
	Vector3D translation;
	
	public Vector3D getTranslation() {
		return translation;
//		return new Vector3D(1,1,1);
	}

	public void setTranslation(Vector3D translation) {
		this.translation = translation.mult(-1);
	}

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
