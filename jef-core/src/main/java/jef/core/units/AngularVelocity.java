package jef.core.units;

import java.util.Objects;

import org.apache.commons.math3.util.Precision;

import jef.core.Conversions;

public class AngularVelocity
{
	public static final double EPSILON_ANGLE = .01;
	public static final double EPSILON_ROTATIONS = .01;

	private double currentAngleInRadians;
	private double radiansPerSecond;

	public AngularVelocity()
	{
		currentAngleInRadians = 0;
		radiansPerSecond = 0;
	}

	public AngularVelocity(double currentAngleInRadians, double radiansPerSecond)
	{
		this.currentAngleInRadians = Conversions.normalizeAngle(currentAngleInRadians);
		this.radiansPerSecond = radiansPerSecond;
	}

	public boolean isCloseToZero()
	{
		return Math.abs(radiansPerSecond) < EPSILON_ROTATIONS;
	}

	public AngularVelocity adjust(double currentAngleInRadians, double radiansPerSecond)
	{
		return new AngularVelocity(this.currentAngleInRadians + currentAngleInRadians,
				this.radiansPerSecond + radiansPerSecond);

	}

	public double getCurrentAngleInRadians()
	{
		return this.currentAngleInRadians;
	}

	public double getRadiansPerSecond()
	{
		return this.radiansPerSecond;
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
