package football.jef.core.units;

import java.util.Objects;

import org.apache.commons.math3.util.Precision;

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
		this.currentAngleInRadians = Precision.round(currentAngleInRadians, 2);
		this.radiansPerSecond = Precision.round(radiansPerSecond, 2);
	}

	public boolean isCloseToZero()
	{
		return Math.abs(radiansPerSecond) < EPSILON_ROTATIONS;
	}
	
	public AngularVelocity adjust(Double currentAngleInRadians, Double radiansPerSecond)
	{
		if (currentAngleInRadians == null)
			currentAngleInRadians = this.getCurrentAngleInRadians();
		
		if (radiansPerSecond == null)
			radiansPerSecond = this.getRadiansPerSecond();
		
		return new AngularVelocity(currentAngleInRadians, radiansPerSecond);
			
	}
	public double getCurrentAngleInRadians()
	{
		return this.currentAngleInRadians;
	}

	public double getRadiansPerSecond()
	{
		return this.radiansPerSecond;
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
