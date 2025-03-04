package jef.core.movement;

import org.apache.commons.numbers.core.Precision;

import jef.core.Direction;
import jef.core.geometry.Vector;

/* @formatter:off */
public interface Location
{
	public static final double EPSILON_VALUE = .02;
	public static final Precision.DoubleEquivalence EPSILON = Precision.doubleEquivalenceOfEpsilon(EPSILON_VALUE);

	public static boolean closeEnoughTo(double v1, double v2)
	{
		return EPSILON.eq(v1, v2);
	}
	
	public double distanceBetween(Location loc);
	public boolean closeEnoughTo(Location loc);
	public double angleTo(Location loc);

	public Location add(LinearVelocity lv);
	public Location add(double x, double y, double z);
	public Location add(Location loc);
	public Location subtract(Location loc);
	public Location multiply(double scalar);
	public Location divide(double scalar);
	public Location negate();

	public Location newFrom(Double x, Double y, Double z);

	public double getX();
	public double getY();
	public double getZ();
	
	public Vector toVector();
	
	/**
	 * @return true if the location is in bounds including in the end zones
	 */
	public boolean isInBounds();
	
	/**
	 * @return true if the location is in bounds and in either one of the two end zones
	 */
	public boolean isInEndZone(Direction direction);
	
}