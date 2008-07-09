package ch.archilogic.object.geom;

import javax.media.j3d.Geometry;
import javax.media.j3d.GeometryArray;
import javax.media.j3d.LineArray;
import javax.vecmath.Point3f;

import ch.archilogic.object.ObjectDef;
import ch.archilogic.object.ObjectType;

public class Grid extends ObjectDef {	
	private float size = 1.0f;
	private int nbSegments = 50;
	private Point3f [][] points;
	
	public Grid() {
		setType(ObjectType.OBJ_GRID);
	}
	
	@Override
	public void create() {
		float segSize = size / nbSegments;
		float segHalf = size / 2;
		points = new Point3f[nbSegments+1][nbSegments+1];
		for (int u=0; u<=nbSegments;u++) {
			for (int v=0; v<=nbSegments;v++) {
				points[u][v] = new Point3f(u*segSize-segHalf, v*segSize-segHalf, (float)(Math.sin((u*0.05)*Math.PI)*Math.cos((v*0.05)*Math.PI))*0.1f);
			}
		}
	}

	@Override
	public Geometry createWireframe() {
		create();
		LineArray grid = new LineArray(nbSegments*nbSegments*4*2, GeometryArray.COORDINATES /*| GeometryArray.COLOR_3*/);
		int pointCnt = 0;
		for (int u=0; u<nbSegments;u++) {
			for (int v=0; v<nbSegments;v++) {
				grid.setCoordinate(pointCnt++, points[u][v]);
				grid.setCoordinate(pointCnt++, points[u][v+1]);
				grid.setCoordinate(pointCnt++, points[u][v+1]);
				grid.setCoordinate(pointCnt++, points[u+1][v+1]);
				grid.setCoordinate(pointCnt++, points[u+1][v+1]);
				grid.setCoordinate(pointCnt++, points[u+1][v]);
				grid.setCoordinate(pointCnt++, points[u+1][v]);
				grid.setCoordinate(pointCnt++, points[u][v]);
			}
		}
		
//	        Color3f c = new Color3f(0.1f, 0.8f, 0.1f);
//	        for (int i = 0; i < 44; i++)
//	          landGeom.setColor(i, c);
		
		return grid;
	}

	@Override
	public Geometry createSolid() {
		return null;
	}	
}
