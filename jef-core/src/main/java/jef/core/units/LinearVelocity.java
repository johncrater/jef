package jef.core.units;

import java.util.Objects;

import org.apache.commons.math3.geometry.euclidean.threed.Vector3D;
import org.apache.commons.math3.geometry.euclidean.twod.Vector2D;
import org.apache.commons.math3.util.Precision;

public class LinearVelocity
{
	public static final double EPSILON = .02;

	public static boolean withinEpsilon(final double v1, final double v2)
	{
		return Math.abs(v1 - v2) < LinearVelocity.EPSILON;
	}

	private Vector3D vector;

	public LinearVelocity()
	{
		vector = new Vector3D(0, 0, 0);
	}

	public LinearVelocity(Vector3D vector)
	{
		this.vector = vector;
	}
	
	public LinearVelocity(double elevation, double azimuth, double distance)
	{
		this(new Vector3D(azimuth, elevation).scalarMultiply(distance));
	}

	public LinearVelocity add(final double distance)
	{
		return new LinearVelocity(this.vector.getDelta(), vector.getAlpha(), distance + vector.getNorm());		
	}

	public LinearVelocity add(final double elevation, final double azimuth, final double distance)
	{
		Vector3D toVector = new Vector3D(azimuth, elevation, distance).add(this.vector);
		return new LinearVelocity(toVector);
	}

	public LinearVelocity add(final LinearVelocity lv)
	{
		Vector3D toVector = lv.vector.add(this.vector);
		return new LinearVelocity(toVector);
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
				&& Precision.equals(this.getDistance(), other.getDistance(), LinearVelocity.EPSILON);
	}

	public double getElevation()
	{
		return this.vector.getDelta();
	}

	public double getAzimuth()
	{
		return this.vector.getAlpha();
	}

	public double getDistance()
	{
		return this.vector.getNorm();
	}

	public double getX()
	{
		return this.vector.getX();
	}

	public double getXYDistance()
	{
		return new Vector2D(getX(), getY()).getNorm();
	}

	public double getXZDistance()
	{
		return new Vector2D(getX(), getZ()).getNorm();
	}

	public double getY()
	{
		return this.vector.getY();
	}

	public double getYZDistance()
	{
		return new Vector2D(getY(), getZ()).getNorm();
	}

	public double getZ()
	{
		return vector.getZ();
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(this.getElevation(), this.getAzimuth(), this.getDistance());
	}

	public boolean isCloseToZero()
	{
		return this.getDistance() < LinearVelocity.EPSILON;
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
		return new LinearVelocity(new Vector3D(scalar, vector));
	}

	public LinearVelocity normalize()
	{
		return new LinearVelocity(vector.normalize());
	}

	public LinearVelocity set(Double elevation, Double azimuth, Double distance)
	{
		if (elevation == null)
			elevation = this.getElevation();

		if (azimuth == null)
			azimuth = this.getAzimuth();

		if (distance == null)
			distance = this.getDistance();

		return new LinearVelocity(elevation, azimuth, distance);
	}

	public LinearVelocity subtract(final LinearVelocity lv)
	{
		return new LinearVelocity(vector.subtract(lv.vector));
	}

	@Override
	public String toString()
	{
		return String.format("(%3.0f\u00B0, %3.0f\u00B0, %7.3f y/s)", Math.toDegrees(this.getElevation()),
				Math.toDegrees(this.getAzimuth()), this.getDistance());
	}
	
	public Vector3D toVector3D()
	{
		return vector;
	}
}
