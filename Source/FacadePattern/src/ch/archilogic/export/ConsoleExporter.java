package ch.archilogic.export;

import java.util.List;

import javax.vecmath.Point3f;

import ch.archilogic.object.Face;
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
			Face face = obj.getFace(i);		
			List<Integer> faceList = face.getIndices();
			System.out.println(String.format("f: %d %d %d %d", faceList.get(0)+1, faceList.get(1)+1, faceList.get(2)+1, faceList.get(3)+1));
		}
	}
}
