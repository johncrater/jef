package jef.core.units;

import java.util.Objects;

import org.apache.commons.math3.util.Precision;

public class LinearVelocity
{
	public static final double EPSILON = .01;

	private double x;
	private double y;
	private double z;

	public LinearVelocity()
	{
	}

	public LinearVelocity(final double x, final double y, final double z)
	{
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public LinearVelocity adjust( double x, final double y, final double z)
	{
		return new LinearVelocity(this.x + x, this.y + y, this.z + z);
	}

	public double calculateXYAngle()
	{
		return Math.atan2(this.getY(), this.getX());
	}

	public double calculateYZAngle()
	{
		return Math.atan2(this.getZ(), this.getY());
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
			return true;

		if ((obj == null) || (this.getClass() != obj.getClass()))
			return false;

		final LinearVelocity other = (LinearVelocity) obj;

		return Precision.equals(getX(), other.getX(), LinearVelocity.EPSILON)
				&& Precision.equals(getY(), other.getY(), LinearVelocity.EPSILON)
				&& Precision.equals(getZ(), other.getZ(), LinearVelocity.EPSILON);
	}

	public double getX()
	{
		return x;
	}

	public double getXYSpeed()
	{
		return Precision.round(Math.sqrt(Math.pow(getX(), 2) + Math.pow(getY(), 2)), 2);
	}

	public double getY()
	{
		return y;
	}

	public double getYZSpeed()
	{
		return Precision.round(Math.sqrt(Math.pow(getY(), 2) + Math.pow(getZ(), 2)), 2);
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

	public boolean isCloseToZero()
	{
		return this.magnitude() < LinearVelocity.EPSILON;
	}

	public LinearVelocity normalize()
	{
		return multiply(1 / magnitude());
	}
	
	public double magnitude()
	{
		return Math.sqrt(Math.pow(this.getX(), 2) + Math.pow(this.getY(), 2) + Math.pow(this.getZ(), 2));
	}

	public LinearVelocity multiply(final double scalar)
	{
		return new LinearVelocity(this.getX() * scalar, this.getY() * scalar, this.getZ() * scalar);
	}

	@Override
	public String toString()
	{
		return "LinearVelocity [x=" + this.x + ", y=" + this.y + ", z=" + this.z + "]";
	}

}
