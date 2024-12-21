package football.jef.core.units;

import com.synerset.unitility.unitsystem.common.DistanceUnit;
import com.synerset.unitility.unitsystem.common.DistanceUnits;

public enum DUnits implements DistanceUnit
{
	YARD;

	private static final double CONVERSION_RATIO_YARDS_TO_METERS = 0.9144;

	@Override
	public double fromValueInBaseUnit(final double valueInBaseUnit)
	{
		return valueInBaseUnit / DUnits.CONVERSION_RATIO_YARDS_TO_METERS;
	}

	@Override
	public DistanceUnit getBaseUnit()
	{
		return DistanceUnits.METER;
	}

	@Override
	public String getSymbol()
	{
		return "Yd";
	}

	@Override
	public double toValueInBaseUnit(final double valueInThisUnit)
	{
		return valueInThisUnit * DUnits.CONVERSION_RATIO_YARDS_TO_METERS;
	}

}
