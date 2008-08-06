package ch.archilogic.runtime;

import java.awt.*;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.media.j3d.Alpha;
import javax.media.j3d.AmbientLight;
import javax.media.j3d.Background;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Canvas3D;
import javax.media.j3d.DirectionalLight;
import javax.media.j3d.RotationInterpolator;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.swing.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

import com.sun.j3d.loaders.IncorrectFormatException;
import com.sun.j3d.loaders.ParsingErrorException;
import com.sun.j3d.loaders.Scene;
import com.sun.j3d.loaders.objectfile.ObjectFile;
import com.sun.j3d.utils.behaviors.vp.OrbitBehavior;
import com.sun.j3d.utils.universe.*;

@SuppressWarnings("serial")
public class ObjectLoader extends JFrame {
	private JPanel drawingPanel;

	public ObjectLoader(String[] args) {
		initComponents();

		objLoad(args);
		init();

		// Create Canvas3D and SimpleUniverse; add canvas to drawing panel
		@SuppressWarnings("unused")
		GraphicsConfiguration config = SimpleUniverse
				.getPreferredConfiguration();
		// renderer = new GraphRenderer(config);
		// drawingPanel.add(renderer, BorderLayout.CENTER);
	}

	private void initComponents() {
		drawingPanel = new JPanel();

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle("Facade Pattern Solver");
		drawingPanel.setLayout(new java.awt.BorderLayout());

		drawingPanel.setPreferredSize(new Dimension(800, 600));
		getContentPane().add(drawingPanel, BorderLayout.CENTER);

		pack();
	}

	private boolean spin = false;

	private boolean noTriangulate = false;

	private boolean noStripify = false;

	private double creaseAngle = 60.0;

	private URL filename = null;

	private SimpleUniverse u;

	private BoundingSphere bounds;

	public BranchGroup createSceneGraph() {
		// Create the root of the branch graph
		BranchGroup objRoot = new BranchGroup();

		// Create a Transformgroup to scale all objects so they
		// appear in the scene.
		TransformGroup objScale = new TransformGroup();
		Transform3D t3d = new Transform3D();
		t3d.setScale(0.7);
		objScale.setTransform(t3d);
		objRoot.addChild(objScale);

		// Create the transform group node and initialize it to the
		// identity. Enable the TRANSFORM_WRITE capability so that
		// our behavior code can modify it at runtime. Add it to the
		// root of the subgraph.
		TransformGroup objTrans = new TransformGroup();
		objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
		objTrans.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
		objScale.addChild(objTrans);

		int flags = ObjectFile.RESIZE;
		if (!noTriangulate)
			flags |= ObjectFile.TRIANGULATE;
		if (!noStripify)
			flags |= ObjectFile.STRIPIFY;
		ObjectFile f = new ObjectFile(flags,
				(float) (creaseAngle * Math.PI / 180.0));
		Scene s = null;
		try {
			s = f.load(filename);
		} catch (FileNotFoundException e) {
			System.err.println(e);
			System.exit(1);
		} catch (ParsingErrorException e) {
			System.err.println(e);
			System.exit(1);
		} catch (IncorrectFormatException e) {
			System.err.println(e);
			System.exit(1);
		}

		objTrans.addChild(s.getSceneGroup());

		bounds = new BoundingSphere(new Point3d(0.0, 0.0, 0.0), 100.0);

		if (spin) {
			Transform3D yAxis = new Transform3D();
			Alpha rotationAlpha = new Alpha(-1, Alpha.INCREASING_ENABLE, 0, 0,
					4000, 0, 0, 0, 0, 0);

			RotationInterpolator rotator = new RotationInterpolator(
					rotationAlpha, objTrans, yAxis, 0.0f,
					(float) Math.PI * 2.0f);
			rotator.setSchedulingBounds(bounds);
			objTrans.addChild(rotator);
		}

		// Set up the background
		Color3f bgColor = new Color3f(0.05f, 0.05f, 0.5f);
		Background bgNode = new Background(bgColor);
		bgNode.setApplicationBounds(bounds);
		objRoot.addChild(bgNode);

		return objRoot;
	}

	private void usage() {
		System.out
				.println("Usage: java ObjLoad [-s] [-n] [-t] [-c degrees] <.obj file>");
		System.out.println("  -s Spin (no user interaction)");
		System.out.println("  -n No triangulation");
		System.out.println("  -t No stripification");
		System.out
				.println("  -c Set crease angle for normal generation (default is 60 without");
		System.out
				.println("     smoothing group info, otherwise 180 within smoothing groups)");
		System.exit(0);
	} // End of usage

	public void init() {
		// if (filename == null) {
		// // Applet
		// try {
		// URL path = getCodeBase();
		// filename = new URL(path.toString() + "./galleon.obj");
		// } catch (MalformedURLException e) {
		// System.err.println(e);
		// System.exit(1);
		// }
		// }

		setLayout(new BorderLayout());
		GraphicsConfiguration config = SimpleUniverse
				.getPreferredConfiguration();

		Canvas3D c = new Canvas3D(config);
		add("Center", c);

		// Create a simple scene and attach it to the virtual universe
		BranchGroup scene = createSceneGraph();
		u = new SimpleUniverse(c);

		// add mouse behaviors to the ViewingPlatform
		ViewingPlatform viewingPlatform = u.getViewingPlatform();

		PlatformGeometry pg = new PlatformGeometry();

		// Set up the ambient light
		Color3f ambientColor = new Color3f(0.1f, 0.1f, 0.1f);
		AmbientLight ambientLightNode = new AmbientLight(ambientColor);
		ambientLightNode.setInfluencingBounds(bounds);
		pg.addChild(ambientLightNode);

		// Set up the directional lights
		Color3f light1Color = new Color3f(1.0f, 1.0f, 0.9f);
		Vector3f light1Direction = new Vector3f(1.0f, 1.0f, 1.0f);
		Color3f light2Color = new Color3f(1.0f, 1.0f, 1.0f);
		Vector3f light2Direction = new Vector3f(-1.0f, -1.0f, -1.0f);

		DirectionalLight light1 = new DirectionalLight(light1Color,
				light1Direction);
		light1.setInfluencingBounds(bounds);
		pg.addChild(light1);

		DirectionalLight light2 = new DirectionalLight(light2Color,
				light2Direction);
		light2.setInfluencingBounds(bounds);
		pg.addChild(light2);

		viewingPlatform.setPlatformGeometry(pg);

		// This will move the ViewPlatform back a bit so the
		// objects in the scene can be viewed.
		viewingPlatform.setNominalViewingTransform();

		if (!spin) {
			OrbitBehavior orbit = new OrbitBehavior(c,
					OrbitBehavior.REVERSE_ALL);
			BoundingSphere bounds = new BoundingSphere(new Point3d(0.0, 0.0,
					0.0), 100.0);
			orbit.setSchedulingBounds(bounds);
			viewingPlatform.setViewPlatformBehavior(orbit);
		}

		u.addBranchGraph(scene);
	}

	// Caled if running as a program
	public void objLoad(String[] args) {
		if (args.length != 0) {
			for (int i = 0; i < args.length; i++) {
				if (args[i].startsWith("-")) {
					if (args[i].equals("-s")) {
						spin = true;
					} else if (args[i].equals("-n")) {
						noTriangulate = true;
					} else if (args[i].equals("-t")) {
						noStripify = true;
					} else if (args[i].equals("-c")) {
						if (i < args.length - 1) {
							creaseAngle = (new Double(args[++i])).doubleValue();
						} else
							usage();
					} else {
						usage();
					}
				} else {
					try {
						if ((args[i].indexOf("file:") == 0)
								|| (args[i].indexOf("http") == 0)) {
							filename = new URL(args[i]);
						} else if (args[i].charAt(0) != '/') {
							filename = new URL("file:./" + args[i]);
						} else {
							filename = new URL("file:" + args[i]);
						}
					} catch (MalformedURLException e) {
						System.err.println(e);
						System.exit(1);
					}
				}
			}
		}
	}

	public static void main(final String args[]) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				ObjectLoader window = new ObjectLoader(args);
				window.setVisible(true);
			}
		});
	}


//	@Override
//	public Shape3D getShape(boolean asWireframe, boolean asSolid) throws FaceException {
//        Appearance app = new Appearance();
//        ColoringAttributes catt = new ColoringAttributes();
//        catt.setColor(new Color3f(Color.blue));
//        app.setColoringAttributes(catt);
//        TriangleArray triangleArray=new TriangleArray(6,TriangleArray.COORDINATES|TriangleArray.COLOR_3);
//        triangleArray.setCoordinate(0,new Point3f(0.0f,0.0f,0.0f));
//        triangleArray.setCoordinate(1,new Point3f(2.0f,0.0f,0.0f));
//        triangleArray.setCoordinate(2,new Point3f(0.0f,0.0f,2.0f));
//        triangleArray.setCoordinate(3,new Point3f(0.0f,0.0f,2.0f));
//        triangleArray.setCoordinate(4,new Point3f(2.0f,0.0f,0.0f));
//        triangleArray.setCoordinate(5,new Point3f(2.0f,0.0f,2.0f));
//        Color3f red = new Color3f(1.0f, 0.0f, 0.0f);
//        for (int i=0;i<6;i++)
//        triangleArray.setColor(i,red);
//        
//        return new Shape3D(triangleArray,app);		
//	}

}
