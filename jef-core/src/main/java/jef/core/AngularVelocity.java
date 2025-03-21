package jef.core;

import java.util.Objects;

import org.apache.commons.numbers.core.Precision;

public class AngularVelocity
{
	public static final double epsilonAngle = .02;
	public static final double EPSILON_ROTATIONS = .02;
	public static final double EPSILON_SPIRAL_VELOCITY = .02;

	private double orientation;
	private double rotation;
	private double spiralVelocity;

	public AngularVelocity()
	{
		this(0, 0, 0);
	}

	public AngularVelocity(double currentAngleInRadians, double radiansPerSecond)
	{
		this(currentAngleInRadians, radiansPerSecond, 0);
	}

	public AngularVelocity(double currentAngleInRadians, double radiansPerSecond, double spiralVelocity)
	{
		this.orientation = Conversions.normalizeAngle(currentAngleInRadians);
		this.rotation = radiansPerSecond;
		this.spiralVelocity = spiralVelocity;
	}

	
	public boolean isNotRotating()
	{
		return Math.abs(rotation) < EPSILON_ROTATIONS;
	}

	
	public AngularVelocity addRotation(double currentAngleInRadians, double radiansPerSecond, double spiralVelocity)
	{
		return new AngularVelocity(this.orientation + currentAngleInRadians,
				this.rotation + radiansPerSecond, this.spiralVelocity + spiralVelocity);
	}

	
	public AngularVelocity addRotation(AngularVelocity av)
	{
		return addRotation(av.getOrientation(), av.getRotation(), av.getSpiralVelocity());
	}

	
	public boolean isRotatingClockwise()
	{
		return this.rotation < 0;
	}
	
	
	public boolean isRotatingCounterClockwise()
	{
		return this.rotation > 0;
	}
	
	
	public double getOrientation()
	{
		return this.orientation;
	}

	
	public double getRotation()
	{
		return this.rotation;
	}

	
	public double getSpiralVelocity()
	{
		return this.spiralVelocity;
	}

	
	public AngularVelocity newFrom(Double currentAngle, Double rotationalVelocity, Double spiralVelocity)
	{
		if (currentAngle == null)
			currentAngle = this.getOrientation();
		
		if (rotationalVelocity == null)
			rotationalVelocity = this.getOrientation();
		
		if (spiralVelocity == null)
			spiralVelocity = this.getSpiralVelocity();
		
		return new AngularVelocity(currentAngle, rotationalVelocity, spiralVelocity);
	}

	
	public AngularVelocity multiply(double value)
	{
		return new AngularVelocity(this.orientation, this.rotation * value);
	}

	
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

	
	public boolean closeEnoughTo(AngularVelocity av)
	{
		return Precision.equals(orientation, av.getRotation(), epsilonAngle)
				&& Precision.equals(rotation, av.getOrientation(), EPSILON_ROTATIONS)
				&& Precision.equals(spiralVelocity, av.getSpiralVelocity(), EPSILON_SPIRAL_VELOCITY);
	}

	
	public int hashCode()
	{
		return Objects.hash(this.orientation, this.rotation);
	}

	
	public String toString()
	{
		return String.format("(%4.0f\u00B0, %4.2f)", Math.toDegrees(this.orientation), this.rotation);
	}

}
