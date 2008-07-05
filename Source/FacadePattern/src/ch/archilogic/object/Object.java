package ch.archilogic.object;

import java.util.ArrayList;
import java.util.List;
import javax.vecmath.*;

public class Object {
	private ObjectType type;
	private List<Point2f> vertices = new ArrayList<Point2f>();
	private List<Integer> verticeStrip = new ArrayList<Integer>();	
	private Boolean isVerticeStripClosed = false;

	public enum AppendType {
		TAIL,
		FRONT
	}
	
	public ObjectType getType() {
		return type;
	}
	public void setType(ObjectType type) {
		this.type = type;
	}
	public List<Point2f> getVertices() {
		return vertices;
	}
	public void setVertices(List<Point2f> vertices) {
		this.vertices = vertices;
	}
	public List<Integer> getVerticeStrip() {
		return verticeStrip;
	}
	public void setVerticeStrip(ArrayList<Integer> verticeStrip) {
		this.verticeStrip = verticeStrip;
	}	
	public Boolean isVerticeStripClosed() {
		return isVerticeStripClosed;
	}
	public void setIsVerticeStripClosed(Boolean isVerticeStripClosed) {
		this.isVerticeStripClosed = isVerticeStripClosed;
	}
	public void addVertice(Point2f point) {
		addVertice(point, AppendType.TAIL);
	}
	public void addVertice(Point2f point, AppendType type) {
		switch (type) {
		case TAIL:
			vertices.add(point);
			verticeStrip.add(vertices.size());
			break;
		case FRONT:
			vertices.add(point);
			verticeStrip.set(0, vertices.size());
			break;
		}
	}
}
