package ch.archilogic.object.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ch.archilogic.math.vector.Vector3D;
import ch.archilogic.object.ObjectDef;

public class ObjectGraph {
	Map<GraphObjectType, ObjectDef> graph = new HashMap<GraphObjectType, ObjectDef>();
	Vector3D translation;
	
	public Vector3D getTranslation() {
		return translation;
	}

	public boolean isVisible(GraphObjectType type) {
		ObjectDef obj = graph.get(type);
		if (obj != null) {
			return obj.isVisible();
		}
		return false; 
	}

	public void setVisible(GraphObjectType type, boolean isVisible) {
		ObjectDef obj = graph.get(type);
		if (obj != null) {
			obj.setVisible(isVisible);
		}
	}
	
	public void setTranslation(Vector3D translation) {
		this.translation = translation.mult(-1);
	}

	public void addChild(GraphObjectType type, ObjectDef obj) {
		graph.put(type, obj);
	}
	
	public Collection<ObjectDef> getObjects() {
		return graph.values();
	}
}
