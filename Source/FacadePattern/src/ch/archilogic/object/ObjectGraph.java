package ch.archilogic.object;

import javax.media.j3d.GeometryArray;
import javax.media.j3d.GeometryStripArray;
import javax.media.j3d.LineArray;
import javax.media.j3d.Node;
import javax.media.j3d.PointArray;
import javax.media.j3d.QuadArray;
import javax.media.j3d.Shape3D;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;

public class ObjectGraph {
	private static final int nGridSize = 50;
	private static final int m_kReportInterval = 50;

	private PointArray pointArray = new PointArray(nGridSize * nGridSize,
			GeometryArray.COORDINATES);

	public PointArray getPointArray() {
		return pointArray;
	}

	public void create() {
		// create the PointArray that we will be rendering
		int nPoint = 0;

		for (int n = 0; n < nGridSize; n++) {
			for (int i = 0; i < nGridSize; i++) {
				Point3f point = new Point3f(n - nGridSize / 2, i - nGridSize / 2, 0.0f);
				pointArray.setCoordinate(nPoint++, point);
			}
		}

	}

	private Shape3D createSolid() {
		Shape3D shape = new Shape3D();
		Color3f red = new Color3f(1,0,0);

		Point3f p1 = new Point3f ( -0.2f, -0.2f, 0 );
		Point3f p2 = new Point3f ( -0.2f,  0.2f, 0.f );
		Point3f p3 = new Point3f (  0.2f,  0.2f, 0 );
		Point3f p4 = new Point3f (  0.2f, -0.2f, 0 );

		Point3f [] face  = { p1, p2, p3, p4 };	
		Color3f [] colors  = { red, red, red, red };	
		QuadArray quad = new QuadArray(4, QuadArray.COORDINATES | QuadArray.COLOR_3);
		quad.setCoordinates(0, face);
		quad.setColors(0, colors);
		
		shape.addGeometry(quad);
		return shape;
	}
	
	private Shape3D createWireframe() {
		Shape3D shape = new Shape3D();
		
		Point3f p1 = new Point3f ( -0.2f, -0.2f, 0 );
		Point3f p2 = new Point3f ( -0.2f,  0.2f, 0.f );
		Point3f p3 = new Point3f (  0.2f,  0.2f, 0 );
		Point3f p4 = new Point3f (  0.2f, -0.2f, 0 );
		
		Point3f [] wireframePoints  = { p1, p2, p2, p3, p3, p4, p4, p1 };
	    LineArray wireframe = new LineArray ( wireframePoints.length, GeometryArray.COORDINATES);
	    wireframe.setCoordinates(0, wireframePoints );
	    
	    shape.addGeometry(wireframe);
	    return shape;
	}

	
	public Node getShape() {
		return createWireframe();
	}
}
