package ch.archilogic.object.format;

import java.io.FileNotFoundException;
import java.util.Hashtable;

import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;

public class ObjLoader {
	private Boolean noTriangulate = false;
	private Boolean noStripify = false;
	private double creaseAngle = 60.0;
	
	public void load(String fileName) {
		int flags = ObjectFile.RESIZE;
		if (!noTriangulate)
			flags |= ObjectFile.TRIANGULATE;
		if (!noStripify)
			flags |= ObjectFile.STRIPIFY;
		ObjectFile f = new ObjectFile(flags,
				(float) (creaseAngle * Math.PI / 180.0));
		Scene s = null;
		try {
			s = f.load(fileName);
		} catch (FileNotFoundException e) {
			System.err.println(e);
			System.exit(1);
		} catch (ParsingErrorException e) {
			System.err.println(e);
			System.exit(1);
		} catch (IncorrectFormatException e) {
			System.err.println(e);
			System.exit(1);
		}
		
		Hashtable namedObjects = s.getNamedObjects();
	}
}
