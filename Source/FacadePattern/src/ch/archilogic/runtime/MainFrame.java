package ch.archilogic.runtime;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.*;

import ch.archilogic.export.ConsoleExporter;
import ch.archilogic.export.ObjExporter;
import ch.archilogic.export.ExtensionFileFilter;
import ch.archilogic.render.GraphRenderer;
import ch.archilogic.solver.SimpleRandomPatternSolver;
import ch.archilogic.solver.Solver;

import com.sun.j3d.utils.universe.*;

public class MainFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	
	private JPanel drawingPanel;
	private GraphRenderer renderer;
	private Solver solver;

    KeyListener keyListener = new KeyListener() {
        public void keyPressed(KeyEvent keyEvent) {
    		int keyCode = keyEvent.getKeyCode();
    	    if (keyCode == KeyEvent.VK_F1) {
    	    	// export object to DXF
    	    	ObjExporter exporter = new ObjExporter();
    	    	String fileName = ExtensionFileFilter.getFileName("c:\\", "Save object to DXF", exporter.getFileSuffix(), ExtensionFileFilter.SAVE);
    	    	if (fileName!=null)
    	    	solver.export(exporter, fileName);
    	    }
    	    else if (keyCode == KeyEvent.VK_F2) {
    	    	// export object to console (debugging)
    	    	solver.export(new ConsoleExporter(), null);
    	    }
        }

        public void keyReleased(KeyEvent keyEvent) {}
        public void keyTyped(KeyEvent keyEvent) {}
      };

      
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

		drawingPanel.setPreferredSize(new Dimension(800, 600));
		getContentPane().add(drawingPanel, BorderLayout.CENTER);

		pack();
	}

	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				// initialize the solver
				Solver solver = new SimpleRandomPatternSolver();
				solver.initialize();
				solver.think();
				
				MainFrame window = new MainFrame();				
				window.setSolver(solver);
				
				window.setVisible(true);
			}
		});
	}
}
