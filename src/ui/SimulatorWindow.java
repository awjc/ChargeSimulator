package ui;

import app.Simulation;
import java.awt.Color;
import java.awt.Dimension;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * A window holding the simulator
 */
public class SimulatorWindow {

  private static final int DRAWING_FPS = 60;
  private static final int MIN_DRAWING_DELAY_MS = 1000 / DRAWING_FPS;

  private static final Dimension DEFAULT_WINDOW_SIZE = new Dimension(1024, 800);
  private static final Color BG_COLOR = new Color(0, 10, 20);

  private final Simulation sim;
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
    JPanel contentPanel = new DoubleBufferedPanel(g -> sim.draw(viewport, frame.getSize(), g));
    contentPanel.setBackground(BG_COLOR);
    frame.setContentPane(contentPanel);
  }

  /**
   * Show the window & start the simulation
   */
  public SimulatorWindow show() {
    sim.initializeAndRun();
    frame.setVisible(true);

    Timer drawTimer = new Timer();
    drawTimer.schedule(new TimerTask() {
      @Override
      public void run() {
        ((DoubleBufferedPanel) frame.getContentPane())
            .paintComponent(frame.getContentPane().getGraphics());
      }
    }, 0, MIN_DRAWING_DELAY_MS);
    return this;
  }

  public void draw() {
    frame.repaint();
  }
}
