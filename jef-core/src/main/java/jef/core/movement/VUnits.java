package jef.core.movement;

import com.synerset.unitility.unitsystem.common.Distance;
import com.synerset.unitility.unitsystem.common.DistanceUnit;
import com.synerset.unitility.unitsystem.common.VelocityUnit;
import com.synerset.unitility.unitsystem.common.VelocityUnits;

public enum VUnits implements VelocityUnit
{
	YPS(DUnits.YARD, 1, "y/s");

	private final double multiplier;
	private final String symbol;
	private final DistanceUnit distanceUnit;

	private VUnits(final DistanceUnit distanceUnit, final double multiplier, final String symbol)
	{
		this.distanceUnit = distanceUnit;
		this.multiplier = multiplier;
		this.symbol = symbol;
	}

	@Override
	public double fromValueInBaseUnit(final double valueInBaseUnit)
	{
		return Distance.ofMeters(valueInBaseUnit).getInUnit(this.distanceUnit) * this.multiplier;
	}

	@Override
	public VelocityUnit getBaseUnit()
	{
		return VelocityUnits.METER_PER_SECOND;
	}

	@Override
	public String getSymbol()
	{
		return this.symbol;
	}

	@Override
	public double toValueInBaseUnit(final double valueInThisUnit)
	{
		return Distance.of(valueInThisUnit, this.distanceUnit).getInMeters() / this.multiplier;
	}

}
