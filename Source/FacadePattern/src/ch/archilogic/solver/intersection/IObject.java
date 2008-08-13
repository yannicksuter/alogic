package ch.archilogic.solver.intersection;

import java.util.ArrayList;
import java.util.List;

import ch.archilogic.math.vector.Vector3D;
import ch.archilogic.object.Face;

public class IObject {
	public Face face;
	public Vector3D point;
	public Vector3D dir;
	public boolean found;
	
	public List<Face> visited = new ArrayList<Face>();
	
	public IObject(Face face) {
		this.face = face;
	}
	
	public void set(IObject i) {
		this.face = i.face;
		this.point = i.point;
		this.dir = i.dir;
		this.found = i.found;
		this.visited.addAll(i.visited);
	}
}