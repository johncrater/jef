package jef.core;

import org.locationtech.jts.algorithm.Angle;
import org.locationtech.jts.geom.LineSegment;

import com.synerset.unitility.unitsystem.common.Distance;

import jef.core.movement.DUnits;
import jef.core.movement.Location;

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
        return Angle.normalize(angle);
    }
    
    public static LineSegment toLineSegment(Location loc1, Location loc2)
    {
    	return new LineSegment(loc1.toCoordinate(), loc2.toCoordinate());
    }
}
