package v2.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;

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
    Random rand = new Random();
    double scale = 3e-7;
    for (int i = 0; i < 1000; i++) {
      addAnimatable(new TestCharge(((i % 100) - 50) / 20.0, i * 0.01, (rand.nextInt(1000) - 500) * scale, (rand.nextInt(1000) - 500) * scale));
    }
  }

  private void addAnimatable(TestCharge tc) {
    drawables.add(tc);
    animatables.add(tc);
  }

  public void update(long deltaTimeMs) {

  }
}
