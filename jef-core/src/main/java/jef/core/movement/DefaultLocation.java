package jef.core.movement;

import java.util.Objects;

import jef.core.Field;
import jef.core.geometry.Vector;

public class DefaultLocation implements Location
{
	private final Vector v;

	public DefaultLocation()
	{
		this(0, 0, 0);
	}

	public DefaultLocation(final double x, final double y)
	{
		this(x, y, 0);
	}

	public DefaultLocation(final double x, final double y, final double z)
	{
		this.v = Vector.fromCartesianCoordinates(x, y, z);
	}

	public DefaultLocation(final Vector v)
	{
		this.v = v;
	}

	@Override
	public Location add(double x, double y, double z)
	{
		return new DefaultLocation(getX() + x, getY() + y, getZ() + z);
	}

	@Override
	public boolean isInPlayableArea()
	{
		if (getX() < 0 || getX() > Field.FIELD_TOTAL_LENGTH)
			return false;
		
		if (getY() < 0 || getY() > Field.FIELD_TOTAL_WIDTH)
			return false;
			
		return true;
	}

	@Override
	public Location add(final LinearVelocity lv)
	{
		return new DefaultLocation(this.v.add(lv.toVector()));
	}

	@Override
	public double angleTo(final Location loc)
	{
		return Math.atan2(loc.getY() - this.getY(), loc.getX() - this.getX());
	}

	@Override
	public boolean closeEnoughTo(final Location loc)
	{
		return Location.EPSILON.eqZero(this.distanceBetween(loc));
	}

	@Override
	public double distanceBetween(final Location loc)
	{
		return Math.sqrt(Math.pow(this.getX() - loc.getX(), 2) + Math.pow(this.getY() - loc.getY(), 2)
				+ Math.pow(this.getZ() - loc.getZ(), 2));
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
			return true;

		if ((obj == null) || (this.getClass() != obj.getClass()))
			return false;

		final Location other = (Location) obj;

		return Location.EPSILON.eq(this.getX(), other.getX()) && Location.EPSILON.eq(this.getY(), other.getY()) && Location.EPSILON.eq(this.getZ(), other.getZ());
	}

	@Override
	public double getX()
	{
		return this.v.getX();
	}

	@Override
	public double getY()
	{
		return this.v.getY();
	}

	@Override
	public double getZ()
	{
		return this.v.getZ();
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(this.getX(), this.getY(), this.getZ());
	}

	@Override
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

		return new DefaultLocation(x, y, z);
	}

	@Override
	public String toString()
	{
		return String.format("(%6.2f, %5.2f, %5.2f)", this.getX(), this.getY(), this.getZ());
	}

	@Override
	public Vector toVector()
	{
		return this.v;
	}
}
