package ch.archilogic.object;

import java.awt.Color;
import java.util.BitSet;
import java.util.HashMap;

import ch.archilogic.math.vector.Vector3D;

public class ObjectVector extends Vector3D {
	private Face face = null;
	private Color color = null;
	private BitSet flags = new BitSet();
	
	private HashMap<String, Vector3D> relatedVec = new HashMap<String, Vector3D>();

	public ObjectVector(Vector3D v) {
		super(v);
	}

	public ObjectVector(double x, double y, double z) {
		super(x,y,z);
	}

	public ObjectVector(ObjectVector v) {
		super(v);
		this.face = v.getFace();
		this.flags = (BitSet) v.getFlags().clone();
	}
	
	public ObjectVector(Face face, Vector3D v) {
		super(v);
		this.face = face;
	}

	public ObjectVector(Face face, Vector3D v, boolean locked) {
		super(v);
		this.face = face;
		this.setFlag(ObjectVectorFlag.LOCKED, locked);
	}

	public ObjectVector(Vector3D v, Color c) {
		super(v);
		this.color = c;
	}
	
	public void set(Face face, Vector3D v, boolean locked) {
		this.set(v);
		this.face = face;
		this.setFlag(ObjectVectorFlag.LOCKED, locked);
	}	
	
	public Face getFace() {
		return face;
	}
	
	public void setFace(Face face) {
		this.face = face;
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
	
	public BitSet getFlags() {
		return this.flags;
	}
	
	public boolean getFlag(ObjectVectorFlag flag) {
		return flags.get(flag.getBitIndex());
	}

	public void setFlag(ObjectVectorFlag flag, boolean value) {
		flags.set(flag.getBitIndex(), value);
	}
	
	public Vector3D getRelatedVec(String key) {
		return relatedVec.get(key);
	}

	public void setRelatedVec(String key, Vector3D value) {
		relatedVec.put(key, value);
	}
}
