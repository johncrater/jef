package jef.core.units;

import java.util.Objects;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.util.Precision;

public class Location
{
	public static final float EPSILON = .01f;
	
	private Vector3D vector;
	
	public Location()
	{
		vector = Vector3D.ZERO;
	}

	public Location(double x, double y, double z)
	{
		vector = new Vector3D(Precision.round(x, 4), Precision.round(y, 4), Precision.round(z, 4));
	}
	
	public Location adjust(Double x, Double y, Double z)
	{
		if (x == null)
			x = getX();
		
		if (y == null)
			y = getY();
		
		if (z == null)
			z = getZ();
		
		return new Location(x, y, z);
	}
	
	public double getX()
	{
		return this.vector.getX();
	}
	
	public double getY()
	{
		return this.vector.getY();
	}
	
	public double getZ()
	{
		return this.vector.getZ();
	}
	
	@Override
	public int hashCode()
	{
		return Objects.hash(vector);
	}

	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;

		if (obj == null)
			return false;

		if (getClass() != obj.getClass())
			return false;

		Location other = (Location) obj;

		return Precision.equals(vector.getX(), other.vector.getX(), EPSILON)
				&& Precision.equals(vector.getY(), other.vector.getY(), EPSILON)
				&& Precision.equals(vector.getZ(), other.vector.getZ(), EPSILON);
	}

	@Override
	public String toString()
	{
		return "Location " + this.vector;
	}

}
