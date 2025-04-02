package jef.formations;

import jef.geometry.Direction;
import jef.geometry.Location;

public abstract class FormationCreator implements IFormationCreator
{
	private final String name;
	private final UnitType unitType;
	private Location scrimmage;
	private Direction direction;

	public FormationCreator(final String name, final UnitType unitType)
	{
		this.name = name;
		this.unitType = unitType;
	}

	@Override
	public Formation createFormation(Location scrimmage, Direction direction)
	{
		this.scrimmage = scrimmage;
		this.direction = direction;
		return new Formation(name, unitType);
	}

	@Override
	public String getName()
	{
		return this.name;
	}

	@Override
	public UnitType getUnitType()
	{
		return this.unitType;
	}

	protected Location getScrimmage()
	{
		return this.scrimmage;
	}

	protected Direction getDirection()
	{
		return this.direction;
	}

	protected Location createLocation(Side side, double x, double y)
	{
		if (getDirection() == Direction.west)
		{
			if (side == Side.LEFT)
				return new Location(x, y);
			else
				return new Location(x, -y);
		}
		else
		{
			if (side == Side.LEFT)
				return new Location(-x, -y);
			else
				return new Location(-x, y);
		}
	}
	
	
}
