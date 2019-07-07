package _original.main;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.geom.Point2D;

public strictfp class Charge {
	private double x;
	private double y;
	private double q;
	
	private static final double SCALING_FACTOR = 1.0;
//	private static final Color POSITIVE_COLOR = new Color(0, 0, 255);
//	private static final Color NEGATIVE_COLOR = new Color(255, 0, 0);
	private static double DISTANCE_THRESHOLD;
	
	public Charge(double x, double y, double q){
		this.x = x;
		this.y = y;
		this.q = q;
		DISTANCE_THRESHOLD = qToDiameter(q)/2.0;
	}
	
	public double getPotentialAt(double x, double y){
		x -= this.x;
		y -= this.y;
		double dist = x*x+y*y;
		if(dist < DISTANCE_THRESHOLD*DISTANCE_THRESHOLD)
			dist = DISTANCE_THRESHOLD*DISTANCE_THRESHOLD;
		
		return SCALING_FACTOR*q/dist;
	}
	
	public double getX(){
		return x;
	}
	
	public double getY(){
		return y;
	}
	
	public Point2D getLocation(){
		return new Point2D.Double(x, y);
	}
	
	public double getCharge(){
		return q;
	}
	
	public void setCharge(double newQ){
		q = newQ;
	}
	
	public void draw(Graphics g, double scaleFactor, Dimension centerPoint){
//		g.setColor(q < 0 ? NEGATIVE_COLOR : POSITIVE_COLOR);
		int diam = qToDiameter(q);
		g.fillOval(centerPoint.width + (int)((x - diam/2)*scaleFactor), 
				centerPoint.height + (int)((y - diam/2)*scaleFactor), 
				(int)Math.ceil(diam*scaleFactor), (int)Math.ceil(diam*scaleFactor));
//		g.fillRect((int)(centerPoint.width + x*scaleFactor), (int)(centerPoint.height + y*scaleFactor), 5, 5);
	}
	
	private int qToDiameter(double q){
		return 15;//(int) Math.abs(q/7.0);
	}
	
	public void update(double deltaT){
		
	}
	
	public double distanceTo(Charge o){
		return Math.floor(Math.sqrt((x - o.x)*(x - o.x)+ (y - o.y)*(y - o.y)));
	}
	
	@Override
	public String toString(){
		return String.format("Charge: [(%.0f, %.0f), %.0f C]", x, y, q);
	}
}
