package ch.archilogic.solver;

import ch.archilogic.export.Exporter;
import ch.archilogic.object.ObjectGraph;

public interface Solver {
	public String getDescription();
	public SolverState getStatus();	
	public ObjectGraph getObjectGraph();
	
	// solve problem
	public void think();
	
	// export results
	public void export(Exporter exporter, String filename);
}
