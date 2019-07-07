package app;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Timer;
import java.util.TimerTask;
import model.Animatable;
import model.SimulationModel;
import ui.Viewport;

public class Simulation {
  private static final int UPDATE_FPS = 60;
  private static final int MIN_UPDATE_DELAY_MS = 1000 / UPDATE_FPS;

  private SimulationModel model;
  private long startTimeMillis;
  private long lastUpdateTimeMillis;

  public Simulation() {
    this.model = SimulationModel.createEmpty();
  }

  public void initializeAndRun() {
    this.startTimeMillis = System.currentTimeMillis();
    this.lastUpdateTimeMillis = startTimeMillis;

    Timer updateTimer = new Timer();
    updateTimer.schedule(new TimerTask() {
      @Override
      public void run() {
        update();
      }
    }, 0, MIN_UPDATE_DELAY_MS);
  }

  /** Draws all the drawables in the model */
  public void draw(Viewport viewport, Dimension frameSize, Graphics g) {
    model.getDrawables().forEach(d -> d.draw(viewport, frameSize, g));
  }

  public void update() {
    final long currentTimeMillis = System.currentTimeMillis();
    long deltaTimeMs = currentTimeMillis - lastUpdateTimeMillis;

    lastUpdateTimeMillis = currentTimeMillis;
    model.getAnimatables().forEach((Animatable a) -> a.update(deltaTimeMs));
  }

}
