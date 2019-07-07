package model;

import java.util.ArrayList;
import java.util.Collection;

public class SimulationModel {
  private Collection<Drawable> drawables;
  private Collection<Animatable> animatables;

  private SimulationModel() {
    reset();
  }

  public static SimulationModel createEmpty() {
    return new SimulationModel();
  }

  public Collection<Drawable> getDrawables() {
    return drawables;
  }

  public Collection<Animatable> getAnimatables() {
    return animatables;
  }

  public void reset() {
    drawables = new ArrayList<>();
    animatables = new ArrayList<>();
    addAnimatable(new TestCharge(0, 0));
  }

  private void addAnimatable(TestCharge tc) {
    drawables.add(tc);
    animatables.add(tc);
  }

  public void update(long deltaTimeMs) {

  }
}
