package jef.core.movement;

import java.util.Objects;

import jef.core.Field;
import jef.core.geometry.Vector;
import jef.core.pathfinding.Direction;

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
	public Location negate()
	{
		return new DefaultLocation(-getX(), -getY(), -getZ());
	}

	@Override
	public Location add(Location loc)
	{
		return add(loc.getX(), loc.getY(), loc.getZ());
	}

	@Override
	public Location subtract(Location loc)
	{
		return new DefaultLocation(getX() - loc.getX(), getY() - loc.getY(), getZ() - loc.getZ());
	}

	@Override
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

	@Override
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
	public Location multiply(double scalar)
	{
		return new DefaultLocation(getX() * scalar, getY() * scalar);
	}

	@Override
	public Location divide(double scalar)
	{
		return new DefaultLocation(getX() / scalar, getY() / scalar);
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

		return Location.EPSILON.eq(this.getX(), other.getX()) && Location.EPSILON.eq(this.getY(), other.getY())
				&& Location.EPSILON.eq(this.getZ(), other.getZ());
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
		return String.format("(%6.2f, %5.2f, %5.2f)", this.getX() - Field.WEST_END_ZONE_X, this.getY() - Field.FIELD_BORDER_WIDTH, this.getZ());
	}

	@Override
	public Vector toVector()
	{
		return this.v;
	}
}
