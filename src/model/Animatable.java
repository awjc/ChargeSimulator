package model;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

public abstract class Animatable {
  protected Point2D pos;
  protected Point2D vel;

  public Animatable(Point2D pos) {
    this.pos = pos;
    this.vel = new Double(0, 0);
  }

  public abstract void update(long deltaTimeMs);
}
