package ch.archilogic.export;

import ch.archilogic.object.ObjectGraph;

public interface Exporter {
	public String getFileSuffix();
	public void write(String filename, ObjectGraph objTree);
}
