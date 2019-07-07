package app;

import java.awt.Dimension;
import java.awt.Graphics;
import model.SimulationModel;
import ui.Viewport;

public class Simulation {
  private SimulationModel model;

  public Simulation() {
    this.model = SimulationModel.createEmpty();
  }

  public void initializeAndRun() {

  }

  /** Draws all the drawables in the model */
  public void draw(Viewport viewport, Dimension frameSize, Graphics g) {
    model.getDrawables().forEach(d -> d.draw(viewport, frameSize, g));
  }

}
