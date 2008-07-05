package ch.archilogic.export;

import ch.archilogic.object.ObjectGraph;

public class TextExporter implements Exporter {
	public String getFileSuffix() {
		return "txt";
	}
	
	public void write(String filename, ObjectGraph objTree) {
		System.out.println(String.format("exporting... %s", filename));
	}
}
