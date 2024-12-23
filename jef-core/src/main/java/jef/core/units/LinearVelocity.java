package jef.core.units;

import java.util.Objects;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.util.Precision;

public class LinearVelocity
{
	public static final double EPSILON = .01;

	private final Vector3D vector;

	public LinearVelocity()
	{
		this.vector = Vector3D.ZERO;
	}

	public LinearVelocity(final double x, final double y, final double z)
	{
		this.vector = new Vector3D(Precision.round(x, 4), Precision.round(y, 4), Precision.round(z, 4));
	}

	public LinearVelocity adjust(Double x, Double y, Double z)
	{
		if (x == null)
			x = this.getX();

		if (y == null)
			y = this.getY();

		if (z == null)
			z = this.getZ();

		return new LinearVelocity(x, y, z);
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
			return true;

		if ((obj == null) || (this.getClass() != obj.getClass()))
			return false;

		final LinearVelocity other = (LinearVelocity) obj;

		return Precision.equals(this.vector.getX(), other.vector.getX(), LinearVelocity.EPSILON)
				&& Precision.equals(this.vector.getY(), other.vector.getY(), LinearVelocity.EPSILON)
				&& Precision.equals(this.vector.getZ(), other.vector.getZ(), LinearVelocity.EPSILON);
	}

	public double getX()
	{
		return this.vector.getX();
	}

	public double getXYVelocity()
	{
		return Precision.round(Math.sqrt(Math.pow(this.vector.getX(), 2) + Math.pow(this.vector.getY(), 2)), 2);
	}

	public double getY()
	{
		return this.vector.getY();
	}

	public double getYZVelocity()
	{
		return Precision.round(Math.sqrt(Math.pow(this.vector.getY(), 2) + Math.pow(this.vector.getZ(), 2)), 2);
	}

	public double getZ()
	{
		return this.vector.getZ();
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(this.vector);
	}

	public LinearVelocity multiply(final double scalar)
	{
		return new LinearVelocity(this.getX() * scalar, this.getY() * scalar, this.getZ() * scalar);
	}

	public double magnitude()
	{
		return Math.sqrt(Math.pow(getX(), 2) + Math.pow(getY(), 2) + Math.pow(getZ(), 2));
	}
	
	public boolean isCloseToZero()
	{
		return magnitude() < EPSILON;
	}

	@Override
	public String toString()
	{
		return "LinearVelocity " + this.vector;
	}

}
