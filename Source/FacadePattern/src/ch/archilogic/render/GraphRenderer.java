package ch.archilogic.render;

import java.awt.Color;
import java.awt.GraphicsConfiguration;

import javax.media.j3d.*;
import javax.vecmath.*;

import com.sun.j3d.utils.behaviors.mouse.*;
import com.sun.j3d.utils.universe.*;

import ch.archilogic.object.ObjectDef;
import ch.archilogic.object.graph.ObjectGraph;
import ch.archilogic.runtime.exception.FaceException;
import ch.archilogic.solver.Solver;

public class GraphRenderer extends Canvas3D {
	private Solver solver = null;

	private static final long serialVersionUID = 1091353214144552939L;
	
	SimpleUniverse universe = null;
	BranchGroup branchGrp = null;
	TransformGroup objGrp = null;
	
    private boolean optionAntialiasing = false;

	public boolean isOptionAntialiasing() {
		return optionAntialiasing;
	}

	public void setOptionAntialiasing(boolean optionAntialiasing) {
		this.optionAntialiasing = optionAntialiasing;
		universe.getViewer().getView().setSceneAntialiasingEnable(optionAntialiasing);	
	}

	public void setSolver(Solver solver) {
		this.solver = solver;
	}
	
	public GraphRenderer(GraphicsConfiguration config) {
		super(config);

		universe = new SimpleUniverse(this);
		universe.getViewingPlatform().setNominalViewingTransform();		
	}

	public void initialize() {
		if (branchGrp != null)
			return;
		
		this.stopRenderer();
		
		// set option configurations
		setOptionAntialiasing(optionAntialiasing);

		if (branchGrp != null) {
			universe.getLocale().removeBranchGraph(branchGrp);			
		}
				
		branchGrp = createSceneGraph(this);
		addObjectsToGroup();
		branchGrp.compile();
		
		universe.addBranchGraph(branchGrp);
		
		this.startRenderer();
	}
	
	public void updateSceneGraphObjects() {
		addObjectsToGroup();		
	}	
	
	private BranchGroup createSceneGraph(Canvas3D cv) {	
		BranchGroup root = new BranchGroup();
				
		// mouse spin
		TransformGroup spin = new TransformGroup();
		spin.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		spin.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		root.addChild(spin);

		// set translation
		objGrp = new TransformGroup();
		//BranchGroup.ALLOW_DETACH
		objGrp.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		objGrp.setCapability(TransformGroup.ALLOW_CHILDREN_EXTEND);
		objGrp.setCapability(TransformGroup.ALLOW_CHILDREN_READ);
		objGrp.setCapability(TransformGroup.ALLOW_CHILDREN_WRITE);
		spin.addChild(objGrp);
		
		// appearance
		Appearance ap = new Appearance();
		ap.setMaterial(new Material());
			
		// rotation
		MouseRotate rotator = new MouseRotate(spin);
		BoundingSphere bounds = new BoundingSphere();
		rotator.setSchedulingBounds(bounds);
		spin.addChild(rotator);

		// translation
		MouseTranslate translator = new MouseTranslate(spin);
		translator.setSchedulingBounds(bounds);
		spin.addChild(translator);

		// zoom
		MouseZoom zoom = new MouseZoom(spin);
		zoom.setSchedulingBounds(bounds);
		spin.addChild(zoom);

		Transform3D scale = new Transform3D();             
		scale.setScale(solver.getScale());
		spin.setTransform(scale);

		// <background and light>
		BoundingSphere bound = new BoundingSphere();
		Background background = new Background(0.1f, 0.15f, 0.11f);
		background.setApplicationBounds(bound);
		root.addChild(background);
		AmbientLight light = new AmbientLight(true, new Color3f(Color.cyan));

		light.setInfluencingBounds(bound);
		root.addChild(light);
		PointLight ptlight = new PointLight(new Color3f(Color.blue),
				new Point3f(3f, 3f, 3f), new Point3f(1f, 0f, 0f));
		ptlight.setInfluencingBounds(bound);
		root.addChild(ptlight);

		PointLight ptlight2 = new PointLight(new Color3f(Color.yellow),
				new Point3f(3f, 3f, 3f), new Point3f(1f, 0f, 0f));
		ptlight2.setInfluencingBounds(bound);
		root.addChild(ptlight2);
		// </background and light>

		return root;
	}
	
	private void addObjectsToGroup() {
		ObjectGraph graph = solver.getObjectGraph();
		if (objGrp != null && graph != null) {
			
			Transform3D trans3D = new Transform3D();
			trans3D.setTranslation(graph.getTranslation().getVector3f());
			objGrp.setTransform(trans3D);
			
			
			BranchGroup newObjectGrp = new BranchGroup();
			newObjectGrp.setCapability(BranchGroup.ALLOW_DETACH);
			try {			
				// load objects
				for (ObjectDef obj : graph.getObjects()) {
					if (obj.isVisible()) 
					{ // only show visible objects
						Shape3D shp = obj.getShape(true, false);
						if (shp != null) {
							newObjectGrp.addChild(shp);
						}
					}
				}
			} catch (FaceException e) {
				e.printStackTrace();
			}
	
			objGrp.removeAllChildren();
			objGrp.addChild(newObjectGrp);
//			universe.getLocale().repalceBrachGraph(objGrp, newLandscape);

			
		}
	}
	
}
