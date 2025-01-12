package jef.core.movement;

import java.util.Objects;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.util.Precision;
import org.locationtech.jts.geom.Coordinate;

public class DefaultLocation implements Location
{
	private double x;
	private double y;
	private double z;

	public DefaultLocation()
	{
	}

	public DefaultLocation(final double x, final double y)
	{
		this.x = Precision.round(x, 8);
		this.y = Precision.round(y, 8);
		this.z = 0;
	}

	public DefaultLocation(final double x, final double y, final double z)
	{
		this.x = Precision.round(x, 8);
		this.y = Precision.round(y, 8);
		this.z = Precision.round(z, 8);
	}

	public DefaultLocation(final Vector3D vector)
	{
		this(vector.getX(), vector.getY(), vector.getZ());
	}

	public DefaultLocation(final Coordinate coordinate)
	{
		this(coordinate.getX(), coordinate.getY(), coordinate.getZ());
	}

	@Override
	public DefaultLocation add(final double x, final double y, final double z)
	{
		return new DefaultLocation(this.x + x, this.y + y, this.z + z);
	}

	@Override
	public DefaultLocation add(final LinearVelocity lv)
	{
		return this.add(lv.getX(), lv.getY(), lv.getZ());
	}

	@Override
	public Location add(final Location loc)
	{
		return this.add(loc.getX(), loc.getY(), loc.getZ());
	}

	@Override
	public double angleTo(final Location loc)
	{
		return Math.atan2(loc.getY() - this.getY(), loc.getX() - this.getX());
	}

	@Override
	public boolean closeEnoughTo(final Location loc)
	{
		return this.closeEnoughTo(loc, Location.EPSILON);
	}

	@Override
	public boolean closeEnoughTo(final Location loc, final double distance)
	{
		return this.distanceBetween(loc) <= distance;
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

		return (this.getX() == other.getX()) && (this.getY() == other.getY()) && (this.getZ() == other.getZ());
	}

	@Override
	public double getX()
	{
		return this.x;
	}

	@Override
	public double getY()
	{
		return this.y;
	}

	@Override
	public double getZ()
	{
		return this.z;
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(this.x, this.y, this.z);
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
	public Coordinate toCoordinate()
	{
		return new Coordinate(this.getX(), this.getY(), this.getZ());
	}

	@Override
	public String toString()
	{
		return String.format("(%6.2f, %5.2f, %5.2f)", this.x, this.y, this.z);
	}

	@Override
	public Vector2D toVector2D()
	{
		return new Vector2D(this.x, this.y);
	}

	@Override
	public Vector3D toVector3D()
	{
		return new Vector3D(this.x, this.y, this.z);
	}

}
