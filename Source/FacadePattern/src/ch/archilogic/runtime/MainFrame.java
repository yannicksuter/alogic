package ch.archilogic.runtime;

/*
 * HelloUniverse.java
 * Copyright (c) 2006 Sun Microsystems, Inc.

 * Simple Java 3D example program to display a spinning cube.
 */

import java.awt.*;
import javax.swing.*;
import javax.vecmath.Point2f;

import ch.archilogic.object.geom.Rect;
import ch.archilogic.render.GraphRenderer;
import ch.archilogic.solver.SimpleRandomPatternSolver;
import ch.archilogic.solver.Solver;

import com.sun.j3d.utils.universe.*;

public class MainFrame extends JFrame {
	private JPanel drawingPanel;
	private GraphRenderer renderer;
	private Solver solver;

	public void setSolver(Solver solver) {
		this.solver = solver;
		if (renderer != null) {
			renderer.setSolver(solver);
		}
	}

	public MainFrame() {
		initComponents();

		// Create Canvas3D and SimpleUniverse; add canvas to drawing panel
		GraphicsConfiguration config = SimpleUniverse.getPreferredConfiguration();
		renderer = new GraphRenderer(config);

		drawingPanel.add(renderer, BorderLayout.CENTER);
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

	public static void main(String args[]) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				// initialize the solver
				Solver solver = new SimpleRandomPatternSolver();
				
				MainFrame window = new MainFrame();
				window.setVisible(true);
				window.setSolver(solver);
			}
		});
	}
}
