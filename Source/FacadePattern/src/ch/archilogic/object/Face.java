package ch.archilogic.object;

import java.util.ArrayList;
import java.util.List;

import javax.media.j3d.LineArray;
import javax.vecmath.Point3f;

public class Face {
	private List<Point3f> vertices = new ArrayList<Point3f>();
	private List<Integer> indices = new ArrayList<Integer>();

	public void addVertice(Point3f p) {
		vertices.add(p);
	}
	
	public void addIndex(Integer index) {
		indices.add(index);
	}
	
	public List<Point3f> getVertices() {
		return vertices;
	}

	public void setVertices(List<Point3f> vertices) {
		this.vertices = vertices;
	}

	public List<Integer> getIndices() {
		return indices;
	}

	public void setIndices(List<Integer> indices) {
		this.indices = indices;
	}
}
