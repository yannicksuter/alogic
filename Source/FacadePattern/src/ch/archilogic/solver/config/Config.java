package ch.archilogic.solver.config;

import ch.archilogic.math.vector.Vector3D;
import ch.archilogic.solver.think.ThinkType;

public class Config {
	public Config(ConfigType type) {
		this.type = type;
	}
	public ConfigType getType() {
		return type;
	}	
	public String getRefObjPath() {
		return refObjPath;
	}
	public void setRefObjPath(String refObjPath) {
		this.refObjPath = refObjPath;
	}
	public double getScale() {
		return scale;
	}
	public void setScale(double scale) {
		this.scale = scale;
	}
	public int getUseEdgeId() {
		return useEdgeId;
	}
	public void setUseEdgeId(int useEdgeId) {
		this.useEdgeId = useEdgeId;
	}
	public boolean isUseEdgeCenterDir() {
		return useEdgeCenterDir;
	}
	public void setUseEdgeCenterDir(boolean useEdgeCenterDir) {
		this.useEdgeCenterDir = useEdgeCenterDir;
	}
	public boolean isUseEdgeSegmentDir() {
		return useEdgeSegmentDir;
	}
	public void setUseEdgeSegmentDir(boolean useEdgeSegmentDir) {
		this.useEdgeSegmentDir = useEdgeSegmentDir;
	}
	public Vector3D getUseEdgeDir() {
		return useEdgeDir;
	}
	public void setUseEdgeDir(Vector3D useEdgeDir) {
		this.useEdgeDir = useEdgeDir;
	}
	public double getUseEdgeLen() {
		return useEdgeLen;
	}
	public void setUseEdgeLen(double useEdgeLen) {
		this.useEdgeLen = useEdgeLen;
	}
	public boolean isConsiderCorner() {
		return considerCorner;
	}
	public void setConsiderCorner(boolean considerCorner) {
		this.considerCorner = considerCorner;
	}
	public boolean isEvaluateCorner() {
		return evaluateCorner;
	}
	public void setEvaluateCorner(boolean evaluateCorner) {
		this.evaluateCorner = evaluateCorner;
	}
	public int getFindMaxNbEdges() {
		return findMaxNbEdges;
	}
	public void setFindMaxNbEdges(int findMaxNbEdges) {
		this.findMaxNbEdges = findMaxNbEdges;
	}
	public int getFindMaxFaces() {
		return findMaxFaces;
	}
	public void setFindMaxFaces(int findMaxFaces) {
		this.findMaxFaces = findMaxFaces;
	}
	public int getFindMaxCornerFaces() {
		return findMaxCornerFaces;
	}
	public void setFindMaxCornerFaces(int findMaxCornerFaces) {
		this.findMaxCornerFaces = findMaxCornerFaces;
	}
	public ThinkType getUseThinkModel() {
		return useThinkModel;
	}
	public void setUseThinkModel(ThinkType useThinkModel) {
		this.useThinkModel = useThinkModel;
	}
	
	private ConfigType type = null;
	
	// these are just the defaults
	private String refObjPath = "file:c:\\tmp\\loadme.obj";
	private double scale = 1.0;
	private int useEdgeId = 1;
	private boolean useEdgeCenterDir = false;
	private boolean useEdgeSegmentDir = false;
	private Vector3D useEdgeDir = new Vector3D(0,1,0);
	private double useEdgeLen = 1.2;
	private boolean considerCorner = false;
	private boolean evaluateCorner = false;
	private int findMaxNbEdges = 2;
	private int findMaxFaces = 10;
	private int findMaxCornerFaces = -1;
	private ThinkType useThinkModel = ThinkType.CYLINDRIC;
}
