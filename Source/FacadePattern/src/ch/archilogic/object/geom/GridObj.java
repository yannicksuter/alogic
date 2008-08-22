package ch.archilogic.object.geom;

import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Point3f;

import ch.archilogic.math.vector.Vector3D;
import ch.archilogic.object.ObjectDef;
import ch.archilogic.object.ObjectType;
import ch.archilogic.runtime.exception.FaceException;

public class GridObj extends ObjectDef {	
	private float size = 1.0f;
	private int nbSegmentsX = 50;
	private int nbSegmentsY = 50;
	
	public GridObj() {
		setType(ObjectType.OBJ_GRID);
	}

	public float getJitter(float base) {
		float b2 = base*0.5f;
		return ((float)Math.random()-0.55f)*b2;
	}

	private Point3f createVertice(int u, int v) {
		float segSize = size / nbSegmentsX;
		float segHalf = size / 2;
		
		return new Point3f(
			u*segSize-segHalf+getJitter(segSize), 
			v*segSize-segHalf+getJitter(segSize), 
			(float)(Math.sin((u*0.05)*Math.PI)*Math.cos((v*0.05)*Math.PI))*0.1f+getJitter(segSize));
	}
	
	@Override
	public void create() throws FaceException {
		Point3f[][] points = new Point3f[nbSegmentsX+1][nbSegmentsY+1];
		for (int u=0; u<nbSegmentsX+1;u++) {
			for (int v=0; v<nbSegmentsY+1;v++) {
				points[u][v] = createVertice(u, v);
			}
		}
				
		for (int u=0; u<nbSegmentsX;u++) {
			for (int v=0; v<nbSegmentsY;v++) {
				List<Vector3D> l = new ArrayList<Vector3D>();
				l.add(new Vector3D(points[u][v]));
				l.add(new Vector3D(points[u][v+1]));
				l.add(new Vector3D(points[u+1][v+1]));
				l.add(new Vector3D(points[u+1][v]));
				createFace(l);
			}
		}
	}
}
