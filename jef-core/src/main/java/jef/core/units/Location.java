package jef.core.units;

import java.util.Objects;

import org.apache.commons.math3.util.Precision;

public class Location
{
	public static boolean withinEpsilon(double v1, double v2)
	{
		return Math.abs(v1 - v2) < EPSILON;
	}
	
	public static final double EPSILON = .02;

	private double x;
	private double y;
	private double z;

	public Location()
	{
	}
	
	public Location(double x, double y, double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public double distanceBetween(Location loc)
	{
		return Math.sqrt(Math.pow(getX() - loc.getX(), 2) + Math.pow(getY() - loc.getY(), 2) + Math.pow(getZ() - loc.getZ(), 2));
	}
	
	public boolean closeEnoughTo(Location loc, double distance)
	{
		return distanceBetween(loc) <= distance;
	}
	
	public boolean closeEnoughTo(Location loc)
	{
		return closeEnoughTo(loc, EPSILON);
	}
	
	public double angleTo(Location loc)
	{
		return Math.atan2(loc.getY() - getY(), loc.getX() - getX());
	}
	
	public Location adjust(double x, double y, double z)
	{
		return new Location(this.x + x, this.y + y, this.z + z);
	}

	public Location adjust(LinearVelocity lv)
	{
		return adjust(lv.getX(), lv.getY(), lv.getZ());
	}

	public Location adjust(Location loc)
	{
		return adjust(loc.getX(), loc.getY(), loc.getZ());
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

	public double getX()
	{
		return x;
	}

	public double getY()
	{
		return y;
	}

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
