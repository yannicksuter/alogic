package ch.archilogic.object.helper;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Enumeration;

import javax.media.j3d.GeometryArray;
import javax.media.j3d.Shape3D;

import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;

public class ObjHelper {
	private static boolean bInA(int b, int a) {
		return (a & b) == b;
	}

	public static Scene loadRefObject(String filename) 
		throws FileNotFoundException, IncorrectFormatException, ParsingErrorException, MalformedURLException 
	{
		Scene s = null;
		int flags = 0/*ObjectFile.RESIZE*/;
		
		ObjectFile f = new ObjectFile(flags);
		s = f.load(new URL(filename));
	
		return s; 
	}
	
	/**
	* convert Shape3D's geometryArrays  using GeometryInfo. 
	* @param shp - the Shape3D target
	* @param indexed - creates a indexed GeometryArray
	* @param compact - compact, if indexed.
	* @param byRef - create a BY_REFERENCE geometry
	* @param interleaved -  create a INTERLEAVED geometry
	* @param useCoordIndexOnly - force USE_COORD_INDEX_ONLY in indexed Geometries
	* @param nio - Use NIO buffers
	*/	
	@SuppressWarnings("unchecked")
	public static void convert(Shape3D shp, boolean indexed, boolean compact, boolean byRef, boolean interleaved, boolean useCoordIndexOnly, boolean nio) {
		Enumeration en = shp.getAllGeometries();
		while (en.hasMoreElements()) {
			GeometryArray geo = (GeometryArray) en.nextElement();
			shp.removeGeometry(geo);
			GeometryInfo gi = new GeometryInfo(geo);
			NormalGenerator ng = new NormalGenerator();
			ng.generateNormals(gi);
			GeometryArray ga;
			if (indexed) {
				ga = gi.getIndexedGeometryArray(compact, byRef, interleaved, useCoordIndexOnly, nio);
			} else {
				ga = gi.getGeometryArray(byRef, interleaved, nio);
			}
			shp.addGeometry(ga);
		}
	}	
	
	@SuppressWarnings("unchecked")
	public static void printInfo(Shape3D shp) {
		Enumeration en = shp.getAllGeometries();
		while (en.hasMoreElements()) {
			Object obj = en.nextElement();
			if (obj instanceof GeometryArray) {
				GeometryArray geo = (GeometryArray) obj; // enum.nextElement();
				System.out.println("Geometry " + geo.getClass().getName());

				int vertexFormat = geo.getVertexFormat();

				System.out.println("VertexCount: " + geo.getVertexCount());
				System.out.println("ValidVertexCount: " + geo.getValidVertexCount());

				boolean isReference = bInA(GeometryArray.BY_REFERENCE, vertexFormat);
				boolean isTC2 = bInA(GeometryArray.TEXTURE_COORDINATE_2, vertexFormat);
				boolean isTC3 = bInA(GeometryArray.TEXTURE_COORDINATE_3, vertexFormat);
				boolean isTC4 = bInA(GeometryArray.TEXTURE_COORDINATE_4, vertexFormat);
				boolean isInterleaved = bInA(GeometryArray.INTERLEAVED, vertexFormat);
				boolean isUSE_NIO_BUFFER = bInA(GeometryArray.USE_NIO_BUFFER, vertexFormat);
				boolean isUSE_COORD_INDEX_ONLY = bInA(GeometryArray.USE_COORD_INDEX_ONLY, vertexFormat);
				boolean isNORMALS = bInA(GeometryArray.NORMALS, vertexFormat);

				System.out.println("Is BY_REFERENCE: " + isReference);
				System.out.println("Is TEXCOORD2: " + isTC2);
				System.out.println("Is TEXCOORD3: " + isTC3);
				System.out.println("Is TEXCOORD4: " + isTC4);
				System.out.println("Is INTERLEAVED: " + isInterleaved);
				System.out.println("Is USE_NIO_BUFFER: " + isUSE_NIO_BUFFER);
				System.out.println("Is USE_COORD_INDEX_ONLY: " + isUSE_COORD_INDEX_ONLY);
				System.out.println("Has isNORMALS: " + isNORMALS);
			}
		}
	}

}
