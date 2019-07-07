package model;

import ui.Viewport;

public abstract class Animatable {
  protected double x;
  protected double y;

  public Animatable(double x, double y) {
    this.x = x;
    this.y = y;
  }

  abstract void step(double timestep);
}
