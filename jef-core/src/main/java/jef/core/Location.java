package jef.core;

import java.util.Objects;

import org.apache.commons.numbers.core.Precision;

import jef.core.geometry.Vector;

public class Location implements Comparable<Location>
{
	public static final double EPSILON_VALUE = .02;
	public static final Precision.DoubleEquivalence EPSILON = Precision
			.doubleEquivalenceOfEpsilon(Location.EPSILON_VALUE);

	public static boolean closeEnoughTo(final double v1, final double v2)
	{
		return Location.EPSILON.eq(v1, v2);
	}

	private final Vector v;

	public Location()
	{
		this(0, 0, 0);
	}

	public Location(final double x, final double y)
	{
		this(x, y, 0);
	}

	public Location(final double x, final double y, final double z)
	{
		this.v = Vector.fromCartesianCoordinates(x, y, z);
	}

	public Location(final Vector v)
	{
		this.v = v;
	}

	public Location add(final double x, final double y, final double z)
	{
		return new Location(this.getX() + x, this.getY() + y, this.getZ() + z);
	}

	public Location add(final LinearVelocity lv)
	{
		return new Location(this.v.add(lv.toVector()));
	}

	public Location add(final Location loc)
	{
		return this.add(loc.getX(), loc.getY(), loc.getZ());
	}

	public double angleTo(final Location loc)
	{
		return Math.atan2(loc.getY() - this.getY(), loc.getX() - this.getX());
	}

	/**
	 * The margin for error is Location.EPSILON_VALUE
	 * @param loc The location to determine the distance from.
	 * @return
	 */
	public boolean closeEnoughTo(final Location loc)
	{
		return this.closeEnoughTo(loc, Location.EPSILON_VALUE);
	}

	/**
	 * @param loc The location to determine the distance from
	 * @param epsilon The margin for error. 
	 * @return
	 */
	public boolean closeEnoughTo(final Location loc, double epsilon)
	{
		return this.distanceBetween(loc) <= epsilon;
	}

	@Override
	public int compareTo(final Location o)
	{
		int ret = Double.compare(this.getX(), o.getX());
		if (ret == 0)
		{
			ret = Double.compare(this.getY(), o.getY());
		}

		return ret;
	}

	public double distanceBetween(final Location loc)
	{
		return Math.sqrt(Math.pow(this.getX() - loc.getX(), 2) + Math.pow(this.getY() - loc.getY(), 2)
				+ Math.pow(this.getZ() - loc.getZ(), 2));
	}

	public Location divide(final double scalar)
	{
		return new Location(this.getX() / scalar, this.getY() / scalar);
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
			return true;

		if ((obj == null) || (this.getClass() != obj.getClass()))
			return false;

		final Location other = (Location) obj;

		return Location.EPSILON.eq(this.getX(), other.getX()) && Location.EPSILON.eq(this.getY(), other.getY())
				&& Location.EPSILON.eq(this.getZ(), other.getZ());
	}

	public double getX()
	{
		return this.v.getX();
	}

	public double getY()
	{
		return this.v.getY();
	}

	public double getZ()
	{
		return this.v.getZ();
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(this.getX(), this.getY(), this.getZ());
	}

	public boolean isInBounds(double epsilon)
	{
		if ((this.getX() <= Field.WEST_END_ZONE_BACK_X)
				|| (this.getX() >= Field.EAST_END_ZONE_BACK_X)
				|| (this.getY() >= Field.SOUTH_SIDELINE_Y - epsilon)
				|| (this.getY() <= Field.NORTH_SIDELINE_Y + epsilon))
			return false;

		return true;
	}

	public boolean isInBounds()
	{
		return isInBounds(0);
	}

	public boolean isInEndZone(final Direction direction)
	{
		if (!this.isInBounds(0))
			return false;

		if (((direction == null) || (direction == Direction.west)) && (this.getX() <= Field.WEST_END_ZONE_X))
			return true;

		if (((direction == null) || (direction == Direction.east)) && (this.getX() >= Field.EAST_END_ZONE_X))
			return true;

		return false;
	}

	public Location moveTowards(Location loc, double distance)
	{
		LinearVelocity lv = new LinearVelocity(this, loc);
		lv = lv.newFrom(null, null, distance);
		return this.add(lv);
	}

	public Location multiply(final double scalar)
	{
		return new Location(this.getX() * scalar, this.getY() * scalar);
	}

	public Location negate()
	{
		return new Location(-this.getX(), -this.getY(), -this.getZ());
	}

	public Location newFrom(Double x, Double y, Double z)
	{
		if (x == null)
		{
			x = this.getX();
		}

		if (y == null)
		{
			y = this.getY();
		}

		if (z == null)
		{
			z = this.getZ();
		}

		return new Location(x, y, z);
	}

	public Location subtract(final Location loc)
	{
		return new Location(this.getX() - loc.getX(), this.getY() - loc.getY(), this.getZ() - loc.getZ());
	}

	@Override
	public String toString()
	{
		return String.format("(%6.2f, %5.2f, %5.2f)", this.getX() - Field.WEST_END_ZONE_X,
				this.getY() - Field.DIM_SIDELINE_WIDTH, this.getZ());
	}

	public Vector toVector()
	{
		return this.v;
	}
}
