package ch.archilogic.export;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.vecmath.Point3f;

import ch.archilogic.object.Face;
import ch.archilogic.object.ObjectDef;
import ch.archilogic.object.ObjectGraph;

public class ObjExporter implements Exporter {
	public String getFileSuffix() {
		return "obj";
	}

	public void write(String filename, ObjectDef envelope) {
		try {
			FileWriter outFile = new FileWriter(filename);
			PrintWriter out = new PrintWriter(outFile);
			
			out.println("#Facade Pattern - 2008");
			
			out.println(String.format("#num vertices: %d", envelope.getVerticeNb()));
			out.println(String.format("#num faces: %d", envelope.getFaceNb()));
			
			// export vertices
			for (int i=0; i<envelope.getVerticeNb();i++) {
				Point3f v = envelope.getVertice(i);
				out.println(String.format("v %f %f %f", v.x, v.y, v.z));
			}

			// export faces
			for (int i=0; i<envelope.getFaceNb();i++) {
				Face face = envelope.getFace(i);		
				List<Integer> faceList = face.getIndices();
				out.println(String.format("f %d %d %d %d", faceList.get(0)+1, faceList.get(1)+1, faceList.get(2)+1, faceList.get(3)+1));
			}			
						
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
