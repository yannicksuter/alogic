package ch.archilogic.solver.think;

public enum ThinkType {
	CYLINDRIC("Think cylindric"),
	FLAT("Think flat");
	
	private String desc;
	
	public String getDesc() {
		return desc;
	}
	
	private ThinkType(String desc) {
		this.desc = desc;
	}
}
