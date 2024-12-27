package jef.core.units;

import java.util.Objects;

import org.apache.commons.math3.util.Precision;

public class Location
{
	public static final float EPSILON = .01f;

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

	public Location adjust(double x, double y, double z)
	{
		return new Location(this.x + x, this.y + y, this.z + z);
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
		return "Location [x=" + this.x + ", y=" + this.y + ", z=" + this.z + "]";
	}

}
