package ui;

import app.PhysUpdateListener;
import app.Simulation;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Timer;
import java.util.TimerTask;
import javax.swing.JFrame;
import model.SimulationModel;
import ui.infopanel.DrawFpsCounter;
import ui.infopanel.InfoPanel;

/**
 * A window holding the simulator
 */
public class SimulatorWindow implements PhysUpdateListener {

  private static final int DRAWING_FPS = 60;
  private static final int MIN_DRAWING_DELAY_MS = 1000 / DRAWING_FPS;

  private static final Dimension DEFAULT_WINDOW_SIZE = new Dimension(1024, 800);
  private static final Color BG_COLOR = new Color(0, 10, 20);

  private SimulatorWindowState state;
  private final JFrame frame;

  public SimulatorWindow(String title) {
    this(title, DEFAULT_WINDOW_SIZE);
  }

  private SimulatorWindow(String title, Dimension size) {
    this.frame = new JFrame(title);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    frame.setSize(size);
    frame.setLocationRelativeTo(null);
    DoubleBufferedPanel contentPanel = new DoubleBufferedPanel(
        makeInfoPanel(),
        (Graphics g) -> state.sim.draw(state.viewport, frame.getSize(), g));
    contentPanel.setBackground(BG_COLOR);

    frame.setContentPane(contentPanel);
  }

  private InfoPanel makeInfoPanel() {
    return InfoPanel.create(new DrawFpsCounter(), new DrawFpsCounter(), new DrawFpsCounter(), new DrawFpsCounter(), new DrawFpsCounter(), new DrawFpsCounter(), new DrawFpsCounter(), new DrawFpsCounter());
  }

  /**
   * Show the window & start the simulation
   */
  public SimulatorWindow initializeAndShow(Simulation sim) {
    this.state = SimulatorWindowState.create(sim);
    state.sim.initializeAndRun();
    frame.setVisible(true);

    Timer drawTimer = new Timer();
    drawTimer.schedule(new TimerTask() {
      @Override
      public void run() {
        ((DoubleBufferedPanel) frame.getContentPane())
            .paintComponent(frame.getContentPane().getGraphics());
      }
    }, 50, MIN_DRAWING_DELAY_MS);
    return this;
  }

  public void draw() {
    frame.repaint();
  }

  @Override
  public void onBeforePhysUpdate(SimulationModel currentState) {

  }

  @Override
  public void onAfterPhysUpdate(SimulationModel currentState) {

  }
}
