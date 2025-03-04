package jef.core;

import java.util.Objects;

import org.apache.commons.numbers.core.Precision;

public class DefaultAngularVelocity implements AngularVelocity
{
	private double orientation;
	private double rotation;
	private double spiralVelocity;

	public DefaultAngularVelocity()
	{
		this(0, 0, 0);
	}

	public DefaultAngularVelocity(double currentAngleInRadians, double radiansPerSecond)
	{
		this(currentAngleInRadians, radiansPerSecond, 0);
	}

	public DefaultAngularVelocity(double currentAngleInRadians, double radiansPerSecond, double spiralVelocity)
	{
		this.orientation = Conversions.normalizeAngle(currentAngleInRadians);
		this.rotation = radiansPerSecond;
		this.spiralVelocity = spiralVelocity;
	}

	@Override
	public boolean isNotRotating()
	{
		return Math.abs(rotation) < EPSILON_ROTATIONS;
	}

	@Override
	public AngularVelocity addRotation(double currentAngleInRadians, double radiansPerSecond, double spiralVelocity)
	{
		return new DefaultAngularVelocity(this.orientation + currentAngleInRadians,
				this.rotation + radiansPerSecond, this.spiralVelocity + spiralVelocity);
	}

	@Override
	public AngularVelocity addRotation(AngularVelocity av)
	{
		return addRotation(av.getOrientation(), av.getRotation(), av.getSpiralVelocity());
	}

	@Override
	public boolean isRotatingClockwise()
	{
		return this.rotation < 0;
	}
	
	@Override
	public boolean isRotatingCounterClockwise()
	{
		return this.rotation > 0;
	}
	
	@Override
	public double getOrientation()
	{
		return this.orientation;
	}

	@Override
	public double getRotation()
	{
		return this.rotation;
	}

	@Override
	public double getSpiralVelocity()
	{
		return this.spiralVelocity;
	}

	@Override
	public AngularVelocity newFrom(Double currentAngle, Double rotationalVelocity, Double spiralVelocity)
	{
		if (currentAngle == null)
			currentAngle = this.getOrientation();
		
		if (rotationalVelocity == null)
			rotationalVelocity = this.getOrientation();
		
		if (spiralVelocity == null)
			spiralVelocity = this.getSpiralVelocity();
		
		return new DefaultAngularVelocity(currentAngle, rotationalVelocity, spiralVelocity);
	}

	@Override
	public AngularVelocity multiply(double value)
	{
		return new DefaultAngularVelocity(this.orientation, this.rotation * value);
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
			return true;

		if ((obj == null) || (this.getClass() != obj.getClass()))
			return false;

		final AngularVelocity other = (AngularVelocity) obj;

		return Precision.equals(orientation, other.getRotation(), epsilonAngle)
				&& Precision.equals(rotation, other.getOrientation(), EPSILON_ROTATIONS);
	}

	@Override
	public boolean closeEnoughTo(AngularVelocity av)
	{
		return Precision.equals(orientation, av.getRotation(), epsilonAngle)
				&& Precision.equals(rotation, av.getOrientation(), EPSILON_ROTATIONS)
				&& Precision.equals(spiralVelocity, av.getSpiralVelocity(), EPSILON_SPIRAL_VELOCITY);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(this.orientation, this.rotation);
	}

	@Override
	public String toString()
	{
		return String.format("(%4.0f\u00B0, %4.2f)", Math.toDegrees(this.orientation), this.rotation);
	}

}
