package model;

import java.awt.Dimension;
import java.awt.Graphics;
import ui.Viewport;

public interface Drawable {
  void draw(Viewport viewport, Dimension frameSize, Graphics g);
}
