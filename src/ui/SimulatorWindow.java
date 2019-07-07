package ui;

import app.Simulation;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * A window holding the simulator
 */
public class SimulatorWindow {
  private static final Dimension DEFAULT_WINDOW_SIZE = new Dimension(1024, 800);
  private static final Color BG_COLOR = new Color(0, 10, 20);

  private final Simulation sim;
  private final JPanel contentPanel;
  private final Viewport viewport;
  private final JFrame frame;

  public SimulatorWindow(Simulation sim, String title) {
    this(sim, title, DEFAULT_WINDOW_SIZE);
  }

  private SimulatorWindow(Simulation sim, String title, Dimension size) {
    this.sim = sim;
    this.viewport = Viewport.create();
    this.frame = new JFrame(title);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(size);
    frame.setLocationRelativeTo(null);
    this.contentPanel = new JPanel() {
      @Override
      public void paintComponent(Graphics g) {
        super.paintComponent(g);
        sim.draw(viewport, frame.getSize(), g);
      }
    };
    contentPanel.setBackground(BG_COLOR);
    frame.setContentPane(contentPanel);
  }

  /**
   * Show the window & start the simulation
   */
  public SimulatorWindow show() {
    sim.initializeAndRun();
    frame.setVisible(true);
    return this;
  }

  public void draw() {
    frame.repaint();
  }
}
