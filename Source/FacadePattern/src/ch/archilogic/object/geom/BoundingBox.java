package ch.archilogic.object.geom;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Hashtable;

import javax.media.j3d.Bounds;
import javax.media.j3d.Geometry;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TriangleArray;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;

import ch.archilogic.object.ObjectDef;
import ch.archilogic.object.helper.BoxBuilder;
import ch.archilogic.object.helper.ObjHelper;
import ch.archilogic.runtime.exception.FaceException;

import com.sun.j3d.loaders.Scene;

public class BoundingBox extends ObjectDef  {			
	private Point3d upper;
	private Point3d lower;
	
	@Override
	public void create() throws FaceException {
//		createFace(BoxBuilder.getFace(BoxBuilder.FACE.FRONT, upper, lower));
//		createFace(BoxBuilder.getFace(BoxBuilder.FACE.BACK, upper, lower));
//		createFace(BoxBuilder.getFace(BoxBuilder.FACE.LEFT, upper, lower));
//		createFace(BoxBuilder.getFace(BoxBuilder.FACE.RIGHT, upper, lower));
//		createFace(BoxBuilder.getFace(BoxBuilder.FACE.TOP, upper, lower));
//		createFace(BoxBuilder.getFace(BoxBuilder.FACE.BOTTOM, upper, lower));
	}
	
	public void setUpper(Point3d upper) {
		this.upper = upper;
	}

	public void setLower(Point3d lower) {
		this.lower = lower;
	}
}
