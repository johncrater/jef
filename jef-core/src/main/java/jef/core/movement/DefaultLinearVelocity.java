package jef.core.movement;

import java.math.BigDecimal;
import java.util.Objects;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.util.Precision;

public class DefaultLinearVelocity implements LinearVelocity
{
	private double elevation;
	private double azimuth;
	private double speed;

	public DefaultLinearVelocity()
	{
	}

	public DefaultLinearVelocity(Vector3D vector)
	{
		this(vector.getDelta(), vector.getAlpha(), vector.getNorm());
	}
	
	public DefaultLinearVelocity(double elevation, double azimuth, double speed)
	{
		this.elevation = elevation;
		this.azimuth = azimuth;
		this.speed = speed;
		
		if (this.speed < 0)
		{
			this.azimuth += Math.PI;
			this.elevation *= -1;
			this.speed *= -1;
		}
		
		this.azimuth = MathUtils.normalizeAngle(this.azimuth, 0.0);
		
		this.elevation = MathUtils.normalizeAngle(this.elevation, 0.0);
		
		if (this.elevation > Math.PI / 2)
		{
			this.elevation = Math.PI - this.elevation;
			this.azimuth += Math.PI;
		}
		
		if (this.elevation < -Math.PI / 2)
		{
			this.elevation = Math.PI + this.elevation;
			this.azimuth -= Math.PI;
		}

		this.azimuth = MathUtils.normalizeAngle(this.azimuth, 0.0);
		assert this.speed >= 0 && this.elevation >= -Math.PI / 2 && this.elevation <= Math.PI / 2;
	}

	@Override
	public LinearVelocity add(final double speed)
	{
		return new DefaultLinearVelocity(getElevation(), getAzimuth(), speed == Double.MIN_VALUE ? speed : getSpeed() + speed);
	}

	@Override
	public LinearVelocity add(final double elevation, final double azimuth, final double speed)
	{
		Vector3D toVector = toVector3D().add(speed, new Vector3D(azimuth, elevation));
		return new DefaultLinearVelocity(toVector);
	}

	@Override
	public LinearVelocity add(final LinearVelocity lv)
	{
		Vector3D toVector = lv.toVector3D().add(toVector3D());
		return new DefaultLinearVelocity(toVector);
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
			return true;

		if ((obj == null) || (this.getClass() != obj.getClass()))
			return false;

		final LinearVelocity other = (LinearVelocity) obj;

		return Precision.equals(this.getElevation(), other.getElevation(), LinearVelocity.EPSILON)
				&& Precision.equals(this.getAzimuth(), other.getAzimuth(), LinearVelocity.EPSILON)
				&& Precision.equals(this.getSpeed(), other.getSpeed(), LinearVelocity.EPSILON);
	}

	@Override
	public double getElevation()
	{
		return this.elevation;
	}

	@Override
	public double getAzimuth()
	{
		return this.azimuth;
	}

	@Override
	public double getSpeed()
	{
		return this.speed;
	}

	@Override
	public double getX()
	{
		return toVector3D().getX();
	}

	@Override
	public double getXYSpeed()
	{
		return new Vector2D(getX(), getY()).getNorm();
	}

	@Override
	public double getXZSpeed()
	{
		return new Vector2D(getX(), getZ()).getNorm();
	}

	@Override
	public double getY()
	{
		return toVector3D().getY();
	}

	@Override
	public double getYZSpeed()
	{
		return new Vector2D(getY(), getZ()).getNorm();
	}

	@Override
	public double getZ()
	{
		return toVector3D().getZ();
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(this.getElevation(), this.getAzimuth(), this.getSpeed());
	}

	@Override
	public boolean isNotMoving()
	{
		return this.getSpeed() < LinearVelocity.EPSILON;
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
		return new DefaultLinearVelocity(getElevation(), getAzimuth(), getSpeed() * scalar);
	}

	@Override
	public LinearVelocity normalize()
	{
		return new DefaultLinearVelocity(toVector3D().normalize());
	}

	@Override
	public LinearVelocity newFrom(Double elevation, Double azimuth, Double speed)
	{
		if (elevation == null)
			elevation = this.getElevation();

		if (azimuth == null)
			azimuth = this.getAzimuth();

		if (speed == null)
			speed = this.getSpeed();

		return new DefaultLinearVelocity(elevation, azimuth, speed);
	}

	@Override
	public LinearVelocity subtract(final LinearVelocity lv)
	{
		return new DefaultLinearVelocity(toVector3D().subtract(lv.toVector3D()));
	}

	@Override
	public String toString()
	{
		return String.format("(%3.0f\u00B0, %3.0f\u00B0, %7.3f y/s)", Math.toDegrees(this.getElevation()),
				Math.toDegrees(this.getAzimuth()), this.getSpeed());
	}
	
	@Override
	public Vector3D toVector3D()
	{
		double d = getSpeed();
		if (d == 0)
			d = Double.MIN_VALUE;
		
		return new Vector3D(getAzimuth(), getElevation()).scalarMultiply(d);
	}
}
