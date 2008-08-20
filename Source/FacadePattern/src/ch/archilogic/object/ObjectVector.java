package ch.archilogic.object;

import java.awt.Color;

import ch.archilogic.math.vector.Vector3D;

public class ObjectVector extends Vector3D {
	private Face face = null;
	private boolean locked = false;
	private Color color = null;

	public ObjectVector(Vector3D v) {
		super(v);
	}

	public ObjectVector(double x, double y, double z) {
		super(x,y,z);
	}

	public ObjectVector(ObjectVector v) {
		super(v);
		this.face = v.getFace();
		this.locked = v.isLocked();
	}
	
	public ObjectVector(Face face, Vector3D v) {
		super(v);
		this.face = face;
	}

	public ObjectVector(Face face, Vector3D v, boolean locked) {
		super(v);
		this.face = face;
		this.locked = locked;
	}

	public ObjectVector(Vector3D v, Color c) {
		super(v);
		this.color = c;
	}
	
	public void set(Face face, Vector3D v, boolean locked) {
		this.set(v);
		this.face = face;
		this.locked = locked;
	}	
	
	public Face getFace() {
		return face;
	}
	
	public void setFace(Face face) {
		this.face = face;
	}
	
	public boolean isLocked() {
		return locked;
	}
	
	public void setLocked(boolean locked) {
		this.locked = locked;
	}
	
	public void setVector(Vector3D v) {
		this.set(v);
	}	

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
}
