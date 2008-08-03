package ch.archilogic.solver;

import com.sun.j3d.loaders.objectfile.ObjectFile;

import ch.archilogic.export.Exporter;
import ch.archilogic.object.ObjectGraph;
import ch.archilogic.object.geom.GridObj;

public class SimpleRandomPatternSolver implements Solver {
	private SolverState status = SolverState.INITIALIZING;
	private ObjectGraph objGraph = null;
	
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

		// test
		objGraph.addChild(new GridObj());
	}
	
	public void think() {
		status = SolverState.THINKING;
				
		status = SolverState.IDLE;
	}

	public void export(Exporter exporter, String filename) {
		exporter.write(filename, objGraph);
	}
}
