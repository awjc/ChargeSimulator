package ui;

import app.Simulation;
import java.awt.Dimension;
import javax.swing.JFrame;

/**
 * A window holding the simulator
 */
public class SimulatorWindow {
  private static final Dimension DEFAULT_WINDOW_SIZE = new Dimension(1024, 800);

  private final Simulation sim;
  private final JFrame frame;

  public SimulatorWindow(Simulation sim, String title) {
    this(sim, title, DEFAULT_WINDOW_SIZE);
  }

  public SimulatorWindow(Simulation sim, String title, int width, int height) {
    this(sim, title, new Dimension(width, height));
  }

  public SimulatorWindow(Simulation sim, String title, Dimension size) {
    this.sim = sim;
    this.frame = new JFrame(title);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(size);
    frame.setLocationRelativeTo(null);
  }

  public SimulatorWindow show() {
    frame.setVisible(true);
    return this;
  }

}
