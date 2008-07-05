package ch.archilogic.runtime;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.KeyStroke;
import javax.vecmath.Point2f;

import ch.archilogic.export.Exporter;
import ch.archilogic.export.ExtensionFileFilter;
import ch.archilogic.export.TextExporter;
import ch.archilogic.object.Frame;
import ch.archilogic.object.geom.Rect;
import ch.archilogic.render.GraphRenderer;
import ch.archilogic.solver.Solver;
import ch.archilogic.solver.SimpleRandomPatternSolver;
import ch.archilogic.solver.SolverState;

public class Patternsolver extends JPanel implements ActionListener, Runnable {
	static Dimension wndDimension = new Dimension(800,600); 
	Solver solver = null;
	GraphRenderer renderer = new GraphRenderer();
	
	// construct the panel
	public Patternsolver(Solver solver) {
		this.solver = solver;  
		
		// initiate keyboard event handling
		registerKeyboardAction(this, "export", KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0), WHEN_IN_FOCUSED_WINDOW);
		addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                repaint();
           }
       });
	}

	public void run() {
		while (solver.getStatus() != SolverState.IDLE) {
			repaint();
		}
	}
	
	// paint the contents of the panel
	public void paint(Graphics g) {
		super.paint(g);
		Graphics2D g2d = (Graphics2D) g;
		wndDimension = getSize();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		paintBackground(g2d, wndDimension);
		
		if (solver != null && solver.getStatus() == SolverState.IDLE) {
			renderer.draw(solver.getObjectGraph());
		}
	}

	private void paintBackground(Graphics2D g2D, Dimension d) {
	     GradientPaint gp = new GradientPaint(0, 0, new Color(192, 192, 172), d.width, d.height, new Color(113, 113, 100), true);
	     g2D.setPaint(gp);
	     g2D.fillRect(0, 0, d.width, d.height);
	    
	     if (solver != null) {
		     g2D.setColor(Color.WHITE);
		     int l = g2D.getFontMetrics().charWidth('-');
		     int h = g2D.getFontMetrics().getHeight();
		     g2D.drawString("ArchLogic::"+solver.getDescription(), l, h);
		     g2D.drawString(solver.getStatus().getDescription(), l, h*2-2);
	     }
	}
	
	// respond to keyboard action
	public void actionPerformed(ActionEvent e) {
		if (solver.getStatus() == SolverState.IDLE) {
			Exporter exporter = new TextExporter();		
			String filename = ExtensionFileFilter.getFileName(System.getProperty("user.dir"), "Export Geometry (*."+exporter.getFileSuffix()+")", exporter.getFileSuffix(), ExtensionFileFilter.SAVE);			
			if (filename != null) {
				solver.export(exporter, filename);
			}			
		}
	}

	// the entry point for the application
	public static void main(String[] args) {
		// create the window
		JFrame f = new JFrame("Facade Pattern Solver");

		// create and add an event handler for window closing event
		f.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		// initialize the solver
		Solver solver = new SimpleRandomPatternSolver();
		Frame base = new Frame();
		base.setFrame(new Rect(new Point2f(4,0), new Point2f(0,10)));
		solver.setFrame(base);
		
		// add a banner panel to the window
		f.getContentPane().add(new Patternsolver(solver));
		f.pack();
		f.setSize(800, 600);
		f.setVisible(true);
		
		solver.think();
	}
}
