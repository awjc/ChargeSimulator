package v3;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.util.List;

public class TestCharge {

  // public double PARAM_1 = 0.32;
  public static double PARAM_1 = 0.49;
  public static double PARAM_2 = 5.5;
  public static double PARAM_3 = 10;

  private double x;
  private double y;
  private double vx;
  private double vy;
  private ForceVector force = new ForceVector();

  public boolean first = false;

  private static final double SPEED_FACTOR = 0.5;

  private static final int TEST_CHARGE_DIAMETER = 15;
  // private static final Color TEST_CHARGE_COLOR = new Color(0, 255, 0);
  // private static final Color FIRST_COLOR = new Color(100, 255, 255);

  public TestCharge(double x, double y, double vx, double vy) {
    this.x = x;
    this.y = y;
    this.vx = vx;
    this.vy = vy;
  }

  public TestCharge(double x, double y) {
    this(x, y, 0, 0);
  }

  public Point2D getLocation() {
    return new Point2D.Double(x, y);
  }

  public void draw(Graphics g, double scaleFactor, Dimension centerPoint) {
//		if(first){
//			g.setColor(FIRST_COLOR);
//		} else{
//			g.setColor(TEST_CHARGE_COLOR);
//		}
    int min = 8;
    int gValue = min + (int) ((255 - min) * mag());
    // int rValue = 1 + (int) ((20 - 1) * mag());
    Color newColor = new Color(0, gValue, 0);
    g.setColor(newColor);
    g.fillOval(
        centerPoint.width + (int) ((x - TEST_CHARGE_DIAMETER * ChargeSimulator.chargeSize / 2)
            * scaleFactor),
        centerPoint.height + (int) ((y - TEST_CHARGE_DIAMETER * ChargeSimulator.chargeSize / 2)
            * scaleFactor),
        (int) Math.ceil(TEST_CHARGE_DIAMETER * scaleFactor * ChargeSimulator.chargeSize),
        (int) Math.ceil(TEST_CHARGE_DIAMETER * scaleFactor * ChargeSimulator.chargeSize));
//		g.fillRect((int)(centerPoint.width + x*scaleFactor), (int)(centerPoint.height + y*scaleFactor), 5, 5);
  }

  private double f(double d) {
    return Math.pow(d, PARAM_1);
  }

  private double g(double d) {
    return Math.pow(d, PARAM_2);
  }

  private double mag() {
    double mag = Math.sqrt(vx * vx + vy * vy);
    // System.out.println(mag);
    return clip(g(f(mag) / PARAM_3));
  }

  private double clip(double d) {
    return Math.max(0, Math.min(1, d));
  }

  public void update(List<Charge> posCharges, List<Charge> negCharges, double deltaT) {
//	public void update(Charge[] posCharges, Charge[] negCharges, double deltaT){
    force.reset();
    synchronized (posCharges) {
      for (Charge c : posCharges) {
        double potential = c.getPotentialAt(x, y);
        double dx = x - c.getX();
        double dy = y - c.getY();

        force.feelForce(potential * dx, potential * dy);
      }
    }

    synchronized (negCharges) {
      for (Charge c : negCharges) {
        double potential = c.getPotentialAt(x, y);
        double dx = x - c.getX();
        double dy = y - c.getY();

        force.feelForce(potential * dx, potential * dy);
      }
    }

    vx += force.getXComponent();
    vy += force.getYComponent();

    x += vx * deltaT * SPEED_FACTOR;
    y += vy * deltaT * SPEED_FACTOR;
  }

  String toSerializedString() {
    return String
        .format("T %s %s %s %s",
            Double.toString(x), Double.toString(y), Double.toString(vx), Double.toString(vy));
  }

  static TestCharge fromSerializedString(String encoded) {
    String[] parts = encoded.split(" ");
    assert parts.length == 5;
    double x = Double.valueOf(parts[1]);
    double y = Double.valueOf(parts[2]);
    double vx = Double.valueOf(parts[3]);
    double vy = Double.valueOf(parts[4]);
    return new TestCharge(x, y, vx, vy);
  }
}
