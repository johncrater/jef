package jef.core;

import java.util.Objects;

import jef.core.geometry.Vector;

public class DefaultLinearVelocity implements LinearVelocity
{
	private final Vector v;

	public DefaultLinearVelocity()
	{
		this.v = new Vector();
	}

	public DefaultLinearVelocity(Location fromLoc, Location toLoc)
	{
		v = toLoc.toVector().subtract(fromLoc.toVector());
	}
	
	public DefaultLinearVelocity(double azimuth, double elevation, double speed)
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

	public DefaultLinearVelocity(final Vector v)
	{
		this.v = v;
	}

	@Override
	public LinearVelocity add(final double speed)
	{
		return new DefaultLinearVelocity(this.getAzimuth(), this.getElevation(), speed == Double.MIN_VALUE ? speed : this.getSpeed() + speed);
	}

	@Override
	public LinearVelocity add(double azimuth, double elevation, double speed)
	{
		return new DefaultLinearVelocity(getAzimuth() + azimuth, getElevation() + elevation, getSpeed() + speed);
	}

	@Override
	public LinearVelocity add(final LinearVelocity lv)
	{
		return new DefaultLinearVelocity(this.v.add(Vector.fromPolarCoordinates(lv.getAzimuth(), lv.getElevation(), lv.getSpeed())));
	}

	@Override
	public boolean closeEnoughTo(final LinearVelocity lv)
	{
		return LinearVelocity.EPSILON.eq(this.getElevation(), lv.getElevation())
				&& LinearVelocity.EPSILON.eq(this.getAzimuth(), lv.getAzimuth())
				&& LinearVelocity.EPSILON.eq(this.getSpeed(), lv.getSpeed());
	}

	@Override
	public double dotProduct(final LinearVelocity lv)
	{
		return (this.getX() * lv.getX()) + (this.getY() * lv.getY()) + (this.getZ() * lv.getZ());
	}

	@Override
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

	@Override
	public double getAzimuth()
	{
		return this.v.getAzimuth();
	}

	@Override
	public double getElevation()
	{
		return this.v.getElevation();
	}

	@Override
	public double getSpeed()
	{
		return this.v.getDistance();
	}

	@Override
	public double getX()
	{
		return this.v.getX();
	}

	@Override
	public double getXYSpeed()
	{
		return this.getX() * this.getY();
	}

	@Override
	public double getXZSpeed()
	{
		return this.getX() * this.getZ();
	}

	@Override
	public double getY()
	{
		return this.v.getY();
	}

	@Override
	public double getYZSpeed()
	{
		return this.getY() * this.getZ();
	}

	@Override
	public double getZ()
	{
		return v.getZ();
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(this.getElevation(), this.getAzimuth(), this.getSpeed());
	}

	@Override
	public boolean isNotMoving()
	{
		return LinearVelocity.EPSILON.eqZero(this.getSpeed());
	}

	@Override
	public boolean movingLeft()
	{
		return (this.getAzimuth() > (Math.PI / 2)) || (this.getAzimuth() < (-Math.PI / 2));
	}

	@Override
	public boolean movingRight()
	{
		return (this.getAzimuth() > (-Math.PI / 2)) && (this.getAzimuth() < (Math.PI / 2));
	}

	@Override
	public LinearVelocity multiply(final double scalar)
	{
		return new DefaultLinearVelocity(this.getAzimuth(), this.getElevation(), this.getSpeed() * scalar);
	}

	@Override
	public LinearVelocity negate()
	{
		return new DefaultLinearVelocity(-this.getAzimuth(), -this.getElevation(), this.getSpeed());
	}

	@Override
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

		return new DefaultLinearVelocity(azimuth, elevation, speed);
	}

	@Override
	public LinearVelocity normalize()
	{
		return new DefaultLinearVelocity(this.v.normalize());
	}

	@Override
	public LinearVelocity subtract(final LinearVelocity lv)
	{
		return this.add(lv.negate());
	}

	@Override
	public String toString()
	{
		return String.format("(%3.0f\u00B0, %3.0f\u00B0, %7.3f y/s)", Math.toDegrees(this.getAzimuth()),
				Math.toDegrees(this.getElevation()), this.getSpeed());
	}

	@Override
	public Vector toVector()
	{
		return this.v;
	}

}
