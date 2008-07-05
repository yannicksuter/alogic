package ch.archilogic.solver;

public enum SolverState {
	INITIALIZING("initializing the solver..."),
	THINKING("thinking..."),
	IDLE("job is done.");

	private String desc = "no description...";
	
	private SolverState(String desc) {
		this.desc = desc;
	}
	public String getDescription() {
		return this.desc;
	}
}
