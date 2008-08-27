package ch.archilogic.solver.config;

import ch.archilogic.log.Logger;
import ch.archilogic.math.vector.Vector3D;
import ch.archilogic.solver.SimpleRandomPatternSolver.ThinkType;

public enum ConfigType {
	MODEL_F1_5("Hauptteil Findling 1,4,5"),
	MODEL_F2("Hauptteil Findling 2"),
	MODEL_F3("Hauptteil Findling 3"),
	MODEL_DACH_F1_5("Dachstück Findling 1,4,5"),
	MODEL_DACH_F2("Dachstück Findling 2"),
	MODEL_DACH_F3("Dachstück Findling 3");

	private ConfigType(String desc) {
		this.desc = desc;
	}
	
	private String desc;	
	public String getDesc() {
		return desc;
	}

	public static Config getConfig(ConfigType type) {
		Config conf = new Config(type);
		
		if (type != null) {
			Logger.info(String.format("Loading config: %s", type.getDesc()));
		}
		
		switch(type) {
		case MODEL_F1_5:			
			conf.setRefObjPath("file:c:\\tmp\\loadme_f1.obj");
			conf.setUseEdgeId(1);
			conf.setUseEdgeDir(new Vector3D(0,1,0));
			conf.setConsiderCorner(false);
			conf.setEvaluateCorner(false);
			conf.setFindMaxNbEdges(2);
			conf.setUseThinkModel(ThinkType.CYLINDRIC);
			break;
		case MODEL_F2:			
			conf.setRefObjPath("file:c:\\tmp\\loadme_f2.obj");
			conf.setUseEdgeId(0);
			conf.setUseEdgeDir(null);
			conf.setConsiderCorner(true);
			conf.setEvaluateCorner(true);
			conf.setFindMaxNbEdges(2);
			conf.setUseThinkModel(ThinkType.CYLINDRIC);
			break;
		case MODEL_F3:			
			conf.setRefObjPath("file:c:\\tmp\\loadme_f3.obj");
			conf.setUseEdgeId(0);
			conf.setUseEdgeDir(new Vector3D(0,-1,0));
			conf.setConsiderCorner(false);
			conf.setEvaluateCorner(false);
			conf.setFindMaxNbEdges(2);
			conf.setUseThinkModel(ThinkType.CYLINDRIC);
			break;
		case MODEL_DACH_F1_5:
			conf.setRefObjPath("file:c:\\tmp\\loadme_f15_dach_voll.obj");
			conf.setUseThinkModel(ThinkType.FLAT);
			break;
		case MODEL_DACH_F2:
			conf.setRefObjPath("file:c:\\tmp\\loadme_f2_dach_voll.obj");
			conf.setUseThinkModel(ThinkType.FLAT);
			break;
		case MODEL_DACH_F3:
			conf.setRefObjPath("file:c:\\tmp\\loadme_f3_dach_voll.obj");
			conf.setUseThinkModel(ThinkType.FLAT);
			break;
		}
		return conf;
	}	
}
