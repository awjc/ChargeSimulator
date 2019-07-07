package model;

import java.util.ArrayList;
import java.util.Collection;

public class SimulationModel {
  private Collection<Drawable> drawables;

  private SimulationModel() {
    reset();
  }

  public static SimulationModel createEmpty() {
    return new SimulationModel();
  }

  public Collection<Drawable> getDrawables() {
    return drawables;
  }

  public void reset() {
    this.drawables = new ArrayList<>();
    drawables.add(new TestCharge(0, 0));
  }

  public void step(double timestep) {

  }
}
