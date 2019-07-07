package model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import ui.Viewport;

public class TestCharge extends Animatable implements Drawable {

  public TestCharge(double x, double y) {
    super(new Double(x, y));
    vel = new Point2D.Double(0, -3e-4);
  }

  @Override
  public void draw(Viewport viewport, Dimension frameSize, Graphics g) {
    g.setColor(Color.GREEN);

    Point2D screen = viewport.convertWorldToScreen(pos.getX(), pos.getY(), frameSize);
    int diameter = 30;

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

    double dvx = 2e-6;
    double dvy = 0;
    vel = new Point2D.Double(vel.getX() + dvx, vel.getY() + dvy);
  }
}
