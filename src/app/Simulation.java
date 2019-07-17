package app;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Arrays;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;
import model.Animatable;
import model.SimulationModel;
import ui.Viewport;

public class Simulation {
  private static final int UPDATE_FPS = 60;
  private static final int MIN_UPDATE_DELAY_MS = 1000 / UPDATE_FPS;
  private final Collection<PhysUpdateListener> physUpdateListeners;

  private SimulationModel model;
  private long lastUpdateTimeMillis;

  private Simulation(Collection<PhysUpdateListener> physUpdateListeners) {
    this.model = SimulationModel.createEmpty();
    this.physUpdateListeners = physUpdateListeners;
  }

  static Simulation create(PhysUpdateListener... physUpdateListener) {
    return new Simulation(Arrays.asList(physUpdateListener));
  }

  public void initializeAndRun() {
    this.lastUpdateTimeMillis = System.currentTimeMillis();

    Timer updateTimer = new Timer();
    updateTimer.schedule(new TimerTask() {
      @Override
      public void run() {
        physUpdateListeners.forEach(l -> l.onBeforePhysUpdate(model));
        doUpdate();
        physUpdateListeners.forEach(l -> l.onAfterPhysUpdate(model));
      }
    }, 0, MIN_UPDATE_DELAY_MS);
  }

  /** Draws all the drawables in the model */
  public void draw(Viewport viewport, Dimension frameSize, Graphics g) {
    model.getDrawables().forEach(d -> d.draw(viewport, frameSize, g));
  }

  private void doUpdate() {
    final long currentTimeMillis = System.currentTimeMillis();
    long deltaTimeMs = currentTimeMillis - lastUpdateTimeMillis;

    lastUpdateTimeMillis = currentTimeMillis;
    model.getAnimatables().forEach((Animatable a) -> a.update(deltaTimeMs));
  }

}
