package football.jef.core;

import com.synerset.unitility.unitsystem.common.Distance;

import football.jef.core.units.DUnits;

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

}
