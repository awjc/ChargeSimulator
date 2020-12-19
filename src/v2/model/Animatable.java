package v2.model;

import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;

public abstract class Animatable {
  protected Point2D pos;
  protected Point2D vel;

  protected Animatable(Point2D pos) {
    this(pos, new Double(0, 0));
  }

  protected Animatable(Point2D pos, Point2D vel) {
    this.pos = pos;
    this.vel = vel;
  }

  public abstract void update(long deltaTimeMs);
}
