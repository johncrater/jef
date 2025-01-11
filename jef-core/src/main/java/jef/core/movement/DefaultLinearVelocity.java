package jef.core.movement;

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
		// sometimes, for legitimate reasons, one of the these can come up NaN.
		
		if (Double.isNaN(elevation))
			elevation = 0;
		
		if (Double.isNaN(azimuth))
			azimuth = 0;
		
		if (Double.isNaN(speed))
			speed = 0;
		
		if (speed < 0)
		{
			azimuth += Math.PI;
			elevation *= -1;
			speed *= -1;
		}
		
		azimuth = MathUtils.normalizeAngle(azimuth, 0.0);
		
		elevation = MathUtils.normalizeAngle(elevation, 0.0);
		
		if (elevation > Math.PI / 2)
		{
			elevation = Math.PI - elevation;
			azimuth += Math.PI;
		}
		
		if (elevation < -Math.PI / 2)
		{
			elevation = Math.PI + elevation;
			azimuth -= Math.PI;
		}

		azimuth = MathUtils.normalizeAngle(azimuth, 0.0);

		this.elevation = elevation;
		this.azimuth = azimuth;
		this.speed = speed;
		
		assert Double.isNaN(this.elevation) == false && Double.isNaN(this.azimuth) == false && Double.isNaN(this.speed) == false;
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

		return this.getElevation() == other.getElevation()
				&& this.getAzimuth() == other.getAzimuth()
				&& this.getSpeed() == other.getSpeed();
	}

	@Override
	public boolean closeEnoughTo(LinearVelocity lv)
	{
		return Precision.equals(this.getElevation(), lv.getElevation(), LinearVelocity.EPSILON)
				&& Precision.equals(this.getAzimuth(), lv.getAzimuth(), LinearVelocity.EPSILON)
				&& Precision.equals(this.getSpeed(), lv.getSpeed(), LinearVelocity.EPSILON);
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

	@Override
	public Vector2D toVector2D()
	{
		double d = getSpeed();
		if (d == 0)
			d = Double.MIN_VALUE;
		
		return new Vector2D(getX(), getY());
	}
}
