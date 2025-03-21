package jef.core;

import java.util.Objects;

import org.apache.commons.numbers.core.Precision;

import jef.core.geometry.Vector;

public class LinearVelocity
{
	public static final Precision.DoubleEquivalence EPSILON = Precision.doubleEquivalenceOfEpsilon(.02);

	public static boolean closeEnoughTo(final double v1, final double v2)
	{
		return EPSILON.eq(v1, v2);
	}

	private final Vector v;

	public LinearVelocity()
	{
		this.v = new Vector();
	}

	public LinearVelocity(Location fromLoc, Location toLoc)
	{
		v = toLoc.toVector().subtract(fromLoc.toVector());
	}
	
	public LinearVelocity(double azimuth, double elevation, double speed)
	{
		// sometimes, for legitimate reasons, one of the these can come up NaN.

		if (Double.isNaN(elevation))
		{
			elevation = 0;
		}

		if (Double.isNaN(azimuth))
		{
			azimuth = 0;
		}

		if (Double.isNaN(speed))
		{
			speed = 0;
		}

		this.v = Vector.fromPolarCoordinates(azimuth, elevation, speed);
	}

	public LinearVelocity(final Vector v)
	{
		this.v = v;
	}

	
	public LinearVelocity add(final double speed)
	{
		return new LinearVelocity(this.getAzimuth(), this.getElevation(), speed == Double.MIN_VALUE ? speed : this.getSpeed() + speed);
	}

	
	public LinearVelocity add(double azimuth, double elevation, double speed)
	{
		return new LinearVelocity(getAzimuth() + azimuth, getElevation() + elevation, getSpeed() + speed);
	}

	
	public LinearVelocity add(final LinearVelocity lv)
	{
		return new LinearVelocity(this.v.add(Vector.fromPolarCoordinates(lv.getAzimuth(), lv.getElevation(), lv.getSpeed())));
	}

	
	public boolean closeEnoughTo(final LinearVelocity lv)
	{
		return LinearVelocity.EPSILON.eq(this.getElevation(), lv.getElevation())
				&& LinearVelocity.EPSILON.eq(this.getAzimuth(), lv.getAzimuth())
				&& LinearVelocity.EPSILON.eq(this.getSpeed(), lv.getSpeed());
	}

	
	public double dotProduct(final LinearVelocity lv)
	{
		return (this.getX() * lv.getX()) + (this.getY() * lv.getY()) + (this.getZ() * lv.getZ());
	}

	
	public boolean equals(final Object obj)
	{
		if (this == obj)
			return true;

		if ((obj == null) || (this.getClass() != obj.getClass()))
			return false;

		final LinearVelocity other = (LinearVelocity) obj;

		return (this.getElevation() == other.getElevation()) && (this.getAzimuth() == other.getAzimuth())
				&& (this.getSpeed() == other.getSpeed());
	}

	
	public double getAzimuth()
	{
		return this.v.getAzimuth();
	}

	
	public double getElevation()
	{
		return this.v.getElevation();
	}

	
	public double getSpeed()
	{
		return this.v.getDistance();
	}

	
	public double getX()
	{
		return this.v.getX();
	}

	
	public double getXYSpeed()
	{
		return this.getX() * this.getY();
	}

	
	public double getXZSpeed()
	{
		return this.getX() * this.getZ();
	}

	
	public double getY()
	{
		return this.v.getY();
	}

	
	public double getYZSpeed()
	{
		return this.getY() * this.getZ();
	}

	
	public double getZ()
	{
		return v.getZ();
	}

	
	public int hashCode()
	{
		return Objects.hash(this.getElevation(), this.getAzimuth(), this.getSpeed());
	}

	
	public boolean isNotMoving()
	{
		return LinearVelocity.EPSILON.eqZero(this.getSpeed());
	}

	
	public boolean movingLeft()
	{
		return (this.getAzimuth() > (Math.PI / 2)) || (this.getAzimuth() < (-Math.PI / 2));
	}

	
	public boolean movingRight()
	{
		return (this.getAzimuth() > (-Math.PI / 2)) && (this.getAzimuth() < (Math.PI / 2));
	}

	
	public LinearVelocity multiply(final double scalar)
	{
		return new LinearVelocity(this.getAzimuth(), this.getElevation(), this.getSpeed() * scalar);
	}

	
	public LinearVelocity negate()
	{
		return new LinearVelocity(-this.getAzimuth(), -this.getElevation(), this.getSpeed());
	}

	
	public LinearVelocity newFrom(Double azimuth, Double elevation, Double speed)
	{
		if (elevation == null)
		{
			elevation = this.getElevation();
		}

		if (azimuth == null)
		{
			azimuth = this.getAzimuth();
		}

		if (speed == null)
		{
			speed = this.getSpeed();
		}

		return new LinearVelocity(azimuth, elevation, speed);
	}

	
	public LinearVelocity normalize()
	{
		return new LinearVelocity(this.v.normalize());
	}

	
	public LinearVelocity subtract(final LinearVelocity lv)
	{
		return this.add(lv.negate());
	}

	
	public String toString()
	{
		return String.format("(%3.0f\u00B0, %3.0f\u00B0, %7.3f y/s)", Math.toDegrees(this.getAzimuth()),
				Math.toDegrees(this.getElevation()), this.getSpeed());
	}

	
	public Vector toVector()
	{
		return this.v;
	}

}
