package ch.archilogic.export;

import ch.archilogic.object.ObjectDef;

public interface Exporter {
	public String getFileSuffix();
	public void write(String filename, ObjectDef objEnvelope);
}
