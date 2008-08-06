package ch.archilogic.object.geom;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.media.j3d.Appearance;
import javax.media.j3d.ColoringAttributes;
import javax.media.j3d.Shape3D;
import javax.media.j3d.TriangleArray;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;

import org.omg.CORBA.ObjectHelper;

import ch.archilogic.object.ObjectDef;
import ch.archilogic.object.helper.ObjHelper;
import ch.archilogic.runtime.exception.FaceException;
import ch.archilogic.runtime.exception.ObjectException;

public class RefModelObj extends ObjectDef {
	
	@SuppressWarnings("unchecked")
	public RefModelObj(Shape3D shape) throws ObjectException, FaceException {
		super();

		ObjHelper.convert(shape, false, false, false, false, false, false);
		
		Enumeration en = shape.getAllGeometries();
		while (en.hasMoreElements()) {
			Object obj = en.nextElement();		
			if (obj instanceof TriangleArray) {
				TriangleArray a = (TriangleArray)obj;
				List<Point3f> l = new ArrayList<Point3f>();
				Point3f p1 = new Point3f();
				Point3f p2 = new Point3f();
				Point3f p3 = new Point3f();
				for (int i = 0; i < a.getVertexCount()/3; i++) {
					l.clear();
					a.getCoordinate(i*3+0, p1);
					l.add(p1);
					a.getCoordinate(i*3+1, p2);
					l.add(p2);
					a.getCoordinate(i*3+2, p3);
					l.add(p3);
					createFace(l);
				}
			} else {
				throw new ObjectException("object could not be parsed.");
			}
		}
		System.out.println(toString());
	}

//	@Override
//	public Shape3D getShape(boolean asWireframe, boolean asSolid) throws FaceException {
//        Appearance app = new Appearance();
//        ColoringAttributes catt = new ColoringAttributes();
//        catt.setColor(new Color3f(Color.blue));
//        app.setColoringAttributes(catt);
//        TriangleArray triangleArray=new TriangleArray(6,TriangleArray.COORDINATES|TriangleArray.COLOR_3);
//        triangleArray.setCoordinate(0,new Point3f(0.0f,0.0f,0.0f));
//        triangleArray.setCoordinate(1,new Point3f(2.0f,0.0f,0.0f));
//        triangleArray.setCoordinate(2,new Point3f(0.0f,0.0f,2.0f));
//        triangleArray.setCoordinate(3,new Point3f(0.0f,0.0f,2.0f));
//        triangleArray.setCoordinate(4,new Point3f(2.0f,0.0f,0.0f));
//        triangleArray.setCoordinate(5,new Point3f(2.0f,0.0f,2.0f));
//        Color3f red = new Color3f(1.0f, 0.0f, 0.0f);
//        for (int i=0;i<6;i++)
//        triangleArray.setColor(i,red);
//        
//        return new Shape3D(triangleArray,app);		
//	}
}
