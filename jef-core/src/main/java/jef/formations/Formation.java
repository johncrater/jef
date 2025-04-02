package jef.formations;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import jef.geometry.Direction;
import jef.geometry.Location;

public class Formation implements IFormation
{
	private final String name;
	private UnitType unitType;
	private final Map<String, FormationPosition> formationPositions = new HashMap<>();
	private Location scrimmage;
	private Direction direction;

	public Formation(final String name, UnitType unitType)
	{
		this.name = name;
		this.unitType = unitType;
	}

	@Override
	public Location getScrimmage()
	{
		return this.scrimmage;
	}

	@Override
	public Direction getDirection()
	{
		return this.direction;
	}

	@Override
	public UnitType getUnitType()
	{
		return this.unitType;
	}

	public IFormation addPosition(final FormationPosition pos)
	{
		this.formationPositions.put(pos.getName(), pos);
		return this;
	}

	@Override
	public Collection<FormationPosition> getFormationPositions()
	{
		return this.formationPositions.values();
	}
	
	@Override
	public Collection<String> positionNames()
	{
		return this.formationPositions.keySet();
	}
	
	@Override
	public FormationPosition getPosition(String name)
	{
		return this.formationPositions.get(name);
	}
	
	@Override
	public boolean isValid()
	{
		return formationPositions.size() == 11;
	}
	
	@Override
	public String getName()
	{
		return this.name;
	}
}