package jef.core.units;

import java.util.Objects;

import org.apache.commons.math3.util.Precision;

public class LinearVelocity
{
	public static boolean withinEpsilon(double v1, double v2)
	{
		return Math.abs(v1 - v2) < EPSILON;
	}
	
	public static final double EPSILON = .02;

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

	public LinearVelocity add( double x, final double y, final double z)
	{
		return new LinearVelocity(this.x + x, this.y + y, this.z + z);
	}
	
	public LinearVelocity add(LinearVelocity lv)
	{
		return add(lv.getX(), lv.getY(), lv.getZ());
	}

	public LinearVelocity subtract(LinearVelocity lv)
	{
		return add(-lv.getX(), -lv.getY(), -lv.getZ());
	}

	public double calculateXYAngle()
	{
		return Math.atan2(this.getY(), this.getX());
	}

	public double calculateXZAngle()
	{
		return Math.atan2(this.getZ(), this.getX());
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
		return Math.sqrt(Math.pow(getX(), 2) + Math.pow(getY(), 2));
	}

	public double getY()
	{
		return y;
	}

	public double getXZSpeed()
	{
		return Math.sqrt(Math.pow(getX(), 2) + Math.pow(getZ(), 2));
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

	public boolean movingRight()
	{
		double xyAngle = this.calculateXYAngle();
		return this.getXYSpeed() > 0 && xyAngle > -Math.PI / 2 && xyAngle < Math.PI / 2; 
	}
	
	public boolean movingLeft()
	{
		double xyAngle = this.calculateXYAngle();
		return this.getXYSpeed() > 0 && (xyAngle > Math.PI / 2 || xyAngle < -Math.PI / 2); 
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
		return String.format("(%6.2f, %6.2f, %6.2f)", this.x, this.y, this.z);
	}

	public double getYZSpeed()
	{
		return Math.sqrt(Math.pow(getY(), 2) + Math.pow(getZ(), 2));
	}

}
