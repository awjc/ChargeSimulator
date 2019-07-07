package model;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import ui.Viewport;

public class TestCharge extends Animatable implements Drawable {

  public TestCharge(double x, double y) {
    super(x, y);
  }


  @Override
  public void draw(Viewport viewport, Dimension frameSize, Graphics g) {
    g.setColor(Color.GREEN);

    Point2D screen = viewport.convertWorldToScreen(x, y, frameSize);
    int diameter = 30;

    g.fillOval(
        (int) (screen.getX() - diameter / 2),
        (int) (screen.getY() - diameter / 2),
        diameter,
        diameter);
  }

  @Override
  public void step(double timestep) {

  }
}
