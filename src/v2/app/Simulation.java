package v2.app;

import java.awt.Dimension;
import java.awt.Graphics;
import java.util.Arrays;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;
import v2.model.Animatable;
import v2.model.SimulationModel;
import v2.ui.Viewport;
import v2.Constants;

public class Simulation {
  private static final int PHYSICS_FPS = Constants.PHYSICS_FPS;
  private static final int MIN_UPDATE_DELAY_MS = 1000 / PHYSICS_FPS;
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
