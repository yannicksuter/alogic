package ch.archilogic.solver;

import com.sun.j3d.loaders.objectfile.ObjectFile;

import ch.archilogic.export.Exporter;
import ch.archilogic.object.ObjectGraph;

public interface Solver {
	public String getDescription();
	public SolverState getStatus();	
	public ObjectGraph getObjectGraph();
	
	// load reference object
	public void addReference(ObjectFile obj);
	
	// solve problem
	public void initialize();
	public void think();
	
	// export results
	public void export(Exporter exporter, String filename);
}
