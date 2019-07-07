package ui;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.geom.Point2D.Double;
import java.awt.geom.Rectangle2D;

public class Viewport {

  private static final Point2D DEFAULT_CENTER_POINT = new Double(0, 0);
  private static final double DEFAULT_PIXELS_PER_UNIT_SPACE = 100;

  /**
   * The center point of the viewport in coordinates of 2D simulation space. This corresponds to the
   * center of the actual window on screen.
   */
  private Point2D centerPoint;

  /**
   * The zoom level is expressed as a double value that is essentially "Pixels per unit of space". A
   * higher zoom level means more pixels are needed for the same distance in simulation space, so it
   * has the effect of zooming in.
   */
  private double pixelsPerUnitSpace;

  private Viewport(Point2D centerPoint, double pixelsPerUnitSpace) {
    this.centerPoint = centerPoint;
    this.pixelsPerUnitSpace = pixelsPerUnitSpace;
  }

  public static Viewport create() {
    return new Viewport(DEFAULT_CENTER_POINT, DEFAULT_PIXELS_PER_UNIT_SPACE);
  }

  /**
   * Get the bounds of this viewport (in simulation space), given the current frame and zoom level
   */
  public Rectangle2D getBoundsForFrame(Dimension frameSize) {
    double totalWidthUnits = frameSize.width / DEFAULT_PIXELS_PER_UNIT_SPACE;
    double totalHeightUnits = frameSize.height / DEFAULT_PIXELS_PER_UNIT_SPACE;
    return new Rectangle2D.Double(
        centerPoint.getX() - totalWidthUnits / 2,
        centerPoint.getY() - totalHeightUnits / 2,
        totalWidthUnits,
        totalHeightUnits);
  }

  public Point2D convertWorldToScreen(double x, double y, Dimension frameSize) {
    double newX = (x - centerPoint.getX()) * pixelsPerUnitSpace + frameSize.width / 2.0;
    double newY = (y - centerPoint.getY()) * pixelsPerUnitSpace + frameSize.height / 2.0;
    return new Point2D.Double(newX, newY);
  }
}
