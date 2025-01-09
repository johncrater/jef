package jef.core.movement;

import java.util.Objects;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.util.Precision;

public class DefaultLocation implements Location
{
	private double x;
	private double y;
	private double z;

	public DefaultLocation()
	{
	}
	
	public DefaultLocation(double x, double y, double z)
	{
		this.x = Precision.round(x, 8);
		this.y = Precision.round(y, 8);
		this.z = Precision.round(z, 8);
	}
	
	public DefaultLocation(Vector3D vector)
	{
		this(vector.getX(), vector.getY(), vector.getZ());
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

		return getX() == other.getX() && getY() == other.getY() && getZ() == other.getZ();
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
	public Vector3D toVector3D()
	{
		return new Vector3D(x, y, z);
	}

	@Override
	public Vector2D toVector2D()
	{
		return new Vector2D(x, y);
	}

	@Override
	public String toString()
	{
		return String.format("(%6.2f, %5.2f, %5.2f)", this.x, this.y, this.z);
	}

}
