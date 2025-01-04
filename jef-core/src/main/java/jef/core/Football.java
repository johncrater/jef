package jef.core;

import com.synerset.unitility.unitsystem.common.Distance;

import jef.core.units.DUnits;

public interface Football extends Moveable
{
	public static final double lengthOfTheMajorAxis = Distance.ofInches(11.25f).getInUnit(DUnits.YARD);
	public static final double lengthOfTheMinorAxis = Distance.ofInches(6.70f).getInUnit(DUnits.YARD);

}
