package jef.core;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/* @formatter:off */
public interface LinearVelocity
{
	public static final double EPSILON = .02;

	public static boolean withinEpsilon(final double v1, final double v2)
	{
		return Math.abs(v1 - v2) <= LinearVelocity.EPSILON;
	}

	public LinearVelocity add(double distance);
	public LinearVelocity add(double elevation, double azimuth, double distance);
	public LinearVelocity add(LinearVelocity lv);
	public LinearVelocity subtract(LinearVelocity lv);
	public LinearVelocity multiply(double scalar);
	public LinearVelocity normalize();
	public LinearVelocity newFrom(Double elevation, Double azimuth, Double distance);

	public double getElevation();
	public double getAzimuth();
	public double getDistance();

	public double getX();
	public double getY();
	public double getZ();

	public double getXYDistance();
	public double getXZDistance();
	public double getYZDistance();

	public boolean isNotMoving();
	public boolean movingLeft();
	public boolean movingRight();

	public Vector3D toVector3D();

}