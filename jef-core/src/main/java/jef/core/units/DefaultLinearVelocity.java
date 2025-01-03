package jef.core.units;

import java.util.Objects;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.util.MathUtils;
import org.apache.commons.math3.util.Precision;

public class DefaultLinearVelocity implements LinearVelocity
{
	public static final double EPSILON = .02;

	public static boolean withinEpsilon(final double v1, final double v2)
	{
		return Math.abs(v1 - v2) <= DefaultLinearVelocity.EPSILON;
	}

	private double elevation;
	private double azimuth;
	private double distance;

	public DefaultLinearVelocity()
	{
	}

	public DefaultLinearVelocity(Vector3D vector)
	{
		this(vector.getDelta(), vector.getAlpha(), vector.getNorm());
	}
	
	public DefaultLinearVelocity(double elevation, double azimuth, double distance)
	{
		this.elevation = elevation;
		this.azimuth = azimuth;
		this.distance = distance;
		
		if (this.distance < 0)
		{
			this.azimuth += Math.PI;
			this.distance *= -1;
		}
		
		while (this.elevation > Math.PI / 2)
		{
			this.elevation -= Math.PI / 2;
			this.azimuth += Math.PI;
		}
		
		while (this.elevation < -Math.PI / 2)
		{
			this.elevation += Math.PI / 2;
			this.azimuth -= Math.PI;
		}

		this.azimuth = MathUtils.normalizeAngle(this.azimuth, 0.0);
		assert this.distance >= 0 && this.elevation >= -Math.PI / 2 && this.elevation <= Math.PI / 2;
	}

	@Override
	public LinearVelocity add(final double distance)
	{
		return new DefaultLinearVelocity(getElevation(), getAzimuth(), distance == Double.MIN_VALUE ? distance : getDistance() + distance);
	}

	@Override
	public LinearVelocity add(final double elevation, final double azimuth, final double distance)
	{
		Vector3D toVector = new Vector3D(azimuth, elevation).add(distance, toVector3D());
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

		return Precision.equals(this.getElevation(), other.getElevation(), DefaultLinearVelocity.EPSILON)
				&& Precision.equals(this.getAzimuth(), other.getAzimuth(), DefaultLinearVelocity.EPSILON)
				&& Precision.equals(this.getDistance(), other.getDistance(), DefaultLinearVelocity.EPSILON);
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
	public double getDistance()
	{
		return this.distance;
	}

	@Override
	public double getX()
	{
		return toVector3D().getX();
	}

	@Override
	public double getXYDistance()
	{
		return new Vector2D(getX(), getY()).getNorm();
	}

	@Override
	public double getXZDistance()
	{
		return new Vector2D(getX(), getZ()).getNorm();
	}

	@Override
	public double getY()
	{
		return toVector3D().getY();
	}

	@Override
	public double getYZDistance()
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
		return Objects.hash(this.getElevation(), this.getAzimuth(), this.getDistance());
	}

	@Override
	public boolean isNotMoving()
	{
		return this.getDistance() < DefaultLinearVelocity.EPSILON;
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
		return new DefaultLinearVelocity(getElevation(), getAzimuth(), getDistance() * scalar);
	}

	@Override
	public LinearVelocity normalize()
	{
		return new DefaultLinearVelocity(toVector3D().normalize());
	}

	@Override
	public LinearVelocity newFrom(Double elevation, Double azimuth, Double distance)
	{
		if (elevation == null)
			elevation = this.getElevation();

		if (azimuth == null)
			azimuth = this.getAzimuth();

		if (distance == null)
			distance = this.getDistance();

		return new DefaultLinearVelocity(elevation, azimuth, distance);
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
				Math.toDegrees(this.getAzimuth()), this.getDistance());
	}
	
	@Override
	public Vector3D toVector3D()
	{
		double d = getDistance();
		if (d == 0)
			d = Double.MIN_VALUE;
		
		return new Vector3D(getAzimuth(), getElevation()).scalarMultiply(d);
	}
}
