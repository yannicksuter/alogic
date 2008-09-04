package ch.archilogic.runtime;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;

import ch.archilogic.dialog.SelectConfigDlg;
import ch.archilogic.export.ConsoleExporter;
import ch.archilogic.export.ObjExporter;
import ch.archilogic.export.ExtensionFileFilter;
import ch.archilogic.math.Rnd;
import ch.archilogic.object.graph.GraphObjectType;
import ch.archilogic.object.graph.ObjectGraph;
import ch.archilogic.render.GraphRenderer;
import ch.archilogic.runtime.exception.FaceException;
import ch.archilogic.solver.CylindricFlatSolverImpl;
import ch.archilogic.solver.Solver;

import com.sun.j3d.utils.universe.*;

public class MainFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	static final int windowWidth = 800;
	static final int windowHeight = 600;
	
	private JPanel drawingPanel;
	private GraphRenderer renderer;
	private Solver solver;

    KeyListener keyListener = new KeyListener() {
        public void keyPressed(KeyEvent keyEvent) {
    		int keyCode = keyEvent.getKeyCode();
    	    if (keyCode == KeyEvent.VK_F1) {
    	    	// export object to DXF
    	    	ObjExporter exporter = new ObjExporter();
    	    	String fileName = ExtensionFileFilter.getFileName("c:\\", "Save object to OBJ", exporter.getFileSuffix(), ExtensionFileFilter.SAVE);
    	    	if (fileName!=null)
    	    	solver.export(exporter, fileName);
    	    }
    	    else if (keyCode == KeyEvent.VK_F2) {
    	    	// export object to console (debugging)
    	    	solver.export(new ConsoleExporter(), null);
    	    }
    	    else if (keyCode == KeyEvent.VK_A) {
    	    	renderer.setOptionAntialiasing(!renderer.isOptionAntialiasing());
    	    }
    	    // change visibility
    	    else if (keyCode == KeyEvent.VK_F9) {
    	    	switchVisibility(GraphObjectType.OBJ_LOCKED_VERTICES);
    	    }    	    
    	    else if (keyCode == KeyEvent.VK_F10) {
    	    	switchVisibility(GraphObjectType.OBJ_REFERENCE);
    	    }    	    
    	    else if (keyCode == KeyEvent.VK_F11) {
    	    	switchVisibility(GraphObjectType.OBJ_EDGES);
    	    }    	    
        }

        public void keyReleased(KeyEvent keyEvent) {}
        public void keyTyped(KeyEvent keyEvent) {}
      };

    private void switchVisibility(GraphObjectType graphObject) {
    	// switch flag
    	ObjectGraph graph = solver.getObjectGraph();
    	boolean isVisible = graph.isVisible(graphObject);
    	graph.setVisible(graphObject, !isVisible);
    	
    	// reinit renderer
    	renderer.updateSceneGraphObjects();
    }
      
	public void setSolver(Solver solver) {
		this.solver = solver;
		if (renderer != null) {
			renderer.setSolver(solver);
			renderer.initialize();
		}
	}

	public MainFrame() {
		initComponents();

		// Create Canvas3D and SimpleUniverse; add canvas to drawing panel
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		renderer = new GraphRenderer(config);
		renderer.addKeyListener(keyListener);

		drawingPanel.add(renderer, BorderLayout.CENTER);
	}

	private void initComponents() {
		drawingPanel = new JPanel();
		drawingPanel.addKeyListener(keyListener);

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setTitle("Facade Pattern Solver");
		drawingPanel.setLayout(new java.awt.BorderLayout());

		drawingPanel.setPreferredSize(new Dimension(windowWidth, windowHeight));
		getContentPane().add(drawingPanel, BorderLayout.CENTER);

		pack();
	}

	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				Rnd.init();
				
				// select config
				SelectConfigDlg dlg = SelectConfigDlg.create();
				if (!dlg.isCanceled()) {
					// initialize the solver
					Solver solver = new CylindricFlatSolverImpl();
					
					try {
						solver.initialize(dlg.getConfig());
						solver.think();
					} catch (FaceException e) {
						e.printStackTrace();
					}
					
					MainFrame window = new MainFrame();				
					window.setTitle("Facade Pattern Solver - avg area=" + solver.getQuadSizeAvg());
					window.setSolver(solver);				
					
					window.setVisible(true);
				} else
				{
					System.exit(0);
				}
			}
		});
	}
}
