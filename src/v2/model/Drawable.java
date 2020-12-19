package v2.model;

import java.awt.Dimension;
import java.awt.Graphics;
import v2.ui.Viewport;

public interface Drawable {
  void draw(Viewport viewport, Dimension frameSize, Graphics g);
}
