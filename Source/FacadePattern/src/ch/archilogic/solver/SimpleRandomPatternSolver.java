package ch.archilogic.solver;

import java.util.Hashtable;

import javax.media.j3d.Shape3D;
import javax.vecmath.Point3d;

import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;

import ch.archilogic.export.Exporter;
import ch.archilogic.object.ObjectGraph;
import ch.archilogic.object.geom.BBoxObj;
import ch.archilogic.object.geom.BoxObj;
import ch.archilogic.object.geom.GridObj;
import ch.archilogic.object.geom.ModelObj;
import ch.archilogic.object.helper.BoxHelper;
import ch.archilogic.object.helper.ObjHelper;
import ch.archilogic.runtime.exception.FaceException;

public class SimpleRandomPatternSolver implements Solver {
	private SolverState status = SolverState.INITIALIZING;
	private ObjectGraph objGraph = null;

	private static String refObjPath = "file:c:\\tmp\\loadme.obj";
	
	public String getDescription() {
		return String.format("Simple Random Pattern Solver");
	}

	public ObjectGraph getObjectGraph() {
		return objGraph;
	}
		
	public SolverState getStatus() {
		return status;
	}

	public void addReference(ObjectFile obj) {
	}
	
	public void initialize() {
		objGraph = new ObjectGraph();

		ModelObj object = null;
		BBoxObj box = new BBoxObj(BoxHelper.FRONT|BoxHelper.BACK|BoxHelper.LEFT|BoxHelper.RIGHT);
		Scene s = null;
		try {
			try {
				Point3d lower = new Point3d(); 
				Point3d upper = new Point3d();
				s = ObjHelper.loadRefObject(refObjPath);
				Hashtable<String,Shape3D> table = s.getNamedObjects();
				for (String key : table.keySet()) {
					Shape3D o = table.get(key);
					ObjHelper.printInfo(o);

					if (o.getBounds() instanceof javax.media.j3d.BoundingBox) {
						javax.media.j3d.BoundingBox b = (javax.media.j3d.BoundingBox) o.getBounds();
						b.getUpper(upper);
						b.getLower(lower);
						
						System.out.println("Bounds U: " + upper + " L: " + lower);
						box.setUpper(upper);
						box.setLower(lower);
					}
					// create new model to be shown
					object = new ModelObj((Shape3D)o.cloneTree());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			box.create();
		} catch (FaceException e) {
			e.printStackTrace();
		}
		
		if (object != null) {
			objGraph.addChild(object);
			objGraph.addChild(box);
		}
	}
	
	public void think() {
		status = SolverState.THINKING;

		// todo: first subdivision
		
		status = SolverState.IDLE;
	}

	public void export(Exporter exporter, String filename) {
		exporter.write(filename, objGraph);
	}
}
