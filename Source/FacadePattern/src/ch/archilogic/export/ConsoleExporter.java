package ch.archilogic.export;

import ch.archilogic.object.ObjectGraph;

public class ConsoleExporter implements Exporter {
	public String getFileSuffix() {
		return null;
	}
	
	public void write(String filename, ObjectGraph objTree) {
		System.out.println(String.format("exporting to console... "));
	}
}
