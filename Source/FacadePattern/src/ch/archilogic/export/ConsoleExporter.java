package ch.archilogic.export;

import javax.vecmath.Point3f;

import ch.archilogic.object.ObjectDef;
import ch.archilogic.object.ObjectGraph;

public class ConsoleExporter implements Exporter {
	public String getFileSuffix() {
		return null;
	}
	
	public void write(String filename, ObjectGraph objGraph) {
		System.out.println(String.format("exporting to console... "));
		
		ObjectDef obj = objGraph.getObject(0);
		System.out.println(String.format("num vertices: %d", obj.getVerticeNb()));
		System.out.println(String.format("num faces: %d", obj.getFaceNb()));
		
		// export vertices
		for (int i=0; i<obj.getVerticeNb();i++) {
			Point3f v = obj.getVertice(i);
			System.out.println(String.format("v: %f %f %f", v.x, v.y, v.z));
		}

		// export faces
		for (int i=0; i<obj.getFaceNb();i++) {
			int [] faceList = obj.getFace(i);
			System.out.println(String.format("f: %d %d %d %d", faceList[0], faceList[1], faceList[2], faceList[3]));
		}
	}
}
