package v2.model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import v2.ui.Viewport;

public class TestCharge extends Animatable implements Drawable {

  TestCharge(double posX, double posY) {
    super(new Double(posX, posY));
  }

  TestCharge(double posX, double posY, double velX, double velY) {
    super(new Double(posX, posY), new Double(velX, velY));
  }

  @Override
  public void draw(Viewport viewport, Dimension frameSize, Graphics g) {
    g.setColor(Color.GREEN);

    Point2D screen = viewport.convertWorldToScreen(pos.getX(), pos.getY(), frameSize);
    int diameter = 10;

    g.fillOval(
        (int) (screen.getX() - diameter / 2),
        (int) (screen.getY() - diameter / 2),
        diameter,
        diameter);
  }

  @Override
  public void update(long deltaTimeMs) {
    double dx = deltaTimeMs * vel.getX();
    double dy = deltaTimeMs * vel.getY();
    pos = new Point2D.Double(pos.getX() + dx, pos.getY() + dy);

    // double dvx = 2e-6;
    // double dvy = 0;
    // vel = new Point2D.Double(vel.getX() + dvx, vel.getY() + dvy);
  }
}
