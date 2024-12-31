package jef.core.units;

import java.util.Objects;

import org.apache.commons.math3.util.Precision;

import jef.core.Conversions;

public class AngularVelocity
{
	public static final double EPSILON_ANGLE = .02;
	public static final double EPSILON_ROTATIONS = .02;

	private double currentAngleInRadians;
	private double radiansPerSecond;
	private double spiralVelocity;

	public AngularVelocity()
	{
		currentAngleInRadians = 0;
		radiansPerSecond = 0;
		spiralVelocity = 0;
	}

	public AngularVelocity(double currentAngleInRadians, double radiansPerSecond)
	{
		this(currentAngleInRadians, radiansPerSecond, 0);
	}

	public AngularVelocity(double currentAngleInRadians, double radiansPerSecond, double spiralVelocity)
	{
		this.currentAngleInRadians = Conversions.normalizeAngle(currentAngleInRadians);
		this.radiansPerSecond = radiansPerSecond;
		this.spiralVelocity = spiralVelocity;
	}

	public boolean isCloseToZero()
	{
		return Math.abs(radiansPerSecond) < EPSILON_ROTATIONS;
	}

	public AngularVelocity adjust(double currentAngleInRadians, double radiansPerSecond, double spiralVelocity)
	{
		return new AngularVelocity(this.currentAngleInRadians + currentAngleInRadians,
				this.radiansPerSecond + radiansPerSecond, this.spiralVelocity + spiralVelocity);
	}

	public AngularVelocity adjust(AngularVelocity av)
	{
		return adjust(av.currentAngleInRadians, av.radiansPerSecond, av.spiralVelocity);
	}

	public boolean rotatingClockwise()
	{
		return this.radiansPerSecond < 0;
	}
	
	public boolean rotatingCounterClockwise()
	{
		return this.radiansPerSecond > 0;
	}
	
	public double getCurrentAngleInRadians()
	{
		return this.currentAngleInRadians;
	}

	public double getRadiansPerSecond()
	{
		return this.radiansPerSecond;
	}

	public double getSpiralVelocity()
	{
		return this.spiralVelocity;
	}

	public void setSpiralVelocity(double spiralVelocity)
	{
		this.spiralVelocity = spiralVelocity;
	}

	public AngularVelocity multiply(double value)
	{
		return new AngularVelocity(this.currentAngleInRadians, this.radiansPerSecond * value);
	}

	@Override
	public boolean equals(final Object obj)
	{
		if (this == obj)
			return true;

		if ((obj == null) || (this.getClass() != obj.getClass()))
			return false;

		final AngularVelocity other = (AngularVelocity) obj;

		return Precision.equals(currentAngleInRadians, other.currentAngleInRadians, EPSILON_ANGLE)
				&& Precision.equals(radiansPerSecond, other.radiansPerSecond, EPSILON_ROTATIONS);
	}

	@Override
	public int hashCode()
	{
		return Objects.hash(this.currentAngleInRadians, this.radiansPerSecond);
	}

	@Override
	public String toString()
	{
		return "AngularVelocity [currentAngleInRadians=" + this.currentAngleInRadians + ", radiansPerSecond="
				+ this.radiansPerSecond + "]";
	}

}
