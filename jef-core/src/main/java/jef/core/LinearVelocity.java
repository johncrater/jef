package jef.core;

import org.apache.commons.numbers.core.Precision;

import jef.core.geometry.Vector;

/* @formatter:off */
public interface LinearVelocity
{
	public static final Precision.DoubleEquivalence EPSILON = Precision.doubleEquivalenceOfEpsilon(.02);

	public static boolean closeEnoughTo(final double v1, final double v2)
	{
		return EPSILON.eq(v1, v2);
	}

	public LinearVelocity add(double speed);
	public LinearVelocity add(double azimuth, double elevation, double speed);
	public LinearVelocity add(LinearVelocity lv);
	public LinearVelocity subtract(LinearVelocity lv);
	public LinearVelocity multiply(double scalar);
	public LinearVelocity normalize();
	public LinearVelocity negate();
	public double dotProduct(LinearVelocity lv);
	
	public LinearVelocity newFrom(Double azimuth, Double elevation, Double speed);

	public double getElevation();
	public double getAzimuth();
	public double getSpeed();

	public double getX();
	public double getY();
	public double getZ();

	public double getXYSpeed();
	public double getXZSpeed();
	public double getYZSpeed();

	public boolean isNotMoving();
	public boolean movingLeft();
	public boolean movingRight();

	public boolean closeEnoughTo(LinearVelocity lv);
	
	public Vector toVector();
}