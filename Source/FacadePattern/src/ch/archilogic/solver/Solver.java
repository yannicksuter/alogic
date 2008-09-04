package ch.archilogic.solver;

import ch.archilogic.export.Exporter;
import ch.archilogic.object.graph.ObjectGraph;
import ch.archilogic.runtime.exception.FaceException;
import ch.archilogic.solver.config.Config;

public interface Solver {
	public String getDescription();
	public SolverState getStatus();	

	public double getScale();
	public ObjectGraph getObjectGraph();
	
	// solve problem
	public void initialize(Config conf) throws FaceException;
	public void think() throws FaceException;
	
	// get information
	public double getQuadSizeAvg();
	
	// export results
	public void export(Exporter exporter, String filename);
}
