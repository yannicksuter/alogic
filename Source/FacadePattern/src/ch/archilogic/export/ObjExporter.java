package ch.archilogic.export;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import javax.vecmath.Point3f;

import ch.archilogic.object.ObjectDef;
import ch.archilogic.object.ObjectGraph;

public class ObjExporter implements Exporter {
	public String getFileSuffix() {
		return "obj";
	}

	public void write(String fileName, ObjectGraph objGraph) {
		try {
			FileWriter outFile = new FileWriter(fileName);
			PrintWriter out = new PrintWriter(outFile);
			
			// nur beispiele
			//out.println("This is line 1");
			//out.println(String.format("name %s, value_int %d, value float %f, value float %.2f", "yannick", 77, 1/3.0f, 1/3.0f));
			out.println("#Facade Pattern - 2008");
			
			ObjectDef obj = objGraph.getObject(0);
			out.println(String.format("#num vertices: %d", obj.getVerticeNb()));
			out.println(String.format("#num faces: %d", obj.getFaceNb()));
			
			// export vertices
			for (int i=0; i<obj.getVerticeNb();i++) {
				Point3f v = obj.getVertice(i);
				out.println(String.format("v %f %f %f", v.x, v.y, v.z));
			}

			// export faces
			for (int i=0; i<obj.getFaceNb();i++) {
				int [] faceList = obj.getFace(i);
				out.println(String.format("f %d %d %d %d", faceList[0]+1, faceList[1]+1, faceList[2]+1, faceList[3]+1));
			}
			
						
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
