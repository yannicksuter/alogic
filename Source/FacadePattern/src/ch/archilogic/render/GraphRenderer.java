package ch.archilogic.render;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.GraphicsConfiguration;

import javax.media.j3d.*;
import javax.vecmath.*;

import com.sun.j3d.utils.behaviors.mouse.*;
import com.sun.j3d.utils.universe.*;

import ch.archilogic.object.ObjectDef;
import ch.archilogic.object.ObjectGraph;
import ch.archilogic.runtime.exception.FaceException;
import ch.archilogic.solver.Solver;

public class GraphRenderer extends Canvas3D {
	private Solver solver = null;

	private static final long serialVersionUID = 1091353214144552939L;
	
	SimpleUniverse su = null;
    private Transform3D scale = new Transform3D();             
	
    private boolean optionAntialiasing = false;
    
	public boolean isOptionAntialiasing() {
		return optionAntialiasing;
	}

	public void setOptionAntialiasing(boolean optionAntialiasing) {
		this.optionAntialiasing = optionAntialiasing;
		su.getViewer().getView().setSceneAntialiasingEnable(optionAntialiasing);	
	}

	public void setSolver(Solver solver) {
		this.solver = solver;
	}
	
	public GraphRenderer(GraphicsConfiguration config) {
		super(config);
	}

	public void initialize() {
		su = new SimpleUniverse(this);
		su.getViewingPlatform().setNominalViewingTransform();
		
		// set option configurations
		setOptionAntialiasing(optionAntialiasing);

		BranchGroup bg = createSceneGraph(this);
		bg.compile();
		
		su.addBranchGraph(bg);
	}
	
	@Override
	public void preRender() {
		super.preRender();

		J3DGraphics2D g2D = getGraphics2D();
		Dimension d = getSize();
		
		GradientPaint gp = new GradientPaint(0, 0, new Color(192, 192, 172),
				d.width, d.height, new Color(113, 113, 100), true);
		g2D.setPaint(gp);
		g2D.fillRect(0, 0, d.width, d.height);

		if (solver != null) {
			g2D.setColor(Color.WHITE);
			int l = g2D.getFontMetrics().charWidth('-');
			int h = g2D.getFontMetrics().getHeight();
			g2D.drawString("ArchLogic::" + solver.getDescription(), l, h);
			g2D.drawString(solver.getStatus().getDescription(), l, h * 2 - 2);
		}

//		g2D.flush(true);
	}

	private BranchGroup createSceneGraph(Canvas3D cv) {
		BranchGroup root = new BranchGroup();
		TransformGroup spin = new TransformGroup();
		spin.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		spin.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		root.addChild(spin);
		
		// appearance
		Appearance ap = new Appearance();
		ap.setMaterial(new Material());
			
		try {
			ObjectGraph graph = solver.getObjectGraph();
			for (ObjectDef obj : graph.getObjects()) {
				spin.addChild(obj.getShape(true, false));
			}
		} catch (FaceException e) {
			e.printStackTrace();
		}

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

		scale.setScale(0.5);
		spin.setTransform(scale);

		// <background and light>
		BoundingSphere bound = new BoundingSphere();
		Background background = new Background(0.0f, 0.0f, 0.0f);
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
}
