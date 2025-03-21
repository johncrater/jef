package jef.core;

import com.synerset.unitility.unitsystem.common.Distance;
import com.synerset.unitility.unitsystem.common.Velocity;

import jef.core.movement.DUnits;
import jef.core.movement.VUnits;

public class Conversions
{
	public static double yardsToMeters(double yards)
	{
		return Distance.of(yards, DUnits.YARD).getInMeters();
	}

	public static double metersToYards(double meters)
	{
		return Distance.ofMeters(meters).getInUnit(DUnits.YARD);
	}

	public static double yardsToInches(double yards)
	{
		return Distance.of(yards, DUnits.YARD).getInInches();
	}

	public static double inchesToYards(double inches)
	{
		return Distance.ofInches(inches).getInUnit(DUnits.YARD);
	}

	public static double roundToNearestQuarter(double value)
	{
		return Math.round(value * 4.0) / 4.0;
	}

	/**
	 * @param angle
	 * @return Normalize to the range [-π, π]
	 */
	public static double normalizeAngle(double angle)
	{
		angle = angle % (2 * Math.PI); // Normalize to the range [-2π, 2π]

		if (angle > Math.PI)
		{
			angle -= 2 * Math.PI;
		}
		else if (angle < -Math.PI)
		{
			angle += 2 * Math.PI;
		}

		return angle;
	}

	/**
	 * @param angle
	 * @return Normalize to the range [0, 2π]
	 */
	public static double normalizeAngle2PI(double angle)
	{
		angle = angle % (2 * Math.PI); // Normalize to the range [-2π, 2π]

		if (angle < 0)
		{
			angle = 2 * Math.PI + angle;
		}

		return angle;
	}

	public static double milesPerHourToYardsPerSecond(double d)
	{
		return Velocity.ofMilesPerHour(d).getInUnit(VUnits.YPS);
	}

}
