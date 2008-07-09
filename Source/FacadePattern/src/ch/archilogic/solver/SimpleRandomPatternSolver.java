package ch.archilogic.solver;

import ch.archilogic.export.Exporter;
import ch.archilogic.object.ObjectGraph;

public class SimpleRandomPatternSolver implements Solver {
	private SolverState status = SolverState.INITIALIZING;
	private ObjectGraph objTree = null;
	
	public String getDescription() {
		return String.format("Simple Random Pattern Solver");
	}

	public ObjectGraph getObjectGraph() {
		return objTree;
	}
		
	public SolverState getStatus() {
		return status;
	}
	
	public void think() {
		status = SolverState.THINKING;
		
		status = SolverState.IDLE;
	}

	public void export(Exporter exporter, String filename) {
		exporter.write(filename, objTree);
	}
}
