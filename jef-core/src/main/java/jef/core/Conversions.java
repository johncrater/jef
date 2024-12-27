package jef.core;

import com.synerset.unitility.unitsystem.common.Distance;

import jef.core.units.DUnits;

public interface Conversions
{
	public static double yardsToMeters(double yards)
	{
		return Distance.of(yards,  DUnits.YARD).getInMeters();
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

    public static double normalizeAngle(double angle) 
    {
        angle = angle % (2 * Math.PI); // Normalize to the range -2PI to 2PI

        if (angle > Math.PI) {
            angle -= 2 * Math.PI;
        } else if (angle < -Math.PI) {
            angle += 2 * Math.PI;
        }

        return angle;
    }}
