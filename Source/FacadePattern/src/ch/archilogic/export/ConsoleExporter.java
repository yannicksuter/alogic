package ch.archilogic.export;

import java.util.List;

import ch.archilogic.math.vector.Vector3D;
import ch.archilogic.object.Face;
import ch.archilogic.object.ObjectDef;

public class ConsoleExporter implements Exporter {
	public String getFileSuffix() {
		return null;
	}
	
	public void write(String filename, ObjectDef envelope) {
		System.out.println(String.format("exporting to console... "));
		
		System.out.println(String.format("num vertices: %d", envelope.getVerticeNb()));
		System.out.println(String.format("num faces: %d", envelope.getFaceNb()));
		
		// export vertices
		for (int i=0; i<envelope.getVerticeNb();i++) {
			Vector3D v = envelope.getVertice(i);
			System.out.println(String.format("v: %f %f %f", v.x, v.y, v.z));
		}

		// export faces
		for (int i=0; i<envelope.getFaceNb();i++) {
			Face face = envelope.getFace(i);		
			List<Integer> faceList = face.getIndices();
			
			switch (faceList.size()) {
			case 2:
				System.out.println(String.format("f: %d %d", faceList.get(0)+1, faceList.get(1)+1));
				break;
			case 3: 
				System.out.println(String.format("f: %d %d %d", faceList.get(0)+1, faceList.get(1)+1, faceList.get(2)+1));
				break;					
			case 4:
				System.out.println(String.format("f: %d %d %d %d", faceList.get(0)+1, faceList.get(1)+1, faceList.get(2)+1, faceList.get(3)+1));
				break;					
			case 5:
				System.out.println(String.format("f: %d %d %d %d %d", faceList.get(0)+1, faceList.get(1)+1, faceList.get(2)+1, faceList.get(3)+1, faceList.get(4)+1));
				break;					
			}
		}
	}
}
