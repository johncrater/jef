package jef.core.units;

import java.util.Objects;

import org.apache.commons.math3.util.Precision;

import jef.core.LinearVelocity;
import jef.core.Location;

public class DefaultLocation implements Location
{
	public static boolean withinEpsilon(double v1, double v2)
	{
		return Math.abs(v1 - v2) < EPSILON;
	}
	
	private double x;
	private double y;
	private double z;

	public DefaultLocation()
	{
	}
	
	public DefaultLocation(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	@Override
	public double distanceBetween(Location loc)
	{
		return Math.sqrt(Math.pow(getX() - loc.getX(), 2) + Math.pow(getY() - loc.getY(), 2) + Math.pow(getZ() - loc.getZ(), 2));
	}
	
	@Override
	public boolean closeEnoughTo(Location loc, double distance)
	{
		return distanceBetween(loc) <= distance;
	}
	
	@Override
	public boolean closeEnoughTo(Location loc)
	{
		return closeEnoughTo(loc, EPSILON);
	}
	
	@Override
	public double angleTo(Location loc)
	{
		return Math.atan2(loc.getY() - getY(), loc.getX() - getX());
	}
	
	@Override
	public DefaultLocation add(double x, double y, double z)
	{
		return new DefaultLocation(this.x + x, this.y + y, this.z + z);
	}

	@Override
	public DefaultLocation add(LinearVelocity lv)
	{
		return add(lv.getX(), lv.getY(), lv.getZ());
	}

	@Override
	public Location add(Location loc)
	{
		return add(loc.getX(), loc.getY(), loc.getZ());
	}

	@Override
	public Location newFrom(Double x, Double y, Double z)
	{
		if (x == null)
			x = getX();
		
		if (y == null)
			y = getY();
		
		if (z == null)
			z = getZ();
		
		return new DefaultLocation(x, y, z);
	}
	
	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
			return true;

		if ((obj == null) || (this.getClass() != obj.getClass()))
			return false;

		final Location other = (Location) obj;

		return Precision.equals(getX(), other.getX(), Location.EPSILON)
				&& Precision.equals(getY(), other.getY(), Location.EPSILON)
				&& Precision.equals(getZ(), other.getZ(), Location.EPSILON);
	}

	@Override
	public double getX()
	{
		return x;
	}

	@Override
	public double getY()
	{
		return y;
	}

	@Override
	public double getZ()
	{
		return z;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(x, y, z);
	}

	@Override
	public String toString()
	{
		return String.format("(%6.2f, %5.2f, %5.2f)", this.x, this.y, this.z);
	}

}
