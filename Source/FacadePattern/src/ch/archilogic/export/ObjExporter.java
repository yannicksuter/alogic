package ch.archilogic.export;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import ch.archilogic.object.ObjectGraph;

public class ObjExporter implements Exporter {
	public String getFileSuffix() {
		return "dxf";
	}

	public void write(String fileName, ObjectGraph objGraph) {
		try {
			FileWriter outFile = new FileWriter(fileName);
			PrintWriter out = new PrintWriter(outFile);
			
			// nur beispiele
			out.println("This is line 1");
			out.println(String.format("name %s, value_int %d, value float %f, value float %.2f", "yannick", 77, 1/3.0f, 1/3.0f));
			
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
