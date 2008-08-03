package ch.archilogic.object.geom;

import java.util.ArrayList;
import java.util.List;

import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.LineArray;
import javax.media.j3d.LineStripArray;
import javax.vecmath.Point3f;

import ch.archilogic.object.Face;
import ch.archilogic.object.ObjectDef;
import ch.archilogic.object.ObjectType;
import ch.archilogic.runtime.exception.FaceException;

public class GridObj extends ObjectDef {	
	private float size = 1.0f;
	private int nbSegments = 2;
	
	public GridObj() {
		setType(ObjectType.OBJ_GRID);
	}

	public float getJitter(float base) {
		float b2 = base*0.5f;
		return ((float)Math.random()-0.55f)*b2;
	}

	private Point3f createVertice(int u, int v) {
		float segSize = size / nbSegments;
		float segHalf = size / 2;
		
		return new Point3f(
			u*segSize-segHalf+getJitter(segSize), 
			v*segSize-segHalf+getJitter(segSize), 
			(float)(Math.sin((u*0.05)*Math.PI)*Math.cos((v*0.05)*Math.PI))*0.1f+getJitter(segSize));
	}
	
	@Override
	public void create() throws FaceException {
		Point3f[][] points = new Point3f[nbSegments+1][nbSegments+1];
		for (int u=0; u<nbSegments+1;u++) {
			for (int v=0; v<nbSegments+1;v++) {
				points[u][v] = createVertice(u, v);
			}
		}
				
		for (int u=0; u<nbSegments;u++) {
			for (int v=0; v<nbSegments;v++) {
				List<Point3f> l = new ArrayList<Point3f>();
				l.add(points[u][v]);
				l.add(points[u][v+1]);
				l.add(points[u+1][v+1]);
				l.add(points[u+1][v]);
				createFace(l);
			}
		}
	}
}
