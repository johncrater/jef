package jef.core.movement;

import org.apache.commons.numbers.core.Precision;

import jef.core.geometry.Vector;

/* @formatter:off */
public interface Location
{
	public static final Precision.DoubleEquivalence EPSILON = Precision.doubleEquivalenceOfEpsilon(.02);

	public static boolean closeEnoughTo(double v1, double v2)
	{
		return EPSILON.eq(v1, v2);
	}
	
	public double distanceBetween(Location loc);
	public boolean closeEnoughTo(Location loc);
	public double angleTo(Location loc);

	public Location add(LinearVelocity lv);
	public Location add(double x, double y, double z);

	public Location newFrom(Double x, Double y, Double z);

	public double getX();
	public double getY();
	public double getZ();
	
	public Vector toVector();
	
	public boolean isInPlayableArea();
}