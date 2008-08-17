package ch.archilogic.solver.intersection;

import java.util.ArrayList;
import java.util.List;

import ch.archilogic.math.vector.Vector3D;
import ch.archilogic.object.Face;

public class IObject {
	public Face face = null;
	public Vector3D point = null;
	public Vector3D dir = null;
	public boolean found = false;
	public boolean edge = false;
	
	public List<Face> visited = new ArrayList<Face>();
	
	public IObject(Face face) {
		this.face = face;
	}

	public IObject(Face face, Vector3D point) {
		this.face = face;
		this.point = point;
	}
	
	public void set(IObject i) {
		this.face = i.face;
		this.point = i.point;
		this.dir = i.dir;
		this.found = i.found;
		this.visited.addAll(i.visited);
	}
}
