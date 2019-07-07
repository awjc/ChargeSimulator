package _original.main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.geom.Point2D;
import java.util.List;

public strictfp class TestCharge {
	private double x;
	private double y;
	private double vx = 0;
	private double vy = 0;
	private boolean pos;
	private ForceVector force = new ForceVector();
	
	public boolean first = false;
	
	private static final double SPEED_FACTOR = 0.5;
	
	private static final int TEST_CHARGE_DIAMETER = 15;
	private static final Color TEST_CHARGE_COLOR = new Color(0, 255, 0);
	private static final Color FIRST_COLOR = new Color(100	, 255, 255);

	public TestCharge(double x, double y, boolean positive){
		this.x = x;
		this.y = y;
		this.pos = positive;
	}

	public Point2D getLocation(){
		return new Point2D.Double(x, y);
	}

	public void draw(Graphics g, double scaleFactor, Dimension centerPoint){
//		if(first){
//			g.setColor(FIRST_COLOR);
//		} else{
//			g.setColor(TEST_CHARGE_COLOR);
//		}
		int gValue = 16 + (int) ((255 - 16) * mag());
		int rValue = 1 + (int) ((40 - 1) * mag());
		Color newColor = new Color(0, gValue, 0);
		g.setColor(newColor);
		g.fillOval(centerPoint.width + (int)((x - TEST_CHARGE_DIAMETER/2)*scaleFactor),
				centerPoint.height +(int)((y - TEST_CHARGE_DIAMETER/2)*scaleFactor),
				(int)Math.ceil(TEST_CHARGE_DIAMETER*scaleFactor), (int)Math.ceil(TEST_CHARGE_DIAMETER*scaleFactor));
//		g.fillRect((int)(centerPoint.width + x*scaleFactor), (int)(centerPoint.height + y*scaleFactor), 5, 5);
	}

	private double f(double d) {
		return Math.pow(d, 0.5);
	}

	private double g(double d) {
		return Math.pow(d, 1.5);
	}

	private double mag() {
		double mag = Math.sqrt(vx * vx + vy * vy);
		// System.out.println(mag);
		return clip(g(f(mag) / 50));
	}

	private double clip(double d) {
		return Math.max(0, Math.min(1, d));
	}

	public void update(List<Charge> posCharges, List<Charge> negCharges, double deltaT){
//	public void update(Charge[] posCharges, Charge[] negCharges, double deltaT){
		force.reset();
		synchronized(posCharges){
			for(Charge c : posCharges){
				double potential = c.getPotentialAt(x, y);
				double dx = x - c.getX();
				double dy = y - c.getY();
				
				if(pos){
					force.feelForce(potential*dx, potential*dy);
				} else{
					force.feelForce(-potential*dx, -potential*dy);
				}
			}
		}
		
		synchronized(negCharges){
			for(Charge c : negCharges){
				double potential = c.getPotentialAt(x, y);
				double dx = x - c.getX();
				double dy = y - c.getY();
				
				if(pos){
					force.feelForce(potential*dx, potential*dy);
				} else{
					force.feelForce(-potential*dx, -potential*dy);
				}
			}
		}

		vx += force.getXComponent();
		vy += force.getYComponent();
		
		x += vx*deltaT*SPEED_FACTOR;
		y += vy*deltaT*SPEED_FACTOR;
	}
}
