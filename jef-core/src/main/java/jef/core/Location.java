package jef.core;

import java.util.Objects;

import org.apache.commons.numbers.core.Precision;

import jef.core.geometry.Vector;

public class Location
{
	public static final double EPSILON_VALUE = .02;
	public static final Precision.DoubleEquivalence EPSILON = Precision.doubleEquivalenceOfEpsilon(EPSILON_VALUE);

	public static boolean closeEnoughTo(double v1, double v2)
	{
		return EPSILON.eq(v1, v2);
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

	public Location add(double x, double y, double z)
	{
		return new Location(getX() + x, getY() + y, getZ() + z);
	}

	public Location negate()
	{
		return new Location(-getX(), -getY(), -getZ());
	}

	public Location add(Location loc)
	{
		return add(loc.getX(), loc.getY(), loc.getZ());
	}

	public Location subtract(Location loc)
	{
		return new Location(getX() - loc.getX(), getY() - loc.getY(), getZ() - loc.getZ());
	}

	public boolean isInBounds()
	{
		if (getX() <= Field.WEST_END_ZONE_BACK_X)
			return false;

		if (getX() >= Field.EAST_END_ZONE_BACK_X)
			return false;

		if (getY() <= Field.SOUTH_SIDELINE_Y)
			return false;

		if (getY() >= Field.NORTH_SIDELINE_Y)
			return false;

		return true;
	}

	public boolean isInEndZone(Direction direction)
	{
		if (isInBounds() == false)
			return false;

		if ((direction == null || direction == Direction.west) && getX() <= Field.WEST_END_ZONE_X)
			return true;

		if ((direction == null || direction == Direction.east) && getX() >= Field.EAST_END_ZONE_X)
			return true;

		return false;
	}

	public Location add(final LinearVelocity lv)
	{
		return new Location(this.v.add(lv.toVector()));
	}

	public double angleTo(final Location loc)
	{
		return Math.atan2(loc.getY() - this.getY(), loc.getX() - this.getX());
	}

	public Location multiply(double scalar)
	{
		return new Location(getX() * scalar, getY() * scalar);
	}

	public Location divide(double scalar)
	{
		return new Location(getX() / scalar, getY() / scalar);
	}

	public boolean closeEnoughTo(final Location loc)
	{
		return Location.EPSILON.eqZero(this.distanceBetween(loc));
	}

	public double distanceBetween(final Location loc)
	{
		return Math.sqrt(Math.pow(this.getX() - loc.getX(), 2) + Math.pow(this.getY() - loc.getY(), 2)
				+ Math.pow(this.getZ() - loc.getZ(), 2));
	}

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

	public int hashCode()
	{
		return Objects.hash(this.getX(), this.getY(), this.getZ());
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
