package jef.core;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;

/* @formatter:off */
public interface Location
{
	public static final double EPSILON = .001;

	public static boolean withinEpsilon(double v1, double v2)
	{
		return Math.abs(v1 - v2) < EPSILON;
	}
	
	public double distanceBetween(Location loc);

	public boolean closeEnoughTo(Location loc, double distance);
	public boolean closeEnoughTo(Location loc);

	public double angleTo(Location loc);

	public Location add(double x, double y, double z);
	public Location add(LinearVelocity lv);
	public Location add(Location loc);

	public Location newFrom(Double x, Double y, Double z);

	public double getX();
	public double getY();
	public double getZ();

	public Vector3D toVector3D();
}