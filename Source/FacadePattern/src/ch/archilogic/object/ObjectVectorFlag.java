package ch.archilogic.object;

public enum ObjectVectorFlag {
	EDGE(1),
	LOCKED(2),
	INSIDE(3),
	PLANAR(4);
	
	private ObjectVectorFlag(int bitIndex) {
		this.bitIndex = bitIndex;
	}
	
	private int bitIndex;
	public int getBitIndex() {
		return bitIndex;
	}
}
