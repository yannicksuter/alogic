package ch.archilogic.object.geom;

import java.util.Hashtable;

import javax.media.j3d.Shape3D;
import com.sun.j3d.loaders.Scene;

import ch.archilogic.object.ObjectDef;
import ch.archilogic.runtime.exception.FaceException;

public class ModelObj extends ObjectDef {
	private Shape3D model;
	
	@SuppressWarnings("unchecked")
	public ModelObj(Scene s, String name) {
		super();
		
		Hashtable<String,Shape3D> table = s.getNamedObjects();
		for (String key : table.keySet()) {
			if (key.equalsIgnoreCase(name)) {
				model = table.get(key);				
			}
		}		
	}
	
	public ModelObj(Shape3D object) {
		super();
		model = object;				
	}

	@Override
	public Shape3D getShape(boolean asWireframe, boolean asSolid) throws FaceException {
		return model;
	}	
}
