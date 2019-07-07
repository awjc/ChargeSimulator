package original.main;

/**
 * There is no javadoc
 */
public strictfp class ForceVector {
	private double xc;
	private double yc;
	
	public ForceVector(){
		xc = 0;
		yc = 0;
	}
	
	public double getXComponent(){
		return xc;
	}
	
	public double getYComponent(){
		return yc;
	}
	
	public void reset(){
		xc = 0;
		yc = 0;
	}
	
	public void feelForce(double xForce, double yForce){
		xc += xForce;
		yc += yForce;
	}
}
